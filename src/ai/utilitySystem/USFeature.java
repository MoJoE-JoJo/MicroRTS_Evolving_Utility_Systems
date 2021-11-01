package ai.utilitySystem;
import rts.*;

public class USFeature extends USNode {
    private String operation; // make an enum for this.
    private USNode param1; // What if we want a constant here instead?
    private USNode param2;

    public USFeature (String name, String operation, USNode param1, USNode param2) {
        this.name = name;
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public void calculateValue(GameState gs, int player) {
        if (this.operation == "Divide") {
            this.value = this.param1.getValue(gs, player) / this.param2.getValue(gs, player);
        } else {
            // add other operations as needed
        }
    }

    @Override
    public String toPlantUML() {
        return "map " + this.name + " {\n" +
            "Func => " + this.operation + "(V1, V2)\n" +
            "Value => " + this.value + "\n" +
            "}\n";
    }

    public String relationsToPlantUML() {
        return this.param1.getName() + " --> " + this.name + " : V1\n" + 
            this.param2.getName() + " --> " + this.name + " : V2\n";
    }
}
