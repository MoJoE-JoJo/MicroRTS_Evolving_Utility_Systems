package ai.utilitySystem;

import rts.GameState;

/**
 * Base node for the utility system.
 */
public abstract class USNode {
    protected String name;
    protected float value;
    protected boolean visited;

    /**
     * Calculate the value of the node. Should only be called by getValue().
     * 
     * @param gs
     * @param player
     * @throws Exception
     */
    protected abstract void calculateValue(GameState gs, int player) throws Exception;

    /**
     * Gets the value of the node.
     * If it has not already been calculated it will make sure that it is.
     * 
     * @param gs
     * @param player
     * @return
     * @throws Exception
     */
    public float getValue(GameState gs, int player) throws Exception {
        if (!this.visited) {
            this.calculateValue(gs, player);
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

    /**
     * Gets the string representation of the node for use in PlantUML.
     * @return
     */
    public abstract String toPlantUML();

}
