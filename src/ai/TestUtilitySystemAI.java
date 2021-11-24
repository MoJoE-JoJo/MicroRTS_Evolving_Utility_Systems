package ai;

import rts.*;
import rts.units.UnitTypeTable;

public class TestUtilitySystemAI extends UtilitySystemAI {
    boolean oneAction = true;

    public TestUtilitySystemAI(UnitTypeTable a_utt) {
        super(a_utt);
        /*
        List<USVariable> variables = new LinkedList<USVariable>();
        List<USFeature> features = new LinkedList<USFeature>();
        List<USAction> actions = new LinkedList<USAction>();

        // variables
        USVariable v1 = new USVariable("v1", USVariable.GameStateVariable.PLAYER_RESOURCE);
        variables.add(v1);
        USVariable v2 = new USVariable("v2", USVariable.GameStateVariable.PLAYER_WORKERS);
        variables.add(v2);
        USVariable v3 = new USVariable("v3", USVariable.GameStateVariable.PLAYER_WARRIORS);
        variables.add(v3);

        // constants
        USConstant c1 = new USConstant(0.1f);

        // features
        USFeature f1 = new USFeature("f1", USFeature.Operation.DIVIDE, v1, v2);
        features.add(f1);
        USFeature f2 = new USFeature("f2", USFeature.Operation.MULTIPLY, v2, v3);
        features.add(f2);
        USFeature f3 = new USFeature("f3", USFeature.Operation.MULTIPLY, v3, c1);
        features.add(f3);
        USFeature f4 = new USFeature("f4", USFeature.Operation.MULTIPLY, f2, f3);
        features.add(f4);

        // actions
        USAction a1 = new USAction("a1", f1, USAction.UtilAction.ATTACK_WITH_SINGLE_UNIT);
        actions.add(a1);
        USAction a2 = new USAction("a2", f2, USAction.UtilAction.DEFEND_WITH_SINGLE_UNIT);
        actions.add(a2);
        USAction a3 = new USAction("a3", f4, USAction.UtilAction.BUILD_WORKER);
        actions.add(a3);

        us = new UtilitySystem(variables, features, actions);
        */


    }

    @Override
    public PlayerAction getAction(int player, GameState gs) {
        //System.out.println("Yolo");
        //return super.getAction(player, gs);
        Player p = gs.getPlayer(player);
        PhysicalGameState pgs = gs.getPhysicalGameState();
        for (UnitActionAssignment ua : gs.getUnitActions().values()){
            System.out.println(ua.action.getType());
        }
        //for(Unit u:pgs.getUnits()) {
            //System.out.println(gs.getActionAssignment(u).toString());
        //}
        if(oneAction){
            oneAction = false;
            if(DefendWithSingleUnit(gs, p)) return translateActions(p.getID(), gs);
            return translateActions(p.getID(), gs);
        }
        else{
            //oneAction = true;
            //return BuildWorker(gs, p);
            return translateActions(p.getID(),gs);
        }
        //return new PlayerAction();
    }
}
