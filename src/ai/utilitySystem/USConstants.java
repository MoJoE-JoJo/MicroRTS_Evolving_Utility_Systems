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
        USVariable vre = new USVariable("resource", GameStateVariable.PLAYER_RESOURCE);
        variables.add(vre);
        USVariable vwo = new USVariable("workers", GameStateVariable.PLAYER_WORKERS);
        variables.add(vwo);
        USVariable vwa = new USVariable("warriors", GameStateVariable.PLAYER_WARRIORS);
        variables.add(vwa);
        USVariable vha = new USVariable("harvesting_workers", GameStateVariable.PLAYER_HARVESTING_WORKERS);
        variables.add(vha);
        USVariable vra = new USVariable("barracks", GameStateVariable.PLAYER_BARRACKS);
        variables.add(vra);

        // constants
        USConstant c0 = new USConstant(0f);
        USConstant c01 = new USConstant(0.1f);
        USConstant c025 = new USConstant(0.25f);
        USConstant c05 = new USConstant(0.5f);
        USConstant c1 = new USConstant(1f);
        USConstant c2 = new USConstant(2f);
        USConstant c5 = new USConstant(5f);
        USConstant c10 = new USConstant(10f);        

        // features
        USFeature fa1 = new USFeature("warriorX5", Operation.MULTIPLY, vwa, c5);
        features.add(fa1);
        USFeature fa2 = new USFeature("workerX1", Operation.MULTIPLY, vwo, c1);
        features.add(fa2);
        USFeature fa3 = new USFeature("warSUMwork", Operation.SUM, fa1, fa2);
        features.add(fa3);
        USFeature fba1 = new USFeature("resourceSUB10", Operation.SUBTRACT, vre, c10);
        features.add(fba1);
        USFeature fba2 = new USFeature("resourceGT10", Operation.MAX, fba1, c0);
        features.add(fba2);
        USFeature fra1 = new USFeature("resouceSUB2", Operation.SUBTRACT, vre, c2);
        features.add(fra1);
        USFeature fra2 = new USFeature("resouceGT2", Operation.MAX, fra1, c0);
        features.add(fra2);
        USFeature fra3 = new USFeature("warrValue", Operation.MAX, fra2, c10);
        features.add(fra3);
        USFeature fra4 = new USFeature("02POWwarriors", Operation.POWER, c01, vra);
        features.add(fra4);
        USFeature fra5 = new USFeature("valueWarXPow", Operation.MULTIPLY, fra3, fra4);
        features.add(fra5);
        USFeature fwa1 = new USFeature("resouceSUB2", Operation.SUBTRACT, vre, c2);
        features.add(fwa1);
        USFeature fwa2 = new USFeature("resouceGT2", Operation.MAX, fwa1, c0);
        features.add(fwa2);
        USFeature fwa3 = new USFeature("warrValue", Operation.MAX, fwa2, c10);
        features.add(fwa3);
        USFeature fwa4 = new USFeature("02POWwarriors", Operation.POWER, c05, vwa);
        features.add(fwa4);
        USFeature fwa5 = new USFeature("valueWarXPow", Operation.MULTIPLY, fwa3, fwa4);
        features.add(fwa5);
        USFeature fwo1 = new USFeature("resouceSUB1", Operation.SUBTRACT, vre, c1);
        features.add(fwo1);
        USFeature fwo2 = new USFeature("resouceGT1", Operation.MAX, fwo1, c0);
        features.add(fwo2);
        USFeature fwo3 = new USFeature("workValue", Operation.MAX, fwo2, c10);
        features.add(fwo3);
        USFeature fwo4 = new USFeature("02POWworkers", Operation.POWER, c025, vwo);
        features.add(fwo4);
        USFeature fwo5 = new USFeature("valueWorXPow", Operation.MULTIPLY, fwo3, fwo4);
        features.add(fwo5);
        USFeature fh1 = new USFeature("harvestingMAX02", Operation.MAX, vha, c01);
        features.add(fh1);
        USFeature fh2 = new USFeature("5DIVabove", Operation.DIVIDE, c5, fh1);
        features.add(fh2);

        // actions
        actions.add(new USAction("attack", fa3, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        //actions.add(new USAction("defend", f2, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_base", fba2, USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_barracks", fra5, USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_war", fwa5, USAction.UtilAction.BUILD_WAR_UNIT));
        actions.add(new USAction("build_worker", fwo5, USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("harvest", fh2, USAction.UtilAction.HARVEST_RESOURCE));

        UtilitySystem us = new UtilitySystem(variables, features, actions);
        return us;
    }
}
