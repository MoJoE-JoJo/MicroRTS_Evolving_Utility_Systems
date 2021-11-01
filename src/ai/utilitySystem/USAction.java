package ai.utilitySystem;
import rts.*;

public class USAction extends USNode {
    private USFeature feature;
    private PlayerAction action;

    public USAction (String name, USFeature feature, PlayerAction action) {
        this.name = name;
        this.feature = feature;
        this.action = action;
    }

    @Override
    public void calculateValue(GameState gs, int player) {
        this.value = this.feature.getValue(gs, player);
    }

    public PlayerAction getAction() {
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
