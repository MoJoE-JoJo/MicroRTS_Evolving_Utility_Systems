package com.anji.microRTS;

import AnjiIntegration.anjiConverter;
import AnjiIntegration.fitnessCalculator;
import ai.utilitySystem.USConstants;
import ai.utilitySystem.UtilitySystem;
import com.anji.integration.XmlPersistableChromosome;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class microRTSFitnessFunction implements BulkFitnessFunction, Configurable {
    private final static int MAX_FITNESS = 6000;
    private int iterations;
    private boolean doCoEvolution;

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
                US.setRandom(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (US == null) {
                //set the fitness to 0 if it failed to build the utility system
                chrom.setFitnessValue(0);
                continue;
            }

            int fitness = 0;
            try {
                for (int i = 0; i < iterations; i++) {
                    boolean playerOne = i % 2 == 0;
                    if (doCoEvolution) {
                        fitness += fitnessCalculator.coEvolutionFitness(US, prevChampion, playerOne);
                    } else {
                        fitness += fitnessCalculator.fitnessOfUtilitySystem(US, playerOne, i);
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
        iterations = props.getIntProperty("fitness.iterations", 10);
        doCoEvolution = props.getBooleanProperty("utility.system.co.evolution", false);

        if (doCoEvolution) {
            prevChampion = USConstants.getRandomUtilitySystem();
            championFitness = 0;
        }
    }
}
