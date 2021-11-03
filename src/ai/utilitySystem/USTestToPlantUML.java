package ai.utilitySystem;

import java.util.LinkedList;
import java.util.List;

import ai.utilitySystem.USFeature.Operation;
import ai.utilitySystem.USVariable.GameStateVariables;
import rts.PlayerAction;

public class USTestToPlantUML {
    public static void main(String args[]) throws InterruptedException {
        // create a US
        List<USVariable> variables = new LinkedList<USVariable>();
        List<USFeature> features = new LinkedList<USFeature>();
        List<USAction> actions = new LinkedList<USAction>();

        // variables
        USVariable v1 = new USVariable("v1", GameStateVariables.PLAYER_RESOURCE);
        variables.add(v1);
        USVariable v2 = new USVariable("v2", GameStateVariables.PLAYER_WORKERS);
        variables.add(v2);
        USVariable v3 = new USVariable("v3", GameStateVariables.PLAYER_WARRIORS);
        variables.add(v3);

        // features
        USFeature f1 = new USFeature("f1", Operation.DIVIDE, v1, v2);
        features.add(f1);
        USFeature f2 = new USFeature("f2", Operation.MULTIPLY, v2, v3);
        features.add(f2);

        // actions
        USAction a1 = new USAction("a1", f1, new PlayerAction());
        actions.add(a1);
        USAction a2 = new USAction("a2", f2, new PlayerAction());
        actions.add(a2);

        UtilitySystem us = new UtilitySystem(variables, features, actions);
        String plantUML = us.toPlantUML();
        System.out.print(plantUML);
    }
}