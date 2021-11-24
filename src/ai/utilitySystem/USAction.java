package ai.utilitySystem;
import rts.*;

public class USAction extends USNode implements Comparable<USAction> {
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

    private USNode feature;
    private UtilAction action;
    private float weightedValue;

    public USAction (String name, USFeature feature, UtilAction action) {
        this.name = name;
        this.feature = feature;
        this.action = action;
    }

    @Override
    protected void calculateValue(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        this.value = this.feature.getValue(gs, player, unitGroups);
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

    public void addFeature(USNode node) {
        feature = node;
    }

    public String relationsToPlantUML() {
        return this.feature.getName() + " --> " + this.name + "\n";
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
