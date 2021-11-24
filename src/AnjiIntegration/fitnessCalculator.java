package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.abstraction.*;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

import java.util.List;

public class fitnessCalculator {

    public static int fitnessOfUtilitySystem(UtilitySystem utilitySystem, boolean isPlayerZero, int opponentAI) throws Exception {
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
            PlayerOne = selectOpponentAI(utt, opponentAI);
        } else {
            playerZero = selectOpponentAI(utt, opponentAI);
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

    public static int coEvolutionFitness(UtilitySystem utilitySystem, UtilitySystem prevChampion, boolean isPlayerZero) throws Exception {
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
            PlayerOne = new UtilitySystemAI(utt, prevChampion, false);
        } else {
            playerZero = new UtilitySystemAI(utt, prevChampion, false);
            PlayerOne = new UtilitySystemAI(utt, utilitySystem, false);
            playerId = 1;
        }

        // == PLAY THE GAME ==

        GameState gs = RunExperimentTest.runExperiment(playerZero, PlayerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES);


        // == EVAL THE FINAL GAMESTATE ==
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
    }

    private static AI selectOpponentAI(UnitTypeTable utt, int opponentAI) {

        switch (opponentAI) {
            case 0:
                return new WorkerRush(utt);
            case 1:
                return new WorkerDefense(utt);
            case 2:
                return new LightRush(utt);
            case 3:
                return new LightDefense(utt);
            case 4:
                return new RangedRush(utt);
            case 5:
                return new RangedDefense(utt);
            case 6:
                return new HeavyRush(utt);
            case 7:
                return new HeavyDefense(utt);
            default:
                return new PassiveAI(utt);
        }
    }

}
