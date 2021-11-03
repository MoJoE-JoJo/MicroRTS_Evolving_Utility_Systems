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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rts.*;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 */
public class UtilitySystemAI extends AbstractionLayerAI {
    private UtilitySystem us; // can we just pass it as param in constructor?
    protected UnitTypeTable utt;
    UnitType workerType;
    UnitType baseType;
    UnitType barracksType;
    UnitType lightType;
    UnitType heavyType;
    UnitType rangedType;

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
            switch (utilAction) {
                case ATTACK_WITH_SINGLE_UNIT -> { return AttackWithSingleUnit(gs, p); }
                case DEFEND_WITH_SINGLE_UNIT -> { return DefendWithSingleUnit(gs, p); }
                case BUILD_BASE -> { return BuildBase(gs, p); }
                case BUILD_BARRACKS -> { return BuildBarracks(gs, p); }
                case BUILD_WORKER -> { return BuildWorker(gs, p); }
                case BUILD_WAR_UNIT -> { return BuildWarUnit(gs, p); }
                case HARVEST_RESOURCE -> { return Harvest_Resources(gs, p); }
                default -> { return new PlayerAction(); }
            }
        }catch(Exception e) {
            return new PlayerAction();
        }
    }
    
    
    @Override
    public List<ParameterSpecification> getParameters()
    {
        return new ArrayList<>();
    }

    PlayerAction AttackWithSingleUnit(GameState gs, Player p){
        System.out.println("Attack With Single Unit");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        boolean warUnitAttacking = false;
        for(Unit u:pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest &&
                    u.getPlayer() == p.getID() &&
                    gs.getActionAssignment(u)==null) {
                attackClosestEnemy(u,p,gs);
                warUnitAttacking = true;
                break;
            }
        }
        if(!warUnitAttacking){
            for(Unit u:pgs.getUnits()) {
                if (u.getType().canAttack && u.getType().canHarvest &&
                        u.getPlayer() == p.getID() &&
                        (gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_HARVEST ||
                                gs.getActionAssignment(u).action.getType() == UnitAction.TYPE_NONE)) {
                    attackClosestEnemy(u,p,gs);
                    break;
                }
            }
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

    PlayerAction DefendWithSingleUnit(GameState gs, Player p){
        System.out.println("Defend With Single Unit");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest
                    && u.getPlayer() == p.getID()
                    && gs.getActionAssignment(u) == null) {
                DefendLogic(u, p, gs);
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

    PlayerAction BuildBase(GameState gs, Player p){
        System.out.println("Build Base");
        return new PlayerAction();
    }

    PlayerAction BuildBarracks(GameState gs, Player p){
        System.out.println("Build Barracks");
        return new PlayerAction();
    }
    PlayerAction BuildWorker(GameState gs, Player p){
        System.out.println("Build Worker");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==baseType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=workerType.cost) train(u, workerType);
                else if(p.getResources()<workerType.cost) break;
            }
        }
        return translateActions(p.getID(),gs);
    }

    PlayerAction BuildWarUnit(GameState gs, Player p){
        System.out.println("Build War Unit");
        Random ran = new Random();
        int val = ran.nextInt(3);
        if(val == 0) BuildLight(gs, p);
        else if(val == 1) BuildHeavy(gs, p);
        else if(val == 2) BuildRanged(gs, p);

        return translateActions(p.getID(),gs);
    }

    PlayerAction Harvest_Resources(GameState gs, Player p){
        System.out.println("Harvest Resource");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==workerType && u.getPlayer()==p.getID()) {
                if(gs.getActionAssignment(u) == null){
                    //Do something
                    //break;
                }
            }
        }
        return translateActions(p.getID(),gs);
    }

    void BuildLight(GameState gs, Player p){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==barracksType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=lightType.cost) train(u, lightType);
                else if(p.getResources()<lightType.cost) break;
            }
        }
    }
    void BuildHeavy(GameState gs, Player p){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==barracksType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=heavyType.cost) train(u, heavyType);
                else if(p.getResources()<heavyType.cost) break;
            }
        }
    }
    void BuildRanged(GameState gs, Player p){
        PhysicalGameState pgs = gs.getPhysicalGameState();
        // behavior of bases:
        for(Unit u:pgs.getUnits()) {
            if (u.getType()==barracksType && u.getPlayer()==p.getID() && gs.getActionAssignment(u)==null) {
                if (p.getResources()>=heavyType.cost) train(u, heavyType);
                else if(p.getResources()<heavyType.cost) break;
            }
        }
    }

}
