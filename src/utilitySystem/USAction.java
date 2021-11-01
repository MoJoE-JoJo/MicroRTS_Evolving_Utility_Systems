package utilitySystem;
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
    public void calculateValue(GameState gs) {
        this.value = this.feature.getValue(gs);
    }

    public PlayerAction getAction() {
        return this.action;
    }
}
