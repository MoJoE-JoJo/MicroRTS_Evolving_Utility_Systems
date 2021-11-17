/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.utilitySystem.*;

import rts.units.UnitTypeTable;

public class UtilitySystemAIRandom extends UtilitySystemAI {
    public UtilitySystemAIRandom(UnitTypeTable a_utt, PathFinding pathfinding, int computationLimit, int iterationsLimit) {
        super(a_utt, pathfinding, computationLimit, iterationsLimit);
    }

    public UtilitySystemAIRandom(UnitTypeTable a_utt, PathFinding a_pf) {
        this(a_utt, a_pf, -1, -1);
        reset(a_utt);
        utilitySystem = USConstants.getRandomUtilitySystem();
    }

    public UtilitySystemAIRandom(UnitTypeTable a_utt) {
        this(a_utt, new AStarPathFinding());
        reset(a_utt);
        utilitySystem = USConstants.getRandomUtilitySystem();
    }
}
