package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

public class fitnessCalculator {

    public static int fitnessOfUtilitySystem(UtilitySystem utilitySystem) throws Exception {
        // == GAME SETTINGS ==
        String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
        UnitTypeTable utt = new UnitTypeTable(); // original game (NOOP length = move length)
        int MAX_GAME_CYCLES = 3000; // game time
        int MAX_INACTIVE_CYCLES = 3000;
        PhysicalGameState pgs = PhysicalGameState.load(scenarioFileName, utt);

        // == SETUP THE AI ==
        AI utilitySystemAI = new UtilitySystemAI(utt, utilitySystem);
        AI ai3 = new PassiveAI();

        // == PLAY THE GAME ==
        GameState gs = RunExperimentTest.runUntilAtResourceCount(utilitySystemAI, ai3, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 10);

        // == EVAL GAMESTATE ==
        System.out.println("Time: " + gs.getTime());
        System.out.println("Winner: " + gs.winner());
        System.out.println(gs);

        return 0;
    }
}
