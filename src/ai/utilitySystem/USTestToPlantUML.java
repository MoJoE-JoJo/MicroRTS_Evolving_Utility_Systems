package ai.utilitySystem;

import ai.utilitySystem.USFeature.Operation;
import ai.utilitySystem.USVariable.GameStateVariable;

import java.util.LinkedList;
import java.util.List;

public class USTestToPlantUML {
    public static void main(String args[]) throws InterruptedException {
        // create a US
        List<USVariable> variables = new LinkedList<USVariable>();
        List<USFeature> features = new LinkedList<USFeature>();
        List<USAction> actions = new LinkedList<USAction>();
        List<USConstant> constants = new LinkedList<USConstant>();


        // variables
        USVariable v1 = new USVariable("v1", GameStateVariable.PLAYER_RESOURCE);
        variables.add(v1);
        USVariable v2 = new USVariable("v2", GameStateVariable.PLAYER_WORKERS);
        variables.add(v2);
        USVariable v3 = new USVariable("v3", GameStateVariable.PLAYER_WARRIORS);
        variables.add(v3);

        // constants
        USConstant c1 = new USConstant("c1",0.1f);
        constants.add(c1);

        // features
        USFeature f1 = new USFeature("f1", Operation.DIVIDE);
        f1.addParam(v1);
        f1.addParam(v2);
        features.add(f1);

        USFeature f2 = new USFeature("f2", Operation.MULTIPLY);
        f2.addParam(v2);
        f2.addParam(v3);
        features.add(f2);

        USFeature f3 = new USFeature("f3", Operation.AVGERAGE);
        f3.addParam(v3);
        f3.addParam(c1);
        features.add(f3);

        USFeature f4 = new USFeature("f4", Operation.MULTIPLY);
        f4.addParam(f2);
        f4.addParam(f3);
        features.add(f4);

        // actions
        USAction a1 = new USAction("a1", f1, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT);
        actions.add(a1);
        USAction a2 = new USAction("a2", f2, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT);
        actions.add(a2);
        USAction a3 = new USAction("a3", f4, USAction.UtilAction.BUILD_WORKER);
        actions.add(a3);

        UtilitySystem us = new UtilitySystem(variables, features, actions, constants);
        String plantUML = us.toPlantUML();
        System.out.print(plantUML);
    }
}