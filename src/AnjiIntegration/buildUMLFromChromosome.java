package AnjiIntegration;

import ai.utilitySystem.UtilitySystem;

public class buildUMLFromChromosome {

    public static void main(String[] args) {
        try {
            anjiConverter anjioCon = new anjiConverter();
            UtilitySystem us = anjioCon.toUtilitySystemFromChromosome(2790);
            String uml = us.toPlantUML();
            System.out.println(uml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
