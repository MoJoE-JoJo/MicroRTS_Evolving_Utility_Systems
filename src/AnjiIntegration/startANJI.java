package AnjiIntegration;

import ai.utilitySystem.UtilitySystem;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJI {


    public static void main(String[] args) {
        System.out.println("Starting ANJI test");
        runAnji();
        //buildUtilitySystemFromChromosome(8817);

    }

    private static void buildUtilitySystemFromChromosome(int id) {
        try {
            UtilitySystem us = anjiConverter.toUtilitySystemFromChromosome(id);
            String uml = us.toPlantUML();
            System.out.println(uml);
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void runAnji() {
        try {
            Properties props = new Properties("utility_system_stage_1.properties");
            Evolver evolver = new Evolver();
            evolver.init(props);
            evolver.run();
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

