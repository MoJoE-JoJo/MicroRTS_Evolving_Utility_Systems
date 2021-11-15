package AnjiIntegration;

import ai.utilitySystem.UtilitySystem;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJI {


    public static void main(String[] args) {
        System.out.println("Starting ANJI test");
        tryAndRunAnji();
        //tryAndBuildFromChromosome();

    }

    private static void tryAndBuildFromChromosome() {

        try {
            UtilitySystem us = anjiConverter.toUtilitySytemFromChromosome(13198);
            System.out.println(us.toPlantUML());
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tryAndRunAnji() {
        try {
            Properties props = new Properties("utility_system.properties");
            Evolver evolver = new Evolver();
            evolver.init(props);
            evolver.run();
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

