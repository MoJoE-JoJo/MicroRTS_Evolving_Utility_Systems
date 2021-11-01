package util;

import rts.GameState;

public class USNode {
    protected String name;
    protected float value;
    protected boolean visited;

    public void calculateValue(GameState gs) {

    }

    public float getValue(GameState gs) {
        if (!this.visited) {
            this.calculateValue(gs);
            this.visited = true;
        }
        return this.value;
    }

    public boolean getVisited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getName() {
        return this.name;
    }

}
