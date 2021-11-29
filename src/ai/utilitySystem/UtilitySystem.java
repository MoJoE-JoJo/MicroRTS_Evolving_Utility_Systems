package ai.utilitySystem;

import ai.utilitySystem.USAction.UtilAction;
import rts.GameState;

import java.util.List;
import java.util.Random;

public class UtilitySystem {
    protected List<USVariable> variables;
    protected List<USFeature> features;
    protected List<USAction> actions;
    protected List<USConstant> constants;
    protected Random RNG;
    protected boolean random = true;
    protected int generation;

    public UtilitySystem(List<USVariable> variables, List<USFeature> features, List<USAction>actions, List<USConstant> constants) {
        this.variables = variables;
        this.features = features;
        this.actions = actions;
        this.constants = constants;
        this.RNG = new Random();
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

    public UtilAction getAction(GameState gs, int player, UnitGroups unitGroups) throws Exception
    {
        if (random)
        {
            return getActionWeightedRandom(gs, player, unitGroups);
        }
        else
        {
            return getActionBest(gs, player, unitGroups);
        }
    }

    // gets the highest scoring action (if equal, by order)
    public UtilAction getActionBest(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        this.markAllNodesUnvisited();
        USAction bestNode = actions.get(0);
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            try{
                if (node.getValue(gs, player, unitGroups) > bestNode.getValue(gs, player, unitGroups)) {
                    bestNode = node;
                }
            }catch (Exception e){
                System.out.println(e);
            }

        }
        return bestNode.getAction();
    }

    // gets a random node, using the scores as weights
    private UtilAction getActionWeightedRandom(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        this.markAllNodesUnvisited();
        // calculate values for all actions
        float[] values = new float[actions.size()];
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            float value = node.getValue(gs, player, unitGroups);
            values[i] = value;
            if (value > max) max = value;
            if (value < min) min = value;
        }
        // if all weights are 0, return a random action
        if (min == max) {
            int randomInt = this.RNG.nextInt(0, values.length);
            return actions.get(randomInt).getAction();
        }
        // normalize the values to the range 0-1
        float[] indices = new float[actions.size()];
        float sum = 0;
        for(int i = 0; i < actions.size(); i++) {
            sum += (values[i] - min) / (max - min);
            indices[i] = sum;
        }
        // chose one randomly using the values as weights
        float r = this.RNG.nextFloat() * sum;
        for(int i = 0; i < actions.size(); i++) {
            if (r <= indices[i]) {
                return actions.get(i).getAction();
            }
        }
        // An action should always be returned to the loop above,
        // if not something is wrong with the implementation above.
        String debugString = toPlantUML();
        throw new Exception("getActionWeightedRandom failed to choose an action.");
    }

    // outputs the utility system as a string that can be parsed by PlantUML
    public String toPlantUML() {
        String concatString = "";
        String variables = "";
        String features = "";
        String actions = "";
        String relations = "";
        String constants = "";
        for(int i = 0; i < this.variables.size(); i++) {
            variables += this.variables.get(i).toPlantUML();
        }
        for(int i = 0; i < this.constants.size(); i++) {
            constants += this.constants.get(i).toPlantUML();
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
        concatString += constants;
        concatString += features;
        concatString += relations;
        concatString += USConstants.PlantUMLEnd;
        return concatString;
    }

    public void setRandom(boolean b) {
        random = b;
    }

    public List<USVariable> getVariables() {
        return variables;
    }

    public List<USFeature> getFeatures() {
        return features;
    }

    public List<USAction> getActions() {
        return actions;
    }
}
