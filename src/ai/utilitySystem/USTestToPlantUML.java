package ai.utilitySystem;

public class USTestToPlantUML {
    public static void main(String args[]) throws InterruptedException {
        UtilitySystem us = StaticUtilitySystems.getSimpleUtilitySystem();
        String plantUML = us.toPlantUML();
        System.out.print(plantUML);
    }
}