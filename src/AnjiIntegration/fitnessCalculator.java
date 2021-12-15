package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.abstraction.*;
import ai.core.AI;
import ai.utilitySystem.StaticUtilitySystems;
import ai.utilitySystem.UtilitySystem;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

public class fitnessCalculator {


    private int MAX_GAME_CYCLES;
    private int MAX_INACTIVE_CYCLES;
    private UnitTypeTable utt;
    private String map;
    private OpponentTypes opponentType;
    private GameTypes gameType;
    private boolean takeMaxAction;
    private int gameGoalCount;

    public enum GameTypes {
        HARVEST,
        MILITIA_UNITS,
        NORMAL
    }

    public enum OpponentTypes {
        PASSIVE,
        COEVOLUTION,
        ROUND_ROBIN,
        BASELINE,
        ROUND_ROBIN_AND_BASELINE,
        COEVOLUTION_AND_ROUND_ROBIN,
        COEVOLUTION_AND_BASELINE,
    }

    public fitnessCalculator(int maxGameCycles, int maxInactiveCycles, String map, OpponentTypes opponentType, GameTypes gameType, int goalCount, boolean takeMaxAction) {
        MAX_GAME_CYCLES = maxGameCycles;
        MAX_INACTIVE_CYCLES = maxInactiveCycles;
        this.map = map;
        this.opponentType = opponentType;
        this.gameType = gameType;
        this.gameGoalCount = goalCount;
        this.takeMaxAction = takeMaxAction;
        utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED);
    }

    @Deprecated
    public int fitnessOfUtilitySystem(UtilitySystem utilitySystem, int iteration, int opponentAI, String map) throws Exception {
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
            PlayerOne = selectRoundRobinOpponentAI(utt, opponentAI);
        } else {
            playerZero = selectRoundRobinOpponentAI(utt, opponentAI);
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

    public int coEvolutionFitness(UtilitySystem utilitySystem, UtilitySystem prevChampion, int iteration) throws Exception {
        // == init game ==
        PhysicalGameState pgs = PhysicalGameState.load(map, utt);

        // == SETUP THE AIs ==
        AI playerZero;
        AI PlayerOne;
        int playerId;

        //decide if player 0 or 1, for swapping positions on the board since it can give some advantages
        boolean isPlayerZero = iteration % 2 == 0;
        if (isPlayerZero) {
            playerZero = new UtilitySystemAI(utt, utilitySystem, false, takeMaxAction);
            playerId = 0;
            PlayerOne = new UtilitySystemAI(utt, prevChampion, false, takeMaxAction);
        } else {
            playerZero = new UtilitySystemAI(utt, prevChampion, false, takeMaxAction);
            PlayerOne = new UtilitySystemAI(utt, utilitySystem, false, takeMaxAction);
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
        } else //else lost the game, reward based on survival time, divided by 10 to never out weight a win or tie
        {
            return gs.getTime() / 10;
        }
    }

    public int calcFitness(UtilitySystem utilitySystem, int iteration) throws Exception {
        // init game

        PhysicalGameState pgs = PhysicalGameState.load(map, utt);

        // == SETUP THE AIs ==
        AI playerZero;
        AI playerOne;
        int playerId;
        boolean isPlayerZero = iteration % 2 == 0;

        if (isPlayerZero) {
            playerZero = new UtilitySystemAI(utt, utilitySystem, false, takeMaxAction);
            playerOne = selectAIFromOpponentType(utt, opponentType, iteration);
            playerId = 0;
        } else {
            playerZero = selectAIFromOpponentType(utt, opponentType, iteration);
            playerOne = new UtilitySystemAI(utt, utilitySystem, false, takeMaxAction);
            playerId = 1;
        }

        GameState gs;
        switch (gameType) {
            case NORMAL:
                // run the game
                gs = RunExperimentTest.runNormalGame(playerZero, playerOne, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES);

                // eval gamestate: small rewards for time survived in lost game, bigger rewards when winning the game fast.

                int res = MAX_GAME_CYCLES + 1000;

                if (gs.winner() == playerId) // win: can be between [1000,6000]
                {
                    return res - gs.getTime();
                } else if (gs.winner() == -1) //tie: always returns [1000]
                {
                    return 1000;
                } else //else lost the game, reward based on survival time are in the range [0, 500]
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

    private static AI selectAIFromOpponentType(UnitTypeTable utt, OpponentTypes type, int iteration) {
        switch (type) {
            case PASSIVE:
                return new PassiveAI(utt);
            case ROUND_ROBIN:
            case COEVOLUTION_AND_ROUND_ROBIN:
                return selectRoundRobinOpponentAI(utt, iteration);
            case COEVOLUTION_AND_BASELINE:
            case BASELINE:
                return new UtilitySystemAI(utt, StaticUtilitySystems.getBaselineUtilitySystem(), false);
            case ROUND_ROBIN_AND_BASELINE:
                if (iteration == 9) {
                    return new UtilitySystemAI(utt, StaticUtilitySystems.getBaselineUtilitySystem(), false);
                } else {
                    return selectRoundRobinOpponentAI(utt, iteration);
                }
            case COEVOLUTION: // co-evolution is handled in an outer layer
                return null;
        }
        return null;
    }

    private static AI selectRoundRobinOpponentAI(UnitTypeTable utt, int iteration) {

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
}
