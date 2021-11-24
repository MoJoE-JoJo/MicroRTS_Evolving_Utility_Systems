package ai.utilitySystem;

import ai.utilitySystem.USAction.UtilAction;
import rts.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class UtilitySystem {
    protected List<USVariable> variables;
    protected List<USFeature> features;
    protected List<USAction> actions;
    protected List<USConstant> constants;
    protected Random random;
    protected int generation;

    public UtilitySystem(List<USVariable> variables, List<USFeature> features, List<USAction>actions, List<USConstant> constants) {
        this.variables = variables;
        this.features = features;
        this.actions = actions;
        this.constants = constants;
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
    public UtilAction getActionBest(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        this.markAllNodesUnvisited();
        // calculate values for all actions
        for(int i = 0; i < actions.size(); i++) {
            USAction node = actions.get(i);
            actions.get(i).setWeightedValue(node.getValue(gs, player, unitGroups));
        }
        // order the actions
        return getSortedUtilActions().get(0);
    }

    // gets a random node, using the scores as weights
    public List<UtilAction> getActionWeightedRandom(GameState gs, int player, UnitGroups unitGroups) throws Exception {
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
        // normalize the values to the range 0-1 and multiply by a random number
        for(int i = 0; i < actions.size(); i++) {
            float val = (values[i] - min) / (max - min);
            actions.get(i).setWeightedValue(val * this.random.nextFloat());
        }
        // order the actions by the random weighted value
        return getSortedUtilActions();
    }

    // make sure weightedValue is set before calling this
    private List<UtilAction> getSortedUtilActions() {
        List<USAction> copiedActions = new ArrayList<>(actions);
        // Shuffle first so that any actions with the same value are in random order
        Collections.shuffle(copiedActions);
        // Sort
        Collections.sort(copiedActions);
        Collections.reverse(copiedActions);

        // Convert to UtilAction
        List<UtilAction> orderedActions = new ArrayList<>();
        for(int i = 0; i < actions.size(); i++) {
            orderedActions.add(copiedActions.get(i).getAction());
        }
        return orderedActions;
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
}
