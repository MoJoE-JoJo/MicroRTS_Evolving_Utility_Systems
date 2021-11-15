package ai.utilitySystem;
import rts.*;

public class USAction extends USNode {
    public enum UtilAction {
        ATTACK_WITH_SINGLE_UNIT,
        DEFEND_WITH_SINGLE_UNIT,
        BUILD_BASE,
        BUILD_BARRACKS,
        BUILD_WORKER,
        BUILD_WAR_UNIT,
        HARVEST_RESOURCE
    }

    private USFeature feature;
    private UtilAction action;

    public USAction (String name, USFeature feature, UtilAction action) {
        this.name = name;
        this.feature = feature;
        this.action = action;
    }

    @Override
    protected void calculateValue(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        this.value = this.feature.getValue(gs, player, unitGroups);
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
        return this.feature.getName() + " --> " + this.name + "\n";
    }
}
