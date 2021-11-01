package util;
import java.util.List;
import rts.*;

public class UtilitySystem {
    private List<USVariable> variables;
    private List<USNode> features;
    private List<USAction> actions;

    private void markAllNodesUnvisited() {
        variables.forEach(node -> {
            node.setVisited(false);
        });
        features.forEach(node -> {
            node.setVisited(false);
        });
        actions.forEach(node -> {
            node.setVisited(false);
        });
    }

    // currently just returns highest value, should select a random based on the values.
    public PlayerAction getAction(GameState gs) {
        this.markAllNodesUnvisited();
        USAction bestNode = actions.get(0);
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            if (node.getValue(gs) > bestNode.getValue(gs)) {
                bestNode = node;
            }
        }
        return bestNode.getAction();
    }

    // outputs the utility system as a string that can be parsed by PlantUML
    public String ToPlantUML() {
        return "";
    }
}
