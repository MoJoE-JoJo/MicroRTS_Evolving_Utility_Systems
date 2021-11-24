package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.WorkerRush;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

import java.util.List;

public class fitnessCalculator {

    public static int fitnessOfUtilitySystem(UtilitySystem utilitySystem, boolean isPlayerZero) throws Exception {
        // == GAME SETTINGS ==
        String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
        UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED); // original game (NOOP length = move length)
        int MAX_GAME_CYCLES = 5000; // game time
        int MAX_INACTIVE_CYCLES = 5000;
        PhysicalGameState pgs = PhysicalGameState.load(scenarioFileName, utt);

        // == SETUP THE AIs ==
        AI playerZero;
        AI PlayerOne;
        int playerId;
        if (isPlayerZero) {
            playerZero = new UtilitySystemAI(utt, utilitySystem, false);
            playerId = 0;
            PlayerOne = new LightRush(utt);
        } else {
            playerZero = new LightRush(utt);
            PlayerOne = new UtilitySystemAI(utt, utilitySystem, false);
            playerId = 1;
        }


        // == PLAY THE GAME ==

        //GameState gs = RunExperimentTest.runUntilAtResourceCount(playerZero, PlayerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 10);
        //GameState gs = RunExperimentTest.runUntilAtWarriorCount(playerZero, PlayerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 5);
        GameState gs = RunExperimentTest.runExperiment(playerZero, PlayerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES);

        // == EVAL THE GAMESTATE ==
        //System.out.println("Time: " + gs.getTime());
        //System.out.println("Winner: " + gs.winner());

        int res = MAX_GAME_CYCLES + 1000;

        if (gs.winner() == playerId) // win
        {
            return res - gs.getTime();
        } else if (gs.winner() == -1) //tie
        {
            return 1000;
        } else //else lost the game, reward based on survival time
        {
            return gs.getTime() / 10;
        }

//        //count units as and reward 5 pr
//        List<Unit> units = gs.getUnits();
//        for (Unit unit : units) {
//            if (unit.getPlayer() == 0) {
//                res += 5;
//            }
//        }


        //return res;
        //return MAX_GAME_CYCLES - gs.getTime() + res;
        //return MAX_GAME_CYCLES - gs.getTime() + res;
    }
}
