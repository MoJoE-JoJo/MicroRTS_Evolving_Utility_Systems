package AnjiIntegration;

import ai.utilitySystem.StaticUtilitySystems;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJIEvolver {


    public static void main(String[] args) {
        try {
          Properties props = new Properties("utility_system_properties/test_4_evolve_baseline.properties");
          Evolver evolver = new Evolver();
          evolver.init(props);
          evolver.run();
          System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

