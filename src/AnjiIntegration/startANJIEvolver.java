package AnjiIntegration;

import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJIEvolver {


    public static void main(String[] args) {
        try {
          Properties props = new Properties("utility_system_properties/test_11_big_map.properties");
          Evolver evolver = new Evolver();
          evolver.init(props);
          evolver.run();
          System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

