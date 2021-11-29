package com.anji.microRTS;

import AnjiIntegration.anjiConverter;
import AnjiIntegration.fitnessCalculator;
import ai.utilitySystem.StaticUtilitySystems;
import ai.utilitySystem.UtilitySystem;
import com.anji.integration.XmlPersistableChromosome;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import java.util.Iterator;
import java.util.List;


public class microRTSFitnessFunction implements BulkFitnessFunction, Configurable {
    private final static int MAX_FITNESS = 6000;
    private int iterations;
    private boolean doCoEvolution = false;

    public enum GameTypes {
            HARVEST,
            MILITIA_UNITS,
            NORMAL
    }

    public enum OpponentTypes {
        PASSIVE,
        COEVOLUTION,
        ROUND_ROBIN
    }

    private GameTypes gametype;
    private int gameGoalCount;
    private OpponentTypes opponentType;

    // co evolution stuff
    private UtilitySystem prevChampion;
    private int championFitness;


    public microRTSFitnessFunction() {
        super();
        //System.out.println("constructor called");
    }

    @Override
    public void evaluate(List genotypes) {
        Iterator it = genotypes.iterator();
        while (it.hasNext()) {
            Chromosome chrom = (Chromosome) it.next();


            // build utility system
            UtilitySystem US = null;
            try {
                var xmlString = new XmlPersistableChromosome(chrom).toXml();
//                System.out.println("Building utility system");
                US = anjiConverter.toUtilitySystemFromXMLString(xmlString);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (US == null) {
                //set the fitness to 0 if it failed to build the utility system
                chrom.setFitnessValue(0);
                continue;
            }


            // === CALC FITNESS ===
            int fitness = 0;
            try {
                for (int i = 0; i < iterations; i++) {

                    if (doCoEvolution) {
                        fitness += fitnessCalculator.coEvolutionFitness(US, prevChampion, i);
                    } else {
                        fitness += fitnessCalculator.calcFitness(US, i, opponentType, gametype, gameGoalCount);
                    }
                }


                fitness /= iterations; // get the avg fitness

                if (doCoEvolution) //if doing co-evolution, see if need of updating champion
                {
                    if (fitness > championFitness) {
                        championFitness = fitness;
                        prevChampion = US;
                    }
                }
                System.out.println("chromosome id -> " + chrom.getId() + ", Fitness score -> " + fitness);
            } catch (Exception e) {
                e.printStackTrace();
            }

            chrom.setFitnessValue(fitness);
        }
    }

    @Override
    public int getMaxFitnessValue() {
        return MAX_FITNESS;
    }

    @Override
    public void init(Properties props) throws Exception {
        iterations = props.getIntProperty("fitness.iterations");
        opponentType = OpponentTypes.valueOf(props.getProperty("fitness.game.opponent"));
        gametype = GameTypes.valueOf(props.getProperty("fitness.game.type"));

        //TODO can add more game settings here if wanted like maps, gametime and so on.

        if (!gametype.equals(GameTypes.NORMAL))
        {
            // read the game goal property
            gameGoalCount = props.getIntProperty("fitness.game.goal");
        }

        if (opponentType.equals(OpponentTypes.COEVOLUTION)) {
            // if doing co-evolution, set initial champ and champ fitness
            prevChampion = StaticUtilitySystems.getRandomUtilitySystem();
            championFitness = 0;
            doCoEvolution = true;
        }

    }
}
