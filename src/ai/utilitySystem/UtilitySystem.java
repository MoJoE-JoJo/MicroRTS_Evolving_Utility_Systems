package ai.utilitySystem;
import java.util.List;
import java.util.Random;
import ai.utilitySystem.USAction.UtilAction;

import rts.*;

public class UtilitySystem {
    private List<USVariable> variables;
    private List<USFeature> features;
    private List<USAction> actions;
    private Random random;
    private int generation;

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
    public UtilAction getActionBest(GameState gs, int player) throws Exception {
        this.markAllNodesUnvisited();
        USAction bestNode = actions.get(0);
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            try{
                if (node.getValue(gs, player) > bestNode.getValue(gs, player)) {
                    bestNode = node;
                }
            }catch (Exception e){
                System.out.println(e);
            }

        }
        return bestNode.getAction();
    }

    // gets a random node, using the scores as weights
    public UtilAction getActionWeightedRandom(GameState gs, int player) throws Exception {
        this.markAllNodesUnvisited();
        float sum = 0;
        float[] indices = new float[actions.size()];
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            sum += node.getValue(gs, player);
            indices[i] = sum;
        }
        float r = this.random.nextFloat() * sum;
        for(int i = 0; i < actions.size(); i++) {
            if (r <= indices[i]) {
                return actions.get(i).getAction();
            }
        }
        // An action should always be returned in the loop above,
        // if not something is wrong with the implementation above.
        throw new Exception("getActionWeightedRandom failed to choose an action.");
    }

    // outputs the utility system as a string that can be parsed by PlantUML
    public String toPlantUML() {
        String concatString = "";
        String variables = "";
        String features = "";
        String actions = "";
        String relations = "";
        for(int i = 0; i < this.variables.size(); i++) {
            variables += this.variables.get(i).toPlantUML();
        }
        for(int i = 0; i < this.features.size(); i++) {
            USFeature feature = this.features.get(i);
            features += feature.toPlantUML();
            relations += feature.relationsToPlantUML();
        }
        for(int i = 0; i < this.actions.size(); i++) {
            USAction action = this.actions.get(i);
            actions += action.toPlantUML();
            relations += action.relationsToPlantUML();
        }
        concatString += USConstants.PlantUMLStart;
        concatString += USConstants.PlantUMLVariablesStart;
        concatString += variables;
        concatString += "}\n";
        concatString += USConstants.PlantUMLActionsStart;
        concatString += actions;
        concatString += "}\n";
        concatString += features;
        concatString += relations;
        concatString += USConstants.PlantUMLEnd;
        return concatString;
    }
}
