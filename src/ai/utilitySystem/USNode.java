package ai.utilitySystem;

import rts.GameState;

public abstract class USNode {
    protected String name;
    protected float value;
    protected boolean visited;

    public abstract void calculateValue(GameState gs);

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

    public abstract String toPlantUML();

}
