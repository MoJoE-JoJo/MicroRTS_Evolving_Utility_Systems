package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.abstraction.*;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import com.anji.microRTS.microRTSFitnessFunction;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

public class fitnessCalculator {

    public static int fitnessOfUtilitySystem(UtilitySystem utilitySystem, int iteration, int opponentAI) throws Exception {
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
        //decide if player 1 or 2, for swapping positions on the board since it can give some advantages
        boolean isPlayerZero = iteration % 2 == 0;
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

    public static int coEvolutionFitness(UtilitySystem utilitySystem, UtilitySystem prevChampion, int iteration) throws Exception {
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

        //decide if player 1 or 2, for swapping positions on the board since it can give some advantages
        boolean isPlayerZero = iteration % 2 == 0;
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


    private static AI selectAIFromOpponentType(UnitTypeTable utt, microRTSFitnessFunction.OpponentTypes type, int iteration) {
        switch (type) {
            case PASSIVE:
                return new PassiveAI(utt);
            case ROUND_ROBIN:
                return selectOpponentAI(utt, iteration);
            case COEVOLUTION:
            case default:
                return null;
        }
    }

    private static AI selectOpponentAI(UnitTypeTable utt, int iteration) {

        
        switch (iteration % 8) {
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

    public static int calcFitness(UtilitySystem utilitySystem, int iteration, microRTSFitnessFunction.OpponentTypes opponentType, microRTSFitnessFunction.GameTypes gametype, int gameGoalCount) throws Exception {
        // == SETUP GAME BASED ON PARAMETERS ==
        String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
        UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED); // original game (NOOP length = move length)
        int MAX_GAME_CYCLES = 5000; // game time
        int MAX_INACTIVE_CYCLES = 5000;
        PhysicalGameState pgs = PhysicalGameState.load(scenarioFileName, utt);

        // == SETUP THE AIs ==
        AI playerZero;
        AI playerOne;
        int playerId;
        boolean isPlayerZero = iteration % 2 == 0;

        if (isPlayerZero) {
            playerZero = new UtilitySystemAI(utt, utilitySystem, false);
            playerOne = selectAIFromOpponentType(utt, opponentType, iteration);
            playerId = 0;
        } else {
            playerZero = selectAIFromOpponentType(utt, opponentType, iteration);
            playerOne = new UtilitySystemAI(utt, utilitySystem, false);
            playerId = 1;
        }

        GameState gs;
        switch (gametype) {
            case NORMAL:
                // run the game
                gs = RunExperimentTest.runNormalGame(playerZero, playerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES);

                // eval gamestate: small rewards for time survived in lost game, bigger rewards when winning the game fast.

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

            case HARVEST:
                // run the game
                gs = RunExperimentTest.runUntilAtResourceCount(playerZero, playerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, playerId, gameGoalCount);

                // eval gamestate: if US_AI killed enemy AI reward 1, else it gets a higher score the quicker it gets the resource amount

                if (gs.winner() == playerId) // utility system AI won
                {
                    return 1;
                }

                return MAX_GAME_CYCLES - gs.getTime() + 5;
            case MILITIA_UNITS:
                // run the game
                gs = RunExperimentTest.runUntilAtWarriorCount(playerZero, playerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, playerId, gameGoalCount);

                if (gs.winner() == playerId) // win
                {
                    int resourceCount = gs.getPhysicalGameState().getPlayer(playerId).getResources();
                    return resourceCount * 5; // 5 pr resource cause why not.
                }

                return MAX_GAME_CYCLES - gs.getTime();
            default:
                throw new Exception("invalid gametype, in fitnessCalculator");
        }
    }
}
