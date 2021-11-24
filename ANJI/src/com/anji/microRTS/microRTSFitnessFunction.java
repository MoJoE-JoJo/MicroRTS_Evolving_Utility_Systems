package com.anji.microRTS;

import AnjiIntegration.anjiConverter;
import AnjiIntegration.fitnessCalculator;
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
    private final static int MAX_FITNESS = 1000000;

    private Random rand;

    public microRTSFitnessFunction() {
        super();
        System.out.println("YOLO");
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

            // TODO play microRTS and get a fitness score
            int fitness = 0;
            int iterations = 3;
            try {
                System.out.println("Calculating fitness for chromosome -> " + chrom.getId());
                for (int i = 0; i < iterations; i++) {
                    fitness += fitnessCalculator.fitnessOfUtilitySystem(US);
                }
                fitness /= iterations;
                System.out.println("Fitness -> " + fitness);
            } catch (Exception e) {
                e.printStackTrace();
            }

            chrom.setFitnessValue(fitness);
        }
    }

    @Override
    public int getMaxFitnessValue() {
        return 5000;
    }

    @Override
    public void init(Properties props) throws Exception {
        Randomizer r = (Randomizer) props.singletonObjectProperty(Randomizer.class);
        rand = r.getRand();
    }
}
