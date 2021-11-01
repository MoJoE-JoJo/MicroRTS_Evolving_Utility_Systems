package ai.utilitySystem;
import rts.*;

public class USVariable extends USNode {
    String resourceName;

    public USVariable(String name, String resourceName) {
        this.name = name;
        this.resourceName = resourceName;
    }

    @Override
    public void calculateValue(GameState gs) {
        // get value from gamestate, based on resource name?
    }

    @Override
    public String toPlantUML() {
        return "object " + this.name + " {\n" +
            "Value: " + this.value + "\n" +
            "}\n";
    }
}
