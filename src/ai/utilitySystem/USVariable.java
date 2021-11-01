package ai.utilitySystem;
import rts.*;

public class USVariable extends USNode {
    GameStateVariables gsv;

    enum GameStateVariables {
        PLAYER_RESOURCE,
        ENEMY_RESOURCE
    }

    public USVariable(String name, GameStateVariables gsv) {
        this.name = name;
        this.gsv = gsv;
    }

    @Override
    public void calculateValue(GameState gs, int player) {
        switch (this.gsv) {
            case PLAYER_RESOURCE:
                this.value = getPlayerResource(gs, player);
                break;
            case ENEMY_RESOURCE:
                // player ids are 0 and 1, so invert it to get the enemy's id.
                this.value = getPlayerResource(gs, player == 0 ? 1 : 0);
                break;
            default:
                // throw an error?
                break;
        }
    }

    @Override
    public String toPlantUML() {
        return "object " + this.name + " {\n" +
            "Value: " + this.value + "\n" +
            "}\n";
    }

    private float getPlayerResource(GameState gs, int player) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        return pgs.getPlayer(player).getResources();
    }
}
