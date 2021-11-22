package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

import java.util.List;

public class fitnessCalculator {

    public static int fitnessOfUtilitySystem(UtilitySystem utilitySystem) throws Exception {
        // == GAME SETTINGS ==
        String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
        UnitTypeTable utt = new UnitTypeTable(); // original game (NOOP length = move length)
        int MAX_GAME_CYCLES = 1000; // game time
        int MAX_INACTIVE_CYCLES = 1000;
        PhysicalGameState pgs = PhysicalGameState.load(scenarioFileName, utt);

        // == SETUP THE AI ==
        AI utilitySystemAI = new UtilitySystemAI(utt, utilitySystem, false);
        AI opponentAi = new PassiveAI();

        // == PLAY THE GAME ==
        GameState gs = RunExperimentTest.runUntilAtWarriorCount(utilitySystemAI, opponentAi, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 1);
        //GameState gs = RunExperimentTest.runExperiment(utilitySystemAI, opponentAi, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES);

        // == EVAL THE GAMESTATE ==
        //System.out.println("Time: " + gs.getTime());
        //System.out.println("Winner: " + gs.winner());

        int res = 1;
        // PUNISH THY FOOL FOR WINNING THE GAME!!
        if (gs.winner() != -1) {
            return res;
        }

//        //System.out.println(gs);
//        List<Unit> units = gs.getUnits();
//        for (Unit unit : units) {
//            if (unit.getPlayer() == 0) {
//                res += 5;
//            }
//        }


        //return res;
        return MAX_GAME_CYCLES - gs.getTime() + res;
    }
}
