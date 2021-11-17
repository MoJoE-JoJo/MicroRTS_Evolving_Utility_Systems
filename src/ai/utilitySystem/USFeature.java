package ai.utilitySystem;

import rts.*;

public class USFeature extends USNode {
    private Operation operation;
    private USNode param1; // What if we want a constant here instead?
    private USNode param2;

    public enum Operation {
        DIVIDE,
        MULTIPLY,
        SUM,
        SUBTRACT,
        MIN,
        MAX,
        POWER
    }

    public USFeature(String name, Operation operation, USNode param1, USNode param2) {
        this.name = name;
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    protected void calculateValue(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        float val1 = this.param1.getValue(gs, player, unitGroups);
        float val2 = this.param2.getValue(gs, player, unitGroups);
        switch (this.operation) {
            case DIVIDE:
                if (val2 == 0) {
                    this.value = 0;
                    break;
                }
                this.value = val1 / val2;
                break;
            case MULTIPLY:
                this.value = val1 * val2;
                break;
            case SUM:
                this.value = val1 + val2;
                break;
            case SUBTRACT:
                this.value = val1 - val2;
                break;
            case MIN:
                this.value = Math.min(val1, val2);
                break;
            case MAX:
                this.value = Math.max(val1, val2);
                break;
            case POWER:
                this.value = (float)Math.pow(val1, val2);
                break;
            default:
                throw new Exception("Not yet implemented operation: " + this.operation);
        }
    }

    @Override
    public NodeType getType() {
        return NodeType.US_FEATURE;
    }

    @Override
    public String toPlantUML() {
        String v1 = "V1";
        String v2 = "V2";
        if (this.param1.getClass() == USConstant.class) {
            USConstant constant = (USConstant) this.param1;
            v1 = "" + constant.getConstant();
        }
        if (this.param2.getClass() == USConstant.class) {
            USConstant constant = (USConstant) this.param2;
            v2 = "" + constant.getConstant();
        }
        return "map " + this.name + " {\n" +
                "Func => " + this.operation + "(" + v1 + "," + v2 + ")\n" +
                "Value => " + this.value + "\n" +
                "}\n";
    }

    public void addParam(USNode node) {
        if (param1 == null) {
            param1 = node;
        } else if (param2 == null) {
            param2 = node;
        } else {
            //TODO Handle this, unsure how

        }
    }

    public USNode getParam2()
    {
        return param2;
    }

    public String relationsToPlantUML() {
        return (this.param1.getClass() == USConstant.class ? "" : (this.param1.getName() + " --> " + this.name + " : V1\n")) +
                (this.param2.getClass() == USConstant.class ? "" : (this.param2.getName() + " --> " + this.name + " : V2\n"));
    }
}
