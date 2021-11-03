package ai.utilitySystem;
import rts.*;

public class USConstant extends USNode {
    private float constant;

    public USConstant(float constant) {
        this.constant = constant;
    }

    public float getConstant() {
        return this.constant;
    }

    @Override
    public void calculateValue(GameState gs, int player) throws Exception {
        this.value = this.constant;
        this.visited = true;
    }

    @Override
    public String toPlantUML() {
        return "object Constant" + this.constant + " {\n" +
            "Value: " + this.constant + "\n" +
            "}\n";
    }
}
