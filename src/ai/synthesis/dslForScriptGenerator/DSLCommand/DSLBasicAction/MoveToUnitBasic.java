/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.synthesis.dslForScriptGenerator.DSLCommand.DSLBasicAction;

import ai.synthesis.dslForScriptGenerator.DSLCommand.DSLEnumerators.EnumPlayerTarget;
import ai.synthesis.dslForScriptGenerator.IDSLParameters.IParameters;
import ai.synthesis.dslForScriptGenerator.DSLParametersConcrete.PlayerTargetParam;
import ai.abstraction.pathfinding.PathFinding;
import ai.synthesis.dslForScriptGenerator.DSLCommand.AbstractBasicAction;
import ai.synthesis.dslForScriptGenerator.DSLCommandInterfaces.IUnitCommand;
import java.util.HashMap;
import java.util.HashSet;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens & Julian
 */
public class MoveToUnitBasic extends AbstractBasicAction implements IUnitCommand {

    boolean needUnit = false;

    @Override
    public PlayerAction getAction(GameState game, int player, PlayerAction currentPlayerAction, PathFinding pf, UnitTypeTable a_utt, HashMap<Long, String> counterByFunction) {

        ResourceUsage resources = new ResourceUsage();
        PhysicalGameState pgs = game.getPhysicalGameState();
        //update variable resources
        resources = getResourcesUsed(currentPlayerAction, pgs);
        PlayerTargetParam p = getPlayerTargetFromParam();
        EnumPlayerTarget enumPlayer = p.getSelectedPlayerTarget().get(0);
        String pt = enumPlayer.name();
        int playerTarget = -1;
        if (pt == "Ally") {
            playerTarget = player;
        }
        if (pt == "Enemy") {
            playerTarget = 1 - player;
        }
        for (Unit unAlly : getPotentialUnits(game, currentPlayerAction, player)) {

            //pick one enemy unit to set the action
            Unit targetEnemy = getTargetEnemyUnit(game, currentPlayerAction, playerTarget, unAlly);

            if (game.getActionAssignment(unAlly) == null && unAlly != null && targetEnemy != null) {

                UnitAction uAct = null;
                UnitAction move = pf.findPathToPositionInRange(unAlly, targetEnemy.getX() + targetEnemy.getY() * pgs.getWidth(), unAlly.getAttackRange(), game, resources);
                if (move != null && game.isUnitActionAllowed(unAlly, move)){
                    uAct = move;
                }

                if (uAct != null && (uAct.getType() == 5 || uAct.getType() == 1)) {
                    setHasDSLUsed();
                    if (counterByFunction.containsKey(unAlly.getID())) {
                        if (!counterByFunction.get(unAlly.getID()).equals("moveToUnit")) {
                            counterByFunction.put(unAlly.getID(), "moveToUnit");
                        }
                    } else {
                        counterByFunction.put(unAlly.getID(), "moveToUnit");
                    }
                    currentPlayerAction.addUnitAction(unAlly, uAct);
                    resources.merge(uAct.resourceUsage(unAlly, pgs));
                }
            }
        }
        return currentPlayerAction;
    }

    @Override
    public String toString() {
        String listParam = "Params:{";
        for (IParameters parameter : getParameters()) {
            listParam += parameter.toString() + ",";
        }
        //remove the last comma.
        listParam = listParam.substring(0, listParam.lastIndexOf(","));
        listParam += "}";

        return "{MoveToUnitBasic:{" + listParam + "}}";
    }

    public void setUnitIsNecessary() {
        this.needUnit = true;
    }

    public void setUnitIsNotNecessary() {
        this.needUnit = false;
    }

    @Override
    public Boolean isNecessaryUnit() {
        return needUnit;
    }

    @Override
    public PlayerAction getAction(GameState game, int player, PlayerAction currentPlayerAction, PathFinding pf, UnitTypeTable a_utt, Unit unAlly, HashMap<Long, String> counterByFunction) {
        //usedCommands.add(getOriginalPieceGrammar()+")");
        if (unAlly != null && currentPlayerAction.getAction(unAlly) != null &&
                unAlly.getPlayer() != player) {
            return currentPlayerAction;
        }
        ResourceUsage resources = new ResourceUsage();
        PhysicalGameState pgs = game.getPhysicalGameState();
        //update variable resources
        resources = getResourcesUsed(currentPlayerAction, pgs);
        PlayerTargetParam p = getPlayerTargetFromParam();
        EnumPlayerTarget enumPlayer = p.getSelectedPlayerTarget().get(0);
        String pt = enumPlayer.name();
        int playerTarget = -1;
        if (pt == "Ally") {
            playerTarget = player;
        }
        if (pt == "Enemy") {
            playerTarget = 1 - player;
        }

        //pick one enemy unit to set the action
        Unit targetEnemy = getTargetEnemyUnit(game, currentPlayerAction, playerTarget, unAlly);        
        if (game.getActionAssignment(unAlly) == null && unAlly != null && targetEnemy != null && hasInPotentialUnits(game, currentPlayerAction, unAlly, player)) {
            UnitAction uAct = null;
            UnitAction move = pf.findPathToPositionInRange(unAlly, targetEnemy.getX() + targetEnemy.getY() * pgs.getWidth(), unAlly.getAttackRange(), game, resources);
            if (move != null && game.isUnitActionAllowed(unAlly, move)){
                uAct = move;
            }
            if (uAct != null && (uAct.getType() == 5 || uAct.getType() == 1)) {
                setHasDSLUsed();
                if (counterByFunction.containsKey(unAlly.getID())) {
                    if (!counterByFunction.get(unAlly.getID()).equals("moveToUnit")) {
                        counterByFunction.put(unAlly.getID(), "moveToUnit");
                    }
                } else {
                    counterByFunction.put(unAlly.getID(), "moveToUnit");
                }
                currentPlayerAction.addUnitAction(unAlly, uAct);
                resources.merge(uAct.resourceUsage(unAlly, pgs));
            }

        }

        return currentPlayerAction;
    }

}
