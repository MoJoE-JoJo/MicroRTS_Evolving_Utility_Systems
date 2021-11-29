package ai.utilitySystem;

import rts.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class USAction extends USNode implements Comparable<USAction> {
    private List<USNode> params;

    public List<USNode> getParams() {
        return params;
    }

    public enum UtilAction {
        ATTACK_WITH_SINGLE_UNIT,
        DEFEND_WITH_SINGLE_UNIT,
        BUILD_BASE,
        BUILD_BARRACKS,
        BUILD_WORKER,
        BUILD_LIGHT,
        BUILD_RANGED,
        BUILD_HEAVY,
        HARVEST_RESOURCE
    }

    private UtilAction action;
    private float weightedValue;

    public USAction(String name, List<USNode> featureList, UtilAction action) {
        this.name = name;
        this.params = Objects.requireNonNullElseGet(featureList, LinkedList::new);
        this.action = action;
    }

    @Override
    protected void calculateValue(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        // find average of all params going to this action.
        float val = 0.0f;
        for (int i = 0; i < params.size(); i++) {
            if (i == 0) val = params.get(i).getValue(gs, player, unitGroups);
            else {
                float nextVal = params.get(i).getValue(gs, player, unitGroups);
                val += nextVal;
            }
        }
        if (val == 0.0f) {
            this.value = 0.0f;
            return;
        }
        val /= params.size();
        this.value = val;
    }


    public UtilAction getAction() {
        return this.action;
    }

    @Override
    public NodeType getType() {
        return NodeType.US_ACTION;
    }

    @Override
    public String toPlantUML() {
        return "object " + this.name + " {\n" +
                "Score: " + this.value + "\n" +
                "}\n";
    }

    public void addParam(USNode node) {
        params.add(node);
    }

    public String relationsToPlantUML() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName()).append(" ---> ").append(this.name).append("\n");
        }
        return sb.toString();

        //return this.feature.getName() + " --> " + this.name + "\n";
    }

    public void setWeightedValue(float weightedValue) {
        this.weightedValue = weightedValue;
    }

    public float getWeightedValue() {
        return this.weightedValue;
    }

    @Override
    public int compareTo(USAction a) {
        return this.weightedValue > a.getWeightedValue() ? 1 : this.weightedValue < a.getWeightedValue() ? -1 : 0;
    }
}
