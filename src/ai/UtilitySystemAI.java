/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import ai.utilitySystem.USVariable;
import ai.utilitySystem.UtilitySystem;
import ai.utilitySystem.*;
import ai.abstraction.*;
import ai.utilitySystem.USAction.UtilAction;

import java.util.*;

import rts.*;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 */
public class UtilitySystemAI extends AbstractionLayerAI {
    protected UtilitySystem us; // can we just pass it as param in constructor?
    protected UnitTypeTable utt;
    protected UnitType workerType;
    protected UnitType baseType;
    protected UnitType barracksType;
    protected UnitType lightType;
    protected UnitType heavyType;
    protected UnitType rangedType;

    protected List<Unit> passiveUnits = new LinkedList<Unit>();
    protected List<Unit> attackingUnits = new LinkedList<Unit>();
    protected List<Unit> defendingUnits = new LinkedList<Unit>();

    public UtilitySystemAI(UnitTypeTable a_utt, PathFinding pathfinding, int computationLimit, int iterationsLimit) {
        super(pathfinding, computationLimit, iterationsLimit);
        List<USVariable> variables = new ArrayList<USVariable>();
        List<USFeature> features = new ArrayList<USFeature>();
        List<USAction> actions = new ArrayList<USAction>();
        utt = a_utt;
        us = new UtilitySystem(variables, features, actions);
    }

    public UtilitySystemAI(UnitTypeTable a_utt, PathFinding a_pf) {
        this(a_utt, a_pf, -1, -1);
        reset(a_utt);
    }

    public UtilitySystemAI(UnitTypeTable a_utt) {
        this(a_utt, new AStarPathFinding());
        //reset(a_utt);
    }
    
    @Override
    public void reset() {
        super.reset();
    }

    public void reset(UnitTypeTable a_utt)
    {
        utt = a_utt;
        if (utt!=null) {
            workerType = utt.getUnitType("Worker");
            baseType = utt.getUnitType("Base");
            barracksType = utt.getUnitType("Barracks");
            lightType = utt.getUnitType("Light");
            heavyType = utt.getUnitType("Heavy");
            rangedType = utt.getUnitType("Ranged");

        }
    }


    @Override
    public AI clone() {
        return new UtilitySystemAI(utt, pf); // copy utility system aswell
    }
   
    
    @Override
    public PlayerAction getAction(int player, GameState gs) {
        try {
            //Do the translation stuff
            //return us.getActionWeightedRandom(gs, player);
            UtilAction utilAction = us.getActionWeightedRandom(gs, player);
            //PhysicalGameState pgs = gs.getPhysicalGameState();
            Player p = gs.getPlayer(player);
            for (Unit u:attackingUnits) {
                //gs.getUnitActions().remove(u); //Just to be sure that it stops it current action, and that it doesn't try to give a new action if it already has one
                attackClosestEnemy(u, p, gs);
            }
            for (Unit u:defendingUnits) {
                //gs.getUnitActions().remove(u); //Just to be sure that it stops it current action, and that it doesn't try to give a new action if it already has one
                DefendLogic(u, p, gs);
            }
            switch (utilAction) {
                case ATTACK_WITH_SINGLE_UNIT -> { return AttackWithSingleUnit(gs, p); }
                case DEFEND_WITH_SINGLE_UNIT -> { return DefendWithSingleUnit(gs, p); }
                case BUILD_BASE -> { return BuildBase(gs, p); }
                case BUILD_BARRACKS -> { return BuildBarracks(gs, p); }
                case BUILD_WORKER -> { return BuildWorker(gs, p); }
                case BUILD_WAR_UNIT -> { return BuildWarUnit(gs, p); }
                case HARVEST_RESOURCE -> { return Harvest_Resources(gs, p); }
                default -> { return translateActions(p.getID(),gs); }
            }
        }catch(Exception e) {
            return translateActions(player,gs);
        }
    }
    
    
    @Override
    public List<ParameterSpecification> getParameters()
    {
        return new ArrayList<>();
    }

    //TODO: Make list of available units for new actions

    //TODO: Choose random unit, done
    //TODO: Make a list of attacking units, done
    protected PlayerAction AttackWithSingleUnit(GameState gs, Player p){
        System.out.println("Attack With Single Unit");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> canAttack = new LinkedList<>();
        boolean warUnitAttacking = false;
        for(Unit u:pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest && u.getPlayer() == p.getID() &&
                    (gs.getActionAssignment(u)==null || passiveUnits.contains(u) || defendingUnits.contains(u))) {
                canAttack.add(u);
                //attackClosestEnemy(u,p,gs);
                warUnitAttacking = true;
            }
        }
        if(warUnitAttacking){
            Random rand = new Random();
            Unit u = canAttack.get(rand.nextInt(canAttack.size()));
            attackClosestEnemy(u,p,gs);
            attackingUnits.add(u);
            defendingUnits.remove(u);
            passiveUnits.remove(u);
        }
        //Use workers if there are no military units
        else if(!warUnitAttacking){
            for(Unit u:pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest && u.getPlayer() == p.getID() &&
                        (gs.getActionAssignment(u)==null || passiveUnits.contains(u) || defendingUnits.contains(u) || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_HARVEST || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_NONE )) {
                    canAttack.add(u);
                    //attackClosestEnemy(u,p,gs);
                    //break;
                }
            }
            Random rand = new Random();
            Unit u = canAttack.get(rand.nextInt(canAttack.size()));
            attackClosestEnemy(u,p,gs);
            attackingUnits.add(u);
            defendingUnits.remove(u);
            passiveUnits.remove(u);
        }
        return translateActions(p.getID(),gs);
    }
    void attackClosestEnemy(Unit u, Player p, GameState gs){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for(Unit u2:pgs.getUnits()) {
            if (u2.getPlayer()>=0 && u2.getPlayer()!=p.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestEnemy==null || d<closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        if (closestEnemy!=null) {
            attack(u,closestEnemy);
        }
    }

    //TODO: Gotta make a list of managed units that defend
    //TODO: Choose random unit
    protected PlayerAction DefendWithSingleUnit(GameState gs, Player p){
        System.out.println("Defend With Single Unit");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        //Use military units
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest
                    && u.getPlayer() == p.getID()
                    && gs.getActionAssignment(u) == null) {
                DefendLogic(u, p, gs);
                return translateActions(p.getID(),gs);
            }
        }
        //Use workers if no military units are available
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && u.getType().canHarvest
                    && u.getPlayer() == p.getID()
                    && gs.getActionAssignment(u) == null) {
                DefendLogic(u, p, gs);
                return translateActions(p.getID(),gs);
            }
        }
        return translateActions(p.getID(),gs);
    }
    void DefendLogic(Unit u, Player p, GameState gs){
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
            }
            else if(u2.getPlayer()==p.getID() && u2.getType() == baseType)
            {
                mybase = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                myBaseX = u2.getX();
                myBaseY = u2.getY();
            }
        }
        if (closestEnemy!=null && (closestDistance < pgs.getHeight()/2 || mybase < pgs.getHeight()/2)) {
            attack(u,closestEnemy);
        }
        else if(mybase >= pgs.getHeight()/2){
            move(u, myBaseX+1, myBaseY-1);
            move(u, myBaseX+1, myBaseY+1);
            move(u, myBaseX-1, myBaseY-1);
            move(u, myBaseX-1, myBaseY+1);
        }
        else
        {
            attack(u, null);
        }
    }

    //TODO: Choose random worker
    protected PlayerAction BuildBase(GameState gs, Player p){
        System.out.println("Build Base");
        //Setup of variables
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Unit> otherResources = new ArrayList<>(otherResourcePoint(p, pgs));
        List<Integer> reservedPositions = new ArrayList<>();
        int nbases = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                nbases++;
            }
        }
        //Build base if there are none left
        if (nbases == 0 && p.getResources() >= baseType.cost){
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (gs.getActionAssignment(u) == null
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_HARVEST
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_MOVE
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_NONE
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_RETURN) {
                        buildIfNotAlreadyBuilding(u, baseType, u.getX(), u.getY(), reservedPositions, p, pgs);
                        return translateActions(p.getID(),gs);
                    }
                }
            }
            //Use attacking worker to build base if none are left
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_ATTACK_LOCATION) {
                        buildIfNotAlreadyBuilding(u, baseType, u.getX(), u.getY(), reservedPositions, p, pgs);
                        return translateActions(p.getID(),gs);
                    }
                }
            }
        }
        //Expand behaviour
        if (!otherResources.isEmpty() && p.getResources() >= baseType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (gs.getActionAssignment(u) == null
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_HARVEST
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_MOVE
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_NONE
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_RETURN) {
                        buildIfNotAlreadyBuilding(u, baseType, otherResources.get(0).getX()-1, otherResources.get(0).getY()-1, reservedPositions, p, pgs);
                        return translateActions(p.getID(),gs);
                    }
                }
            }
            //Use Attacking worker if none other are available
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_ATTACK_LOCATION) {
                        buildIfNotAlreadyBuilding(u, baseType, otherResources.get(0).getX() - 1, otherResources.get(0).getY() - 1, reservedPositions, p, pgs);
                        return translateActions(p.getID(),gs);
                    }
                }
            }
        }
        return translateActions(p.getID(),gs);
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
        if(!bases.isEmpty()){
            return getOrderedResources(new ArrayList<>(otherResources), bases.get(0));
        }else{
            return new ArrayList<>(otherResources);
        }
    }
    protected List<Unit> getOrderedResources(List<Unit> resources, Unit base){
        List<Unit> resReturn = new ArrayList<>();

        HashMap<Integer, ArrayList<Unit>> map = new HashMap<>();
        for (Unit res : resources) {
            int d = Math.abs(res.getX() - base.getX()) + Math.abs(res.getY() - base.getY());
            if(map.containsKey(d)){
                ArrayList<Unit> nResourc = map.get(d);
                nResourc.add(res);
            }else{
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

    //TODO: Choose random worker
    protected PlayerAction BuildBarracks(GameState gs, Player p){
        System.out.println("Build Barracks");
        //Setup of variables
        PhysicalGameState pgs = gs.getPhysicalGameState();
        List<Integer> reservedPositions = new ArrayList<>();
        //Build barracks with resource worker
        if (p.getResources() >= barracksType.cost) {
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (gs.getActionAssignment(u) == null
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_HARVEST
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_MOVE
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_NONE
                            || gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_RETURN) {
                        buildIfNotAlreadyBuilding(u, barracksType, u.getX(), u.getY(), reservedPositions, p, pgs);
                        return translateActions(p.getID(),gs);
                    }
                }
            }
            //Use Attacking worker if none other are available
            for (Unit u : pgs.getUnits()) {
                if (u.getType() == workerType && u.getPlayer() == p.getID()) {
                    if (gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_ATTACK_LOCATION) {
                        buildIfNotAlreadyBuilding(u, barracksType, u.getX(), u.getY(), reservedPositions, p, pgs);
                        return translateActions(p.getID(),gs);
                    }
                }
            }
        }
        return translateActions(p.getID(),gs);
    }
    //TODO: Make random
    protected PlayerAction BuildWorker(GameState gs, Player p){
        System.out.println("Build Worker");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==baseType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=workerType.cost){
                    train(u, workerType);
                    break;
                }
                else if(p.getResources()<workerType.cost) break;
            }
        }
        return translateActions(p.getID(),gs);
    }

    //TODO: Make random to choose
    protected PlayerAction BuildWarUnit(GameState gs, Player p){
        System.out.println("Build War Unit");
        Random ran = new Random();
        int val = ran.nextInt(3);
        if(val == 0) BuildLight(gs, p);
        else if(val == 1) BuildHeavy(gs, p);
        else if(val == 2) BuildRanged(gs, p);

        return translateActions(p.getID(),gs);
    }

    //TODO: Make to choose randomly
    //TODO: Make it able to harvest several resource (look at economyrush)
    protected PlayerAction Harvest_Resources(GameState gs, Player p){
        System.out.println("Harvest Resource");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        //Only takes workers who are idling and makes them harvest
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==workerType && u.getPlayer()==p.getID()) {
                if(gs.getActionAssignment(u) == null){
                    Unit closestBase = null;
                    Unit closestResource = null;
                    //Find closest resources
                    int closestDistance = 0;
                    for (Unit u2 : pgs.getUnits()) {
                        if (u2.getType().isResource) {
                            int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                            if (closestResource == null || d < closestDistance) {
                                closestResource = u2;
                                closestDistance = d;
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
                    break;
                }
            }
        }
        return translateActions(p.getID(),gs);
    }

    protected void BuildLight(GameState gs, Player p){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==barracksType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=lightType.cost){
                    train(u, lightType);
                    break;
                }
                else if(p.getResources()<lightType.cost) break;
            }
        }
    }
    protected void BuildHeavy(GameState gs, Player p){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==barracksType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=heavyType.cost){
                    train(u, heavyType);
                    break;
                }
                else if(p.getResources()<heavyType.cost) break;
            }
        }
    }
    protected void BuildRanged(GameState gs, Player p){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==barracksType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=rangedType.cost){
                    train(u, rangedType);
                    break;
                }
                else if(p.getResources()<heavyType.cost) break;
            }
        }
    }

}
