package utilitySystem;
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
}
