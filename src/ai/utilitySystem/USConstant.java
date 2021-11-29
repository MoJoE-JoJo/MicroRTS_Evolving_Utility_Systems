package ai.utilitySystem;

import rts.*;

public class USConstant extends USNode {
    private float constant;

    public USConstant(String name, float constant) {
        this.name = name;
        this.constant = constant;
    }

    public float getConstant() {
        return this.constant;
    }

    @Override
    protected void calculateValue(GameState gs, int player, UnitGroups unitGroups) throws Exception {
        this.value = this.constant;
        this.visited = true;
    }

    public static NodeType getType() {
        return NodeType.US_CONSTANT;
    }

    @Override
    public String toPlantUML() {
        return "object " + this.name + " {\n" +
                "Value: " + this.constant + "\n" +
                "}\n";
    }
}
