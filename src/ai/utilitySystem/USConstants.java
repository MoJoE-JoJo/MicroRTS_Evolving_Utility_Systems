package ai.utilitySystem;

import ai.utilitySystem.USFeature.Operation;
import ai.utilitySystem.USVariable.GameStateVariable;

import java.util.LinkedList;
import java.util.List;

public final class USConstants {
    public static final String PlantUMLStart = "@startuml\n" +
                                            "skinparam packageStyle rectangle\n"+
                                            "skinparam linetype polyline\n"+
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
        List<USConstant> constants = new LinkedList<USConstant>();

        // constants
        USConstant c1 = new USConstant("c1",1f);
        constants.add(c1);

        // features
        USFeature f1 = new USFeature("f1", Operation.DIVIDE);
        f1.addParam(c1);
        features.add(f1);

        // actions
        actions.add(new USAction("attack", f1, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        actions.add(new USAction("defend", f1, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_worker", f1, USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("build_barracks", f1, USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_base", f1, USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_light", f1, USAction.UtilAction.BUILD_LIGHT));
        actions.add(new USAction("harvest", f1, USAction.UtilAction.HARVEST_RESOURCE));

        UtilitySystem us = new UtilitySystem(variables, features, actions, constants);
        return us;
    }

    // create a simple utility system
    public static final UtilitySystem getSimpleUtilitySystem() {
        // create a US
        List<USVariable> variables = new LinkedList<USVariable>();
        List<USFeature> features = new LinkedList<USFeature>();
        List<USAction> actions = new LinkedList<USAction>();
        List<USConstant> constants = new LinkedList<USConstant>();

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
        USConstant c0 = new USConstant("c0",0f);
        constants.add(c0);
        USConstant c01 = new USConstant("c0.1",0.1f);
        constants.add(c01);
        USConstant c025 = new USConstant("c0.25",0.25f);
        constants.add(c025);
        USConstant c05 = new USConstant("c0.5",0.5f);
        constants.add(c05);
        USConstant c1 = new USConstant("c1",1f);
        constants.add(c1);
        USConstant c2 = new USConstant("c2",2f);
        constants.add(c2);
        USConstant c5 = new USConstant("c5",5f);
        constants.add(c5);
        USConstant c10 = new USConstant("c10",10f);
        constants.add(c10);

        // features
        USFeature fa1 = new USFeature("warriorX5", Operation.MULTIPLY);
        fa1.addParam(vwa);
        fa1.addParam(c5);
        features.add(fa1);

        USFeature fa2 = new USFeature("workerX1", Operation.MULTIPLY);
        fa2.addParam(vwo);
        fa2.addParam(c1);
        features.add(fa2);

        USFeature fa3 = new USFeature("warSUMwork", Operation.SUM);
        fa3.addParam(fa1);
        fa3.addParam(fa2);
        features.add(fa3);

        USFeature fba1 = new USFeature("resourceSUB10", Operation.SUBTRACT);
        fba1.addParam(vre);
        fba1.addParam(c10);
        features.add(fba1);

        USFeature fba2 = new USFeature("resourceGT10", Operation.MAX);
        fba2.addParam(fba1);
        fba2.addParam(c0);
        features.add(fba2);

        USFeature fra1 = new USFeature("01POWracks", Operation.POWER);
        fra1.addParam(c01);
        fra1.addParam(vra);
        features.add(fra1);

        USFeature fra2 = new USFeature("valueRacksXPow", Operation.MULTIPLY);
        fra2.addParam(c10);
        fra2.addParam(fra1);
        features.add(fra2);

        USFeature fwa = new USFeature("racksX10", Operation.MULTIPLY);
        fwa.addParam(c10);
        fwa.addParam(vra);
        features.add(fwa);

        USFeature fwo1 = new USFeature("resouceSUB1", Operation.SUBTRACT);
        fwo1.addParam(vre);
        fwo1.addParam(c1);
        features.add(fwo1);

        USFeature fwo2 = new USFeature("resouceGT1", Operation.MAX);
        fwo2.addParam(fwo1);
        fwo2.addParam(c0);
        features.add(fwo2);

        USFeature fwo3 = new USFeature("workValue", Operation.MAX);
        fwo3.addParam(fwo2);
        fwo3.addParam(c10);
        features.add(fwo3);

        USFeature fwo4 = new USFeature("02POWworkers", Operation.POWER);
        fwo4.addParam(c025);
        fwo4.addParam(vwo);
        features.add(fwo4);

        USFeature fwo5 = new USFeature("valueWorXPow", Operation.MULTIPLY);
        fwo5.addParam(fwo3);
        fwo5.addParam(fwo4);
        features.add(fwo5);

        USFeature fh1 = new USFeature("harvestingMAX02", Operation.MAX);
        fh1.addParam(vha);
        fh1.addParam(c01);
        features.add(fh1);

        USFeature fh2 = new USFeature("5DIVabove", Operation.DIVIDE);
        fh2.addParam(c5);
        fh2.addParam(fh1);
        features.add(fh2);

        // actions
        actions.add(new USAction("attack", fa3, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        //actions.add(new USAction("defend", f2, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_base", fba2, USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_barracks", fra2, USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_light", fwa, USAction.UtilAction.BUILD_LIGHT));
        actions.add(new USAction("build_worker", fwo5, USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("harvest", fh2, USAction.UtilAction.HARVEST_RESOURCE));

        UtilitySystem us = new UtilitySystem(variables, features, actions, constants);
        return us;
    }
}
