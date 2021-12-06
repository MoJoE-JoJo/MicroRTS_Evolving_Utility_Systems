/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import ai.abstraction.AbstractAction;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Harvest;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import ai.utilitySystem.*;
import ai.utilitySystem.USAction.UtilAction;
import rts.*;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

import java.util.*;

/**
 *
 */
public class UtilitySystemAI extends AbstractionLayerAI {
    protected UtilitySystem utilitySystem; // can we just pass it as param in constructor?
    protected UnitTypeTable utt;
    protected UnitType workerType;
    protected UnitType baseType;
    protected UnitType barracksType;
    protected UnitType lightType;
    protected UnitType heavyType;
    protected UnitType rangedType;

    protected HashSet<Unit> passiveUnits;
    protected HashSet<Unit> harvestingWorkers;
    protected HashSet<Unit> buildingWorkers;
    protected HashSet<Unit> attackingUnits;
    protected HashSet<Unit> defendingUnits;
    protected boolean useMaxUtil = false;
    protected boolean verbose = true;

    public UtilitySystemAI(UnitTypeTable a_utt, PathFinding pathfinding, int computationLimit, int iterationsLimit) {
        super(pathfinding, computationLimit, iterationsLimit);
        List<USVariable> variables = new ArrayList<USVariable>();
        List<USFeature> features = new ArrayList<USFeature>();
        List<USAction> actions = new ArrayList<USAction>();
        List<USConstant> constants = new ArrayList<USConstant>();
        passiveUnits = new HashSet<>();
        harvestingWorkers = new HashSet<>();
        buildingWorkers = new HashSet<>();
        attackingUnits = new HashSet<>();
        defendingUnits = new HashSet<>();
        utt = a_utt;
        utilitySystem = new UtilitySystem(variables, features, actions, constants);
    }

    public UtilitySystemAI(UnitTypeTable a_utt, PathFinding a_pf) {
        this(a_utt, a_pf, -1, -1);
        reset(a_utt);
        utilitySystem = StaticUtilitySystems.getBaselineUtilitySystem();
    }

    public UtilitySystemAI(UnitTypeTable a_utt) {
        this(a_utt, new AStarPathFinding());
        reset(a_utt);
    }

    public UtilitySystemAI(UnitTypeTable a_utt, UtilitySystem us, boolean verbose) {
        this(a_utt, new AStarPathFinding());
        reset(a_utt);
        utilitySystem = us;
        this.verbose = verbose;
    }

    public UtilitySystemAI(UnitTypeTable a_utt, UtilitySystem us, boolean verbose, boolean useMaxUtil) {
        this(a_utt, new AStarPathFinding());
        reset(a_utt);
        utilitySystem = us;
        this.verbose = verbose;
        this.useMaxUtil = useMaxUtil;
    }


    @Override
    public void reset() {
        super.reset();
    }

    public void reset(UnitTypeTable a_utt) {
        utt = a_utt;
        if (utt != null) {
            workerType = utt.getUnitType("Worker");
            baseType = utt.getUnitType("Base");
            barracksType = utt.getUnitType("Barracks");
            lightType = utt.getUnitType("Light");
            heavyType = utt.getUnitType("Heavy");
            rangedType = utt.getUnitType("Ranged");

        }
        passiveUnits = new HashSet<>();
        harvestingWorkers = new HashSet<>();
        buildingWorkers = new HashSet<>();
        attackingUnits = new HashSet<>();
        defendingUnits = new HashSet<>();
    }


    @Override
    public AI clone() {
        return new UtilitySystemAI(utt, utilitySystem, verbose, useMaxUtil); // copy utility system aswell
    }


    @Override
    public PlayerAction getAction(int player, GameState gs) {
        try {
            //Remove dead units
            HashSet<Unit> liveUnits = new HashSet<>(gs.getUnits());
            passiveUnits.removeIf(u -> !liveUnits.contains(u));
            attackingUnits.removeIf(u -> !liveUnits.contains(u));
            defendingUnits.removeIf(u -> !liveUnits.contains(u));
            harvestingWorkers.removeIf(u -> !liveUnits.contains(u));
            buildingWorkers.removeIf(u -> !liveUnits.contains(u));

            //Make all units that are doing nothing part of the passive set
            for (Unit u : gs.getUnits()) {
                if ((gs.getActionAssignment(u) == null || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_NONE) &&
                        !attackingUnits.contains(u) &&
                        !defendingUnits.contains(u) &&
                        !harvestingWorkers.contains(u)){
                    passiveUnits.add(u);
                    harvestingWorkers.remove(u);
                    buildingWorkers.remove(u);
                }
            }
            //Do the translation stuff

            UnitGroups unitGroups = new UnitGroups(passiveUnits, harvestingWorkers, buildingWorkers, attackingUnits, defendingUnits);
            List<UtilAction> utilActions;
            UtilAction utilAction;
            if(!useMaxUtil){
                utilActions = utilitySystem.getActionWeightedRandom(gs, player, unitGroups);
                utilAction = utilActions.get(0);
            }
            else{
                utilAction = utilitySystem.getActionBest(gs, player, unitGroups);
            }
            //utilActions = new ArrayList<>();
            //utilActions.add(utilAction);
            Player p = gs.getPlayer(player);
            for (Unit u : attackingUnits) {
                //gs.getUnitActions().remove(u); //Just to be sure that it stops it current action, and that it doesn't try to give a new action if it already has one
                attackClosestEnemy(u, p, gs);
            }
            for (Unit u : defendingUnits) {
                //gs.getUnitActions().remove(u); //Just to be sure that it stops it current action, and that it doesn't try to give a new action if it already has one
                DefendLogic(u, p, gs);
            }
            for (Unit u : harvestingWorkers) {
                //gs.getUnitActions().remove(u); //Just to be sure that it stops it current action, and that it doesn't try to give a new action if it already has one
                HarvestLogic(u, p, gs);
            }
            switch (utilAction) {
                case ATTACK_WITH_SINGLE_UNIT -> {
                    if(AttackWithSingleUnit(gs, p)) return translateActions(p.getID(), gs);
                }
                case DEFEND_WITH_SINGLE_UNIT -> {
                    if(DefendWithSingleUnit(gs, p)) return translateActions(p.getID(), gs);
                }
                case BUILD_BASE -> {
                    if(BuildBase(gs, p)) return translateActions(p.getID(), gs);
                }
                case BUILD_BARRACKS -> {
                    if(BuildBarracks(gs, p)) return translateActions(p.getID(), gs);
                }
                case BUILD_WORKER -> {
                    if(BuildWorker(gs, p)) return translateActions(p.getID(), gs);
                }
                case BUILD_LIGHT -> {
                    if(BuildLight(gs, p)) return translateActions(p.getID(), gs);
                }
                case BUILD_RANGED -> {
                    if(BuildRanged(gs, p)) return translateActions(p.getID(), gs);
                }
                case BUILD_HEAVY -> {
                    if(BuildHeavy(gs, p)) return translateActions(p.getID(), gs);
                }
                case HARVEST_RESOURCE -> {
                    if(Harvest_Resources(gs, p)) return translateActions(p.getID(), gs);
                }
            }
            return translateActions(p.getID(), gs);
        } catch (Exception e) {
            System.out.println(e);
            return translateActions(player, gs);
        }
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

    protected boolean AttackWithSingleUnit(GameState gs, Player p) {
        if (verbose) System.out.println("Attack With Single Unit");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canAttack = new LinkedList<>();
        //Check passive military units
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest && u.getPlayer() == p.getID() && passiveUnits.contains(u)) {
                canAttack.add(u);
            }
        }
        //Check defending military units
        if (canAttack.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && !u.getType().canHarvest && u.getPlayer() == p.getID() && defendingUnits.contains(u)) {
                    canAttack.add(u);
                }
            }
        }
        //Check passive workers
        if (canAttack.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() && passiveUnits.contains(u)) {
                    canAttack.add(u);
                }
            }
        }
        //Check defending workers
        if (canAttack.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() && defendingUnits.contains(u)) {
                    canAttack.add(u);
                }
            }
        }
        //Check harvesting workers
        if (canAttack.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() && harvestingWorkers.contains(u)) {
                    canAttack.add(u);
                }
            }
        }
        if (canAttack.size() > 0) {
            Random rand = new Random();
            Unit u = canAttack.get(rand.nextInt(canAttack.size()));
            attackClosestEnemy(u, p, gs);
            attackingUnits.add(u);
            defendingUnits.remove(u);
            passiveUnits.remove(u);
            harvestingWorkers.remove(u);
            //return true;
        }
        //return false;
        return true;
    }

    void attackClosestEnemy(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        if (closestEnemy != null) {
            attack(u, closestEnemy);
        }
    }

    protected boolean DefendWithSingleUnit(GameState gs, Player p) {
        if (verbose) System.out.println("Defend With Single Unit");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canDefend = new LinkedList<>();
        //Check passive military units
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest && u.getPlayer() == p.getID() && passiveUnits.contains(u)) {
                canDefend.add(u);
            }
        }
        //Check attacking military units
        if (canDefend.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && !u.getType().canHarvest && u.getPlayer() == p.getID() && attackingUnits.contains(u)) {
                    canDefend.add(u);
                }
            }
        }
        //Check passive workers
        if (canDefend.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() && passiveUnits.contains(u)) {
                    canDefend.add(u);
                }
            }
        }
        //Check defending workers
        if (canDefend.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() && attackingUnits.contains(u)) {
                    canDefend.add(u);
                }
            }
        }
        //Check harvesting workers
        if (canDefend.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() && harvestingWorkers.contains(u)) {
                    canDefend.add(u);
                }
            }
        }
        if (canDefend.size() > 0) {
            Random rand = new Random();
            Unit u = canDefend.get(rand.nextInt(canDefend.size()));
            DefendLogic(u, p, gs);
            attackingUnits.remove(u);
            defendingUnits.add(u);
            passiveUnits.remove(u);
            harvestingWorkers.remove(u);
            //return true;
        }
        //return false;
        return true;
    }

    void DefendLogic(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        int mybase = 0;
        int myBaseX = 0;
        int myBaseY = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            } else if (u2.getPlayer() == p.getID() && u2.getType() == baseType) {
                mybase = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                myBaseX = u2.getX();
                myBaseY = u2.getY();
            }
        }
        if (closestEnemy != null && (closestDistance < pgs.getHeight() / 2 || mybase < pgs.getHeight() / 2)) {
            attack(u, closestEnemy);
        } else if (mybase >= pgs.getHeight() / 2) {
            move(u, myBaseX + 1, myBaseY - 1);
            move(u, myBaseX + 1, myBaseY + 1);
            move(u, myBaseX - 1, myBaseY - 1);
            move(u, myBaseX - 1, myBaseY + 1);
        } else {
            attack(u, null);
        }
    }

    protected boolean BuildBase(GameState gs, Player p) {
        if (verbose) System.out.println("Build Base");
        //Setup of variables
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> otherResources = new ArrayList<>(otherResourcePoint(p, pgs));
        List<Integer> reservedPositions = new ArrayList<>();
        List<Unit> canBuild = new LinkedList<Unit>();
        int nBases = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                nBases++;
            }
        }
        //Build base if there are none left
        if (nBases == 0 && p.getResources() >= baseType.cost) {
            //Check passive worker
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (passiveUnits.contains(u)) canBuild.add(u);
                }
            }
            //Check harvesting workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (harvestingWorkers.contains(u)) canBuild.add(u);
                    }
                }
            }
            //Check defending workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (defendingUnits.contains(u)) canBuild.add(u);
                    }
                }
            }
            //Check attacking workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (attackingUnits.contains(u)) canBuild.add(u);
                    }
                }
            }
            if (canBuild.size() > 0) {
                Random rand = new Random();
                Unit u = canBuild.get(rand.nextInt(canBuild.size()));
                buildIfNotAlreadyBuilding(u, baseType, u.getX(), u.getY(), reservedPositions, p, pgs);
                attackingUnits.remove(u);
                defendingUnits.remove(u);
                passiveUnits.remove(u);
                harvestingWorkers.remove(u);
                buildingWorkers.add(u);
                return true;
            }
        }
        //Expand behaviour
        if (!otherResources.isEmpty() && nBases > 0 && p.getResources() >= baseType.cost) {
            //check passive worker
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (passiveUnits.contains(u)) canBuild.add(u);
                }
            }
            //Check harvesting workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (harvestingWorkers.contains(u)) canBuild.add(u);
                    }
                }
            }
            //Check defending workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (defendingUnits.contains(u)) canBuild.add(u);

                    }
                }
            }
            //Check attacking workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (attackingUnits.contains(u)) canBuild.add(u);
                    }
                }
            }
            if (canBuild.size() > 0) {
                Random rand = new Random();
                Unit u = canBuild.get(rand.nextInt(canBuild.size()));
                buildIfNotAlreadyBuilding(u, baseType, otherResources.get(0).getX() - 1, otherResources.get(0).getY() - 1, reservedPositions, p, pgs);
                attackingUnits.remove(u);
                defendingUnits.remove(u);
                passiveUnits.remove(u);
                harvestingWorkers.remove(u);
                buildingWorkers.add(u);
                return true;
            }
        }
        return false;
    }

    protected List<Unit> otherResourcePoint(Player p, PhysicalGameState pgs) {

        List<Unit> bases = getMyBases(p, pgs);
        Set<Unit> myResources = new HashSet<>();
        Set<Unit> otherResources = new HashSet<>();

        for (Unit base : bases) {
            List<Unit> closestUnits = new ArrayList<>(pgs.getUnitsAround(base.getX(), base.getY(), 10));
            for (Unit closestUnit : closestUnits) {
                if (closestUnit.getType().isResource) {
                    myResources.add(closestUnit);
                }
            }
        }

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().isResource) {
                if (!myResources.contains(u2)) {
                    otherResources.add(u2);
                }
            }
        }
        if (!bases.isEmpty()) {
            return getOrderedResources(new ArrayList<>(otherResources), bases.get(0));
        } else {
            return new ArrayList<>(otherResources);
        }
    }

    protected List<Unit> getOrderedResources(List<Unit> resources, Unit base) {
        List<Unit> resReturn = new ArrayList<>();

        HashMap<Integer, ArrayList<Unit>> map = new HashMap<>();
        for (Unit res : resources) {
            int d = Math.abs(res.getX() - base.getX()) + Math.abs(res.getY() - base.getY());
            if (map.containsKey(d)) {
                ArrayList<Unit> nResourc = map.get(d);
                nResourc.add(res);
            } else {
                ArrayList<Unit> nResourc = new ArrayList<>();
                nResourc.add(res);
                map.put(d, nResourc);
            }
        }
        ArrayList<Integer> keysOrdered = new ArrayList<>(map.keySet());
        Collections.sort(keysOrdered);

        for (Integer key : keysOrdered) {
            resReturn.addAll(map.get(key));

        }

        return resReturn;

    }

    protected List<Unit> getMyBases(Player p, PhysicalGameState pgs) {

        List<Unit> bases = new ArrayList<>();
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                bases.add(u2);
            }
        }
        return bases;
    }

    protected boolean BuildBarracks(GameState gs, Player p) {
        if (verbose) System.out.println("Build Barracks");
        //Setup of variables
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Integer> reservedPositions = new ArrayList<>();
        List<Unit> canBuild = new LinkedList<Unit>();
        //Build barracks with passive worker
        if (p.getResources() >= barracksType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (passiveUnits.contains(u)) canBuild.add(u);
                }
            }
            //Check harvesting workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID())
                        if (harvestingWorkers.contains(u)) canBuild.add(u);
                }
            }
            //Check defending workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID())
                        if (defendingUnits.contains(u)) canBuild.add(u);
                }
            }
            //Check attacking workers
            if (canBuild.size() == 0) {
                for (Unit u : pgs.getUnits()) {
                    if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                        if (attackingUnits.contains(u)) canBuild.add(u);
                    }
                }
            }
            if (canBuild.size() > 0) {
                Random rand = new Random();
                Unit u = canBuild.get(rand.nextInt(canBuild.size()));
                Random danr = new Random();
                int posX = danr.nextInt(-1, 2);
                int posY = danr.nextInt(-1, 2);

                buildIfNotAlreadyBuilding(u, barracksType, u.getX()+posX, u.getY()+posY, reservedPositions, p, pgs);

                attackingUnits.remove(u);
                defendingUnits.remove(u);
                passiveUnits.remove(u);
                harvestingWorkers.remove(u);
                buildingWorkers.add(u);
                return true;
            }
        }
        return false;
    }

    protected boolean BuildWorker(GameState gs, Player p) {
        if (verbose) System.out.println("Build Worker");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canTrain = new LinkedList<Unit>();
        // behavior of bases:
        if (p.getResources() >= workerType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == baseType && u.getPlayer() == p.getID() && gs.getActionAssignment(u) == null) {
                    canTrain.add(u);
                }
            }
            if (canTrain.size() > 0) {
                Random rand = new Random();
                Unit u = canTrain.get(rand.nextInt(canTrain.size()));
                train(u, workerType);
                return true;
            }
        }
        return false;
    }

//    protected boolean BuildWarUnit(GameState gs, Player p) {
//        if (verbose) System.out.println("Build War Unit");
//        Random ran = new Random();
//        int val = ran.nextInt(3);
//        if (val == 0) BuildLight(gs, p);
//        else if (val == 1) BuildHeavy(gs, p);
//        else if (val == 2) BuildRanged(gs, p);
//
//        return translateActions(p.getID(), gs);
//    }

    protected boolean Harvest_Resources(GameState gs, Player p) {
        if (verbose) System.out.println("Harvest Resource");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canHarvest = new LinkedList<>();
        //Only takes workers who are idling and makes them harvest
        for (Unit u : pgs.getUnits()) {
            if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                if (passiveUnits.contains(u)) canHarvest.add(u);
            }
        }
        //defending workers
        if (canHarvest.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (defendingUnits.contains(u)) canHarvest.add(u);
                }
            }
        }
        //attacking workers
        if (canHarvest.size() == 0) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (attackingUnits.contains(u)) canHarvest.add(u);
                }
            }
        }
        if (canHarvest.size() > 0) {
            Random rand = new Random();
            Unit u = canHarvest.get(rand.nextInt(canHarvest.size()));
            HarvestLogic(u, p, gs);
            attackingUnits.remove(u);
            defendingUnits.remove(u);
            passiveUnits.remove(u);
            harvestingWorkers.add(u);
            //return true;
        }
        //return false;
        return true;
    }

    void HarvestLogic(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestBase = null;
        Unit closestResource = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().isResource) {
                if (pf.pathToPositionInRangeExists(u, u2.getPosition(pgs), 1, gs, gs.getResourceUsage())) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestResource == null || d < closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
        }
        //Find closest base
        closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().isStockpile && u2.getPlayer() == p.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestBase == null || d < closestDistance) {
                    closestBase = u2;
                    closestDistance = d;
                }
            }
        }
        //This block should make it so that they can get a new target to harvest and return to
        if (closestResource != null && closestBase != null) {
            AbstractAction aa = getAbstractAction(u);
            if (aa instanceof Harvest) {
                Harvest h_aa = (Harvest) aa;
                if (h_aa.getTarget() != closestResource || h_aa.getBase() != closestBase) {
                    harvest(u, closestResource, closestBase);
                }
            } else {
                harvest(u, closestResource, closestBase);
            }
        }
    }

    protected boolean BuildLight(GameState gs, Player p) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canTrain = new LinkedList<Unit>();
        // behavior of bases:
        if (p.getResources() >= lightType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == barracksType && u.getPlayer() == p.getID() && gs.getActionAssignment(u) == null) {
                    canTrain.add(u);
                }
            }
        }
        if (canTrain.size() > 0) {
            Random rand = new Random();
            Unit u = canTrain.get(rand.nextInt(canTrain.size()));
            train(u, lightType);
            return true;
        }
        return false;
    }

    protected boolean BuildHeavy(GameState gs, Player p) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canTrain = new LinkedList<Unit>();
        // behavior of bases:
        if (p.getResources() >= heavyType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == barracksType && u.getPlayer() == p.getID() && gs.getActionAssignment(u) == null) {
                    canTrain.add(u);
                }
            }
        }
        if (canTrain.size() > 0) {
            Random rand = new Random();
            Unit u = canTrain.get(rand.nextInt(canTrain.size()));
            train(u, heavyType);
            return true;
        }
        return false;
    }

    protected boolean BuildRanged(GameState gs, Player p) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canTrain = new LinkedList<Unit>();

        // behavior of bases:
        if (p.getResources() >= rangedType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == barracksType && u.getPlayer() == p.getID() && gs.getActionAssignment(u) == null) {
                    canTrain.add(u);
                }
            }
        }
        if (canTrain.size() > 0) {
            Random rand = new Random();
            Unit u = canTrain.get(rand.nextInt(canTrain.size()));
            train(u, rangedType);
            return true;
        }
        return false;
    }

}
