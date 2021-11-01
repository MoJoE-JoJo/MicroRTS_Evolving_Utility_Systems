package utilitySystem;
import java.util.List;
import java.util.Random;

import rts.*;

public class UtilitySystem {
    private List<USVariable> variables;
    private List<USFeature> features;
    private List<USAction> actions;
    private Random random;

    public UtilitySystem(List<USVariable> variables, List<USFeature> features, List<USAction>actions) {
        this.variables = variables;
        this.features = features;
        this.actions = actions;
        this.random = new Random();
    }

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

    // gets the highest scoring action
    public PlayerAction getActionBest(GameState gs) {
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

    // gets a random node, using the scores as weights
    public PlayerAction getActionWeightedRandom(GameState gs) throws Exception {
        float sum = 0;
        float[] indices = new float[actions.size()];
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            sum += node.getValue(gs);
            indices[i] = sum;
        }
        float r = this.random.nextFloat() * sum;
        for(int i = 0; i < actions.size(); i++) {
            if (r <= sum) {
                return actions.get(i).getAction();
            }
        }
        // An action should always be returned in the loop above,
        // if not something is wrong with the implementation above.
        throw new Exception("getActionWeightedRandom failed to choose an action.");
    }

    // outputs the utility system as a string that can be parsed by PlantUML
    public String ToPlantUML() {
        return "";
    }
}
