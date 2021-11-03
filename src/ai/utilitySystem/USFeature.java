package ai.utilitySystem;
import rts.*;

public class USFeature extends USNode {
    private Operation operation;
    private USNode param1; // What if we want a constant here instead?
    private USNode param2;

    enum Operation {
        DIVIDE,
        MULTIPLY,
        SUM,
        SUBTRACT,
        MIN,
        MAX
    }

    public USFeature (String name, Operation operation, USNode param1, USNode param2) {
        this.name = name;
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public void calculateValue(GameState gs, int player) throws Exception {
        switch (this.operation) {
            case DIVIDE:
                // How do we avoid division by 0?
                this.value = this.param1.getValue(gs, player) / this.param2.getValue(gs, player);
                break;
            case MULTIPLY:
                this.value = this.param1.getValue(gs, player) * this.param2.getValue(gs, player);
                break;
            case SUM:
                this.value = this.param1.getValue(gs, player) + this.param2.getValue(gs, player);
                break;
            case SUBTRACT:
                this.value = this.param1.getValue(gs, player) - this.param2.getValue(gs, player);
                break;
            case MIN:
                this.value = Math.max(this.param1.getValue(gs, player), this.param2.getValue(gs, player));
                break;
            case MAX:
                this.value = Math.min(this.param1.getValue(gs, player), this.param2.getValue(gs, player));
                break;
            case default:
                throw new Exception("Not yet implemented operation: " + this.operation);
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
