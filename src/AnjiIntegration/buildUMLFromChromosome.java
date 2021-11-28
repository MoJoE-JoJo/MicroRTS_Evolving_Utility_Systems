package AnjiIntegration;

import ai.utilitySystem.UtilitySystem;

public class buildUMLFromChromosome {

    public static void main(String[] args) {
        try {
            UtilitySystem us = anjiConverter.toUtilitySystemFromChromosome(8348);

            String uml = us.toPlantUML();
            System.out.println(uml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
