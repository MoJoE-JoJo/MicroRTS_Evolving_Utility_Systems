package util;
import rts.*;

public class USAction extends USNode {
    private String operation;
    private USNode param1;
    private USNode param2;
    private PlayerAction action;

    public USAction (String name, String operation, USNode param1, USNode param2, PlayerAction action) {
        this.name = name;
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
        this.action = action;
    }

    @Override
    public void calculateValue(GameState gs) {
        if (this.operation == "/") {
            this.value = this.param1.getValue(gs) / this.param2.getValue(gs);
        } else {
            // add other operations as needed
        }
    }

    public PlayerAction getAction() {
        return this.action;
    }
}
