package ai.utilitySystem;

import java.util.LinkedList;
import java.util.List;

import ai.utilitySystem.USFeature.Operation;
import ai.utilitySystem.USVariable.GameStateVariable;

public class USTestRandom {
    public static void main(String args[]) throws InterruptedException {
        // create a US
        List<USVariable> variables = new LinkedList<USVariable>();
        List<USFeature> features = new LinkedList<USFeature>();
        List<USAction> actions = new LinkedList<USAction>();

        // constants
        USConstant c1 = new USConstant(1f);

        // features
        USFeature f1 = new USFeature("f1", Operation.DIVIDE, c1, c1);
        features.add(f1);

        // actions
        actions.add(new USAction("attack", f1, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        actions.add(new USAction("defend", f1, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_worker", f1, USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("build_barracks", f1, USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_base", f1, USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_war", f1, USAction.UtilAction.BUILD_WAR_UNIT));
        actions.add(new USAction("harvest", f1, USAction.UtilAction.HARVEST_RESOURCE));

        UtilitySystem us = new UtilitySystem(variables, features, actions);
        String plantUML = us.toPlantUML();
        System.out.print(plantUML);
    }
}