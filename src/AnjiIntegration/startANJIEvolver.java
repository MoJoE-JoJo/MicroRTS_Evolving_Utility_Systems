package AnjiIntegration;

import ai.utilitySystem.StaticUtilitySystems;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJIEvolver {


    public static void main(String[] args) {
        try {
//            anjiConverter poop = new anjiConverter();
//            String s = poop.toXMLStringFromUtilitySystem(StaticUtilitySystems.getSimpleUtilitySystem());
//            String uml = StaticUtilitySystems.getSimpleUtilitySystem().toPlantUML();
//            System.out.println(s);
          Properties props = new Properties("utility_system_properties/test_3_ensemble_learning.properties");
          Evolver evolver = new Evolver();
          evolver.init(props);
          evolver.run();
          System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

