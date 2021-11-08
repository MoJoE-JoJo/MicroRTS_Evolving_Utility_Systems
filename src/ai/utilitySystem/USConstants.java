package ai.utilitySystem;

import java.util.LinkedList;
import java.util.List;

import ai.utilitySystem.USFeature.Operation;
import ai.utilitySystem.USVariable.GameStateVariable;

public final class USConstants {
    public static final String PlantUMLStart = "@startuml\n" +
                                            "left to right direction\n" +
                                            "' Horizontal lines: -->, <--, <-->\n" +
                                            "' Vertical lines: ->, <-, <->\n" +
                                            "title Simple Utility System\n\n";
    public static final String PlantUMLEnd = "@enduml";
    public static final String PlantUMLVariablesStart = "package Variables {\n";
    public static final String PlantUMLActionsStart = "package Actions {\n";

    // create a utility system that picks an action at random
    public static final UtilitySystem getRandomUtilitySystem() {
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
        return us;
    }

    // create a simple utility system
    public static final UtilitySystem getSimpleUtilitySystem() {
        // create a US
        List<USVariable> variables = new LinkedList<USVariable>();
        List<USFeature> features = new LinkedList<USFeature>();
        List<USAction> actions = new LinkedList<USAction>();

        // variables
        USVariable v1 = new USVariable("resource", GameStateVariable.PLAYER_RESOURCE);
        variables.add(v1);
        USVariable v2 = new USVariable("workers", GameStateVariable.PLAYER_WORKERS);
        variables.add(v2);
        USVariable v3 = new USVariable("warriors", GameStateVariable.PLAYER_WARRIORS);
        variables.add(v3);
        USVariable v4 = new USVariable("harvesting_workers", GameStateVariable.PLAYER_HARVESTING_WORKERS);
        variables.add(v4);

        // constants
        USConstant c1 = new USConstant(1f);
        USConstant c2 = new USConstant(5f);
        USConstant c3 = new USConstant(10f);
        USConstant c4 = new USConstant(0f);
        USConstant c5 = new USConstant(2f); 
        USConstant c6 = new USConstant(0.1f);        

        // features
        USFeature f1 = new USFeature("warriorX5", Operation.MULTIPLY, v3, c2);
        features.add(f1);
        USFeature f2 = new USFeature("workerX1", Operation.MULTIPLY, v2, c1);
        features.add(f2);
        USFeature f3 = new USFeature("warSUMwork", Operation.SUM, f1, f2);
        features.add(f3);
        USFeature f4 = new USFeature("resourceSUB10", Operation.SUBTRACT, v1, c3);
        features.add(f4);
        USFeature f5 = new USFeature("resourceGT10", Operation.MAX, f4, c4);
        features.add(f5);
        USFeature f6 = new USFeature("resouceSUB5", Operation.SUBTRACT, v1, c2);
        features.add(f6);
        USFeature f7 = new USFeature("resouceGT5", Operation.MAX, f6, c4);
        features.add(f7);
        USFeature f8 = new USFeature("resouceSUB2", Operation.SUBTRACT, v1, c5);
        features.add(f8);
        USFeature f9 = new USFeature("resouceGT2", Operation.MAX, f8, c4);
        features.add(f9);
        USFeature f10 = new USFeature("warMAX1", Operation.MAX, f1, c1);
        features.add(f10);
        USFeature f11 = new USFeature("resourceGT2DIVwarMin1", Operation.DIVIDE, f10, f1);
        features.add(f11);
        USFeature f12 = new USFeature("resouceSUB1", Operation.SUBTRACT, v1, c1);
        features.add(f12);
        USFeature f13 = new USFeature("resouceGT1", Operation.MAX, f12, c4);
        features.add(f13);
        USFeature f14 = new USFeature("workMAX1", Operation.MAX, f2, c1);
        features.add(f14);
        USFeature f15 = new USFeature("resourceGT1DIVworkMin1", Operation.DIVIDE, f13, f14);
        features.add(f15);
        USFeature f16 = new USFeature("harvestingMAX02", Operation.MAX, v4, c6);
        features.add(f16);
        USFeature f17 = new USFeature("1DIVabove", Operation.DIVIDE, c1, f16);
        features.add(f17);

        // actions
        actions.add(new USAction("attack", f3, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        //actions.add(new USAction("defend", f2, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_base", f5, USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_barracks", f7, USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_war", f11, USAction.UtilAction.BUILD_WAR_UNIT));
        actions.add(new USAction("build_worker", f15, USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("harvest", f17, USAction.UtilAction.HARVEST_RESOURCE));

        UtilitySystem us = new UtilitySystem(variables, features, actions);
        return us;
    }
}
