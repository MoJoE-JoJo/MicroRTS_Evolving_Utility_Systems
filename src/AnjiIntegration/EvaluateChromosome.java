package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.abstraction.*;
import ai.core.AI;
import ai.utilitySystem.StaticUtilitySystems;
import ai.utilitySystem.UtilitySystem;
import com.anji.util.Properties;
import org.jgap.Chromosome;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.ExperimenterAsymmetric;
import tests.RunExperimentTest;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class EvaluateChromosome {

    private static List<AI> opponentBots = new LinkedList<>();
    private static String map;
    private static fitnessCalculator.OpponentTypes opponentType;
    private static fitnessCalculator.GameTypes gametype;
    private static int maxGameCycles;
    private static int maxInactiveCycles;
    private static boolean takeMaxAction;
    private static int gameGoalCount;
    private static UtilitySystem utilSystem;
    private static UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED);
    private static PrintStream csvResult;

    public static void main(String[] args){
        // two arguments:
        // [0] the properties files for the training/evolving
        // [1] the champion chromosome XML file example -> chromosome1234.xml

        try {

            // read the first argument as the properties file
            Properties prop = new Properties(args[0]);
            //Properties props = new Properties("utility_system_properties/test_1.properties");

            // read second argument as the champion chromosome file.
            anjiConverter conv = new anjiConverter();
            InputStream inputStream = conv.readXmlFileIntoInputStream(args[1]);
            //InputStream inputStream = conv.readXmlFileIntoInputStream("./db/chromosome/chromosome" + 8659 + ".xml");
            UtilitySystem util = conv.toUtilitySystemFromInputStream(inputStream);

            eval(prop, util);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void eval(Properties props, UtilitySystem championSystem) {
        try {
            // build CSV result path
            String runName = props.getProperty("run.name");
            String filepath = "./storage/" + runName;
            csvResult = new PrintStream(new File(filepath + "/" + "evalResults.csv"));
            // set the util system
            utilSystem = championSystem;

            // === SETUP GAME BASED ON PROPERTY FILE ===

            map = props.getProperty("fitness.game.map");
            opponentType = fitnessCalculator.OpponentTypes.valueOf(props.getProperty("fitness.game.opponent.evaluate"));
            gametype = fitnessCalculator.GameTypes.valueOf(props.getProperty("fitness.game.type"));
            maxGameCycles = props.getIntProperty("fitness.game.cycles.max");
            maxInactiveCycles = props.getIntProperty("fitness.game.cycles.inactive.max");
            takeMaxAction = props.getBooleanProperty("utility.system.take.max.action");

            if (!gametype.equals(fitnessCalculator.GameTypes.NORMAL)) {
                // read the game goal property
                gameGoalCount = props.getIntProperty("fitness.game.goal");
            }

            // == SETUP THE OPPONENT BOTS ==
            opponentBots = new LinkedList<>();
            switch (opponentType) {
                case PASSIVE:
                    opponentBots.add(new PassiveAI());
                    break;
                case ROUND_ROBIN:
                    opponentBots.add(new WorkerRush(utt));
                    opponentBots.add(new LightRush(utt));
                    opponentBots.add(new HeavyRush(utt));
                    opponentBots.add(new RangedRush(utt));
                    opponentBots.add(new WorkerDefense(utt));
                    opponentBots.add(new LightDefense(utt));
                    opponentBots.add(new HeavyDefense(utt));
                    opponentBots.add(new RangedDefense(utt));
                    break;
                case BASELINE:
                    opponentBots.add(new UtilitySystemAI(utt, StaticUtilitySystems.getBaselineUtilitySystem(), false));
                    break;
                case ROUND_ROBIN_AND_BASELINE:
                    opponentBots.add(new UtilitySystemAI(utt, StaticUtilitySystems.getBaselineUtilitySystem(), false));
                    opponentBots.add(new WorkerRush(utt));
                    opponentBots.add(new LightRush(utt));
                    opponentBots.add(new HeavyRush(utt));
                    opponentBots.add(new RangedRush(utt));
                    opponentBots.add(new WorkerDefense(utt));
                    opponentBots.add(new LightDefense(utt));
                    opponentBots.add(new HeavyDefense(utt));
                    opponentBots.add(new RangedDefense(utt));
                    break;
                case default:
                    throw new Exception("unknown evaluate opponent types");
            }

            // == EVAL DIFFERENTLY BASED ON GAMETYPE ==

            switch (gametype) {
                case NORMAL:
                    evaluateNormalGames();
                    break;
                case HARVEST:
                    evaluateResourceGathering();
                    break;
                case MILITIA_UNITS:
                    evaluateMilitiaUnits();
                    break;
                default:
                    throw new Exception("invalid gametype, in EvaluateChromosome");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void evaluateNormalGames() {

        try {

            List<PhysicalGameState> pgs = new LinkedList<>();
            pgs.add(PhysicalGameState.load(map, utt));

            // == SETUP THE AI ==
            List<AI> bots1 = new LinkedList<>();
            bots1.add(new UtilitySystemAI(utt, utilSystem, false));

            // == PLAY THE GAME ==
            ExperimenterAsymmetric.runExperiments(bots1, opponentBots,
                    pgs, utt, 100, 5000, 300, false, csvResult, false, false, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void evaluateMilitiaUnits() {
        int score = 0;
        int falseWins = 0;
        for (int i = 0; i < 100; i++) {
            try {

                // setup AI and game
                PhysicalGameState pgs = null;
                pgs = PhysicalGameState.load(map, utt);

                // == SETUP THE AI ==
                AI utilitySystemAI = new UtilitySystemAI(utt, utilSystem, false);
                AI opponentAi = opponentBots.get(0);

                // == PLAY THE GAME ==
                GameState gs = RunExperimentTest.runUntilAtWarriorCount(utilitySystemAI, opponentAi, pgs, utt, maxGameCycles, maxInactiveCycles, 0, gameGoalCount);

                // == EVAL THE GAMESTATE ==
                // here print out the time it takes to reach the end.

                if (gs.winner() == 0) {
                    falseWins++;
                }

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
        System.out.println("Amount of 'false wins', where the utility system won by killing the enemy");
        System.out.println(falseWins);


    }

    public static void evaluateResourceGathering() {
        int score = 0;
        int falseWins = 0;
        for (int i = 0; i < 100; i++) {
            try {

                // setup AI and game
                PhysicalGameState pgs = null;
                pgs = PhysicalGameState.load(map, utt);

                // == SETUP THE AI ==
                AI utilitySystemAI = new UtilitySystemAI(utt, utilSystem, false);
                AI opponentAi = opponentBots.get(0);

                // == PLAY THE GAME ==
                GameState gs = RunExperimentTest.runUntilAtResourceCount(utilitySystemAI, opponentAi, pgs, utt, maxGameCycles, maxInactiveCycles, 0, gameGoalCount);

                // == EVAL THE GAMESTATE ==
                // here print out the time it takes to reach the end.

                if (gs.winner() == 0) {
                    falseWins++;
                }

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
        System.out.println("Amount of 'false wins', where the utility system won by killing the enemy");
        System.out.println(falseWins);

    }
}
