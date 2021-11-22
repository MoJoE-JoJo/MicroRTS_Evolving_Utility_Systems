package AnjiIntegration;

import ai.PassiveAI;
import ai.UtilitySystemAI;
import ai.core.AI;
import ai.utilitySystem.UtilitySystem;
import com.anji.neat.Evolver;
import com.anji.util.Properties;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import tests.RunExperimentTest;

public class startANJI {


    public static void main(String[] args) {
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

