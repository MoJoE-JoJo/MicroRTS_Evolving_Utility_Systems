package NEATIntegration;

import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class startANJI {


    public static void main(String[] args) {
        System.out.println("Starting ANJI test");

        try {
            anjiConverter.toUtilitySytemFromChromosome(9999);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            Properties props = new Properties("ttt.properties");
//            Evolver evolver = new Evolver();
//            evolver.init(props);
//            evolver.run();
//            System.exit(0);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}

