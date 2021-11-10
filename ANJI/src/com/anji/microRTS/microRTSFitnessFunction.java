package com.anji.microRTS;

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


    @Override
    public void evaluate(List genotypes) {
        Iterator it = genotypes.iterator();
        while ( it.hasNext() ) {
            Chromosome chrom = (Chromosome) it.next();
            //TODO build utility system, and play microRTS.


            // currently, just copy of random fitness.

            int randomFitness = rand.nextInt( MAX_FITNESS );
            chrom.setFitnessValue( randomFitness + 1 );
        }
    }

    @Override
    public int getMaxFitnessValue() {
        return 20;
    }

    @Override
    public void init(Properties props) throws Exception {
        Randomizer r = (Randomizer) props.singletonObjectProperty( Randomizer.class );
        rand = r.getRand();
    }
}
