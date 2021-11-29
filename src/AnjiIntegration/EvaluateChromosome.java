package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

public class EvaluateChromosome {

    public static void main(String[] args) {


        int score = 0;
        for (int i = 0; i < 100; i++) {

            try {
                // build utility system
                anjiConverter anjioCon = new anjiConverter();
                UtilitySystem utilitySystem = anjioCon.toUtilitySystemFromChromosome(8409);

                // setup AI and game
                // == GAME SETTINGS ==
                String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
                UnitTypeTable utt = new UnitTypeTable(); // original game (NOOP length = move length)
                int MAX_GAME_CYCLES = 5000; // game time
                int MAX_INACTIVE_CYCLES = 5000;
                PhysicalGameState pgs = null;
                pgs = PhysicalGameState.load(scenarioFileName, utt);

                // == SETUP THE AI ==
                AI utilitySystemAI = new UtilitySystemAI(utt, utilitySystem, false);
                AI opponentAi = new PassiveAI();

                // == PLAY THE GAME ==
                //GameState gs = RunExperimentTest.runUntilAtWarriorCount(utilitySystemAI, opponentAi, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 0, 1);
                //

                GameState gs = RunExperimentTest.runUntilAtResourceCount(utilitySystemAI, opponentAi, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 0, 15);

                // == EVAL THE GAMESTATE ==
                int time = gs.getTime();
                System.out.println(time);
                score += time;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        score = score / 100; // get average time
        System.out.println("final average time");
        System.out.println(score);
    }
}
