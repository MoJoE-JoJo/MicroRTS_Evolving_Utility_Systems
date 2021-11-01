package util;
import java.util.List;
import rts.*;

public class UtilitySystem {
    private List<USNode> variables;
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

    // need to find the best way to pass gamestate, just using String as a placeholder
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
}
