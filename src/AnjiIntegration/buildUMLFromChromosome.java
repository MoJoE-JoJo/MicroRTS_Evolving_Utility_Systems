package AnjiIntegration;

import ai.utilitySystem.UtilitySystem;

public class buildUMLFromChromosome {

    public static void main(String[] args) {
        try {
            anjiConverter anjiCon = new anjiConverter();
            UtilitySystem us = anjiCon.toUtilitySystemFromChromosome(427);

            String uml = us.toPlantUML();
            System.out.println(uml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
