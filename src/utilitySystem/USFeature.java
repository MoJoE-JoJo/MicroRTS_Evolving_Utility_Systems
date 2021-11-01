package utilitySystem;
import rts.*;

public class USFeature extends USNode {
    private String operation;
    private USNode param1;
    private USNode param2;

    public USFeature (String name, String operation, USNode param1, USNode param2) {
        this.name = name;
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public void calculateValue(GameState gs) {
        if (this.operation == "/") {
            this.value = this.param1.getValue(gs) / this.param2.getValue(gs);
        } else {
            // add other operations as needed
        }
    }
}
