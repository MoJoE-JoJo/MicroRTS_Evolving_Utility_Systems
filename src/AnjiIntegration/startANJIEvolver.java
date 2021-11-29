package AnjiIntegration;

import ai.utilitySystem.USConstants;
import ai.utilitySystem.UtilitySystem;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJIEvolver {


    public static void main(String[] args) {
        try {
            String s = anjiConverter.toXMLStringFromUtilitySystem(USConstants.getSimpleUtilitySystem());
            String uml = USConstants.getSimpleUtilitySystem().toPlantUML();
            System.out.println(s);
//          Properties props = new Properties("utility_system_properties/test_3_ensemble_learning.properties");
//          Evolver evolver = new Evolver();
//          evolver.init(props);
//          evolver.run();
//          System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

