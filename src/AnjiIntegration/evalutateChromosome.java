package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

public class evalutateChromosome {

    public static void main(String[] args) {

        try {
            // build utility system
            UtilitySystem utilitySystem = anjiConverter.toUtilitySystemFromChromosome(69);

            // setup AI and game
            // == GAME SETTINGS ==
            String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
            UnitTypeTable utt = new UnitTypeTable(); // original game (NOOP length = move length)
            int MAX_GAME_CYCLES = 1000; // game time
            int MAX_INACTIVE_CYCLES = 1000;
            PhysicalGameState pgs = null;
            pgs = PhysicalGameState.load(scenarioFileName, utt);

            // == SETUP THE AI ==
            AI utilitySystemAI = new UtilitySystemAI(utt, utilitySystem, true);
            AI opponentAi = new PassiveAI();

            // == PLAY THE GAME ==
            GameState gs = RunExperimentTest.runUntilAtWarriorCount(utilitySystemAI, opponentAi, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 1);

            // == EVAL THE GAMESTATE ==
            System.out.println("Time: " + gs.getTime());
            System.out.println("Winner: " + gs.winner());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
