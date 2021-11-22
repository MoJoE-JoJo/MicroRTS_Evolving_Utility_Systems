package ai.utilitySystem;
import rts.*;

import java.util.ArrayList;
import java.util.List;

public class USAction extends USNode {

    private List<USNode> params;

    public enum UtilAction {
        ATTACK_WITH_SINGLE_UNIT,
        DEFEND_WITH_SINGLE_UNIT,
        BUILD_BASE,
        BUILD_BARRACKS,
        BUILD_WORKER,
        BUILD_WAR_UNIT,
        HARVEST_RESOURCE
    }

    private USNode feature;
    private UtilAction action;

    public USAction (String name, USFeature feature, UtilAction action) {
        this.name = name;
        this.feature = feature;
        this.action = action;
        this.params = new ArrayList<>();
    }

    public void addParam(USNode node) {
        params.add(node);
    }

    @Override
    protected void calculateValue(GameState gs, int player, UnitGroups unitGroups) throws Exception {

        // find average of all params going to this action.
        float val = 0.0f;
        for (int i = 0; i < params.size(); i++)
        {
            if (i == 0) val = params.get(i).getValue(gs, player, unitGroups);
            else {
                float nextVal = params.get(i).getValue(gs, player, unitGroups);
                val += nextVal;
            }
        }
        if(val == 0.0f)
        {
            this.value = 0.0f;
            return;
        }
        val /= params.size();
        this.value = val;
    }

    @Override
    public NodeType getType() {
        return NodeType.US_ACTION;
    }

    public UtilAction getAction() {
        return this.action;
    }

    @Override
    public String toPlantUML() {
        return "object " + this.name + " {\n" +
            "Score: " + this.value + "\n" +
            "}\n";
    }

    public String relationsToPlantUML() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<params.size(); i++){
            sb.append(params.get(i).getName()).append(" ---> ").append(this.name).append("\n");
        }
        return sb.toString();

        //return this.feature.getName() + " --> " + this.name + "\n";
    }
}
