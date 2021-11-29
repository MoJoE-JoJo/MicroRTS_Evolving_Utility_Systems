package ai.utilitySystem;

import ai.utilitySystem.USFeature.Operation;
import ai.utilitySystem.USVariable.GameStateVariable;

import java.util.LinkedList;
import java.util.List;

public final class StaticUtilitySystems {
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
        actions.add(new USAction("attack", new LinkedList<>(List.of(f1)), USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        actions.add(new USAction("defend", new LinkedList<>(List.of(f1)), USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_worker", new LinkedList<>(List.of(f1)), USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("build_barracks", new LinkedList<>(List.of(f1)), USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_base", new LinkedList<>(List.of(f1)), USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_light", new LinkedList<>(List.of(f1)), USAction.UtilAction.BUILD_LIGHT));
        actions.add(new USAction("harvest", new LinkedList<>(List.of(f1)), USAction.UtilAction.HARVEST_RESOURCE));

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
        USVariable vid = new USVariable("iddle_warriors", GameStateVariable.PLAYER_IDLE_WARRIORS);
        variables.add(vid);
        USVariable vidwo = new USVariable("iddle_warriors", GameStateVariable.PLAYER_IDLE_WORKERS);
        variables.add(vidwo);

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
        USConstant c25 = new USConstant("c25",25f);
        constants.add(c25);

        // features
        USFeature fa1 = new USFeature("iddlewarr", Operation.MULTIPLY);
        fa1.addParam(vid);
        fa1.addParam(c25);
        features.add(fa1);

        USFeature fba1 = new USFeature("resourceSUB10", Operation.SUBTRACT);
        fba1.addParam(vre);
        fba1.addParam(c10);
        features.add(fba1);

        USFeature fba2 = new USFeature("resourceGT10", Operation.MAX);
        fba2.addParam(fba1);
        fba2.addParam(c0);
        features.add(fba2);

        USFeature fra1 = new USFeature("01POWracks", Operation.POWER);
        fra1.addParam(c05);
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

        USFeature fwa2 = new USFeature("racksX10Xresources", Operation.MULTIPLY);
        fwa2.addParam(fwa);
        fwa2.addParam(vre);
        features.add(fwa2);

        USFeature fwo1 = new USFeature("02POWworkers", Operation.POWER);
        fwo1.addParam(c05);
        fwo1.addParam(vwo);
        features.add(fwo1);

        USFeature fwo2 = new USFeature("valueWorXPow", Operation.MULTIPLY);
        fwo2.addParam(c25);
        fwo2.addParam(fwo1);
        features.add(fwo2);

        USFeature fh1 = new USFeature("iddlework", Operation.MULTIPLY);
        fh1.addParam(vidwo);
        fh1.addParam(c25);
        features.add(fh1);

        // actions
        actions.add(new USAction("attack", new LinkedList<>(List.of(fa1)), USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT));
        //actions.add(new USAction("defend", f2, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT));
        actions.add(new USAction("build_base", new LinkedList<>(List.of(fba2)), USAction.UtilAction.BUILD_BASE));
        actions.add(new USAction("build_barracks", new LinkedList<>(List.of(fra2)), USAction.UtilAction.BUILD_BARRACKS));
        actions.add(new USAction("build_light", new LinkedList<>(List.of(fwa2)), USAction.UtilAction.BUILD_LIGHT));
        actions.add(new USAction("build_worker", new LinkedList<>(List.of(fwo2)), USAction.UtilAction.BUILD_WORKER));
        actions.add(new USAction("harvest", new LinkedList<>(List.of(fh1)), USAction.UtilAction.HARVEST_RESOURCE));

        UtilitySystem us = new UtilitySystem(variables, features, actions, constants);
        return us;
    }
}
