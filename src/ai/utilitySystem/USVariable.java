package ai.utilitySystem;
import java.util.List;

import rts.*;
import rts.units.Unit;

public class USVariable extends USNode {
    GameStateVariable gsv;

    enum GameStateVariable {
        PLAYER_RESOURCE,            // The player's total resource count.
        ENEMY_RESOURCE,             // The enemy's total resource count.
        PLAYER_WORKERS,             // The player's total worker unit count.
        ENEMY_WORKERS,              // The enemy's total worker unit count.
        PLAYER_WARRIORS,            // The player's total warrior unit count (Light, Heavy and ranged).
        ENEMY_WARRIORS,             // The enemy's total warrior unit count (Light, Heavy and ranged).
        PLAYER_LIGHT,               // The player's total light unit count.
        ENEMY_LIGHT,                // The enemy's total light unit count.
        PLAYER_HEAVY,               // The player's total heavy unit count.
        ENEMY_HEAVY,                // The enemy's total heavy unit count.
        PLAYER_RANGED,              // The player's total ranged unit count.
        ENEMY_RANGED,               // The enemy's total ranged unit count.
        PLAYER_BARRACKS,            // The player's total barrack unit count.
        ENEMY_BARRACKS,             // The enemy's total barrack unit count.
        PLAYER_BASE_HEALTH,         // The sum of all of the player's base units health.
        ENEMY_BASE_HEALTH,          // The sum of all of the enemy's base units health.
        UNHARVESTED_RESOURCES,      // The sum of unharvested resources in all resource units.
        PLAYER_HARVESTING_WORKERS   // The count of the player's worker units that are actively harvesting resources.
    }

    public USVariable(String name, GameStateVariable gsv) {
        this.name = name;
        this.gsv = gsv;
    }

    @Override
    protected void calculateValue(GameState gs, int player) throws Exception {
        // player ids are 0 and 1, so invert it to get the enemy's id.
        final int enemy = player == 0 ? 1 : 0;
        int sum;
        switch (this.gsv) {
            case PLAYER_RESOURCE:
                this.value = getPlayerResource(gs, player);
                break;
            case ENEMY_RESOURCE:
                this.value = getPlayerResource(gs, enemy);
                break;
            case PLAYER_WORKERS:
                this.value = getPlayerUnitCount(gs, player, "Worker");
                break;
            case ENEMY_WORKERS:
                this.value = getPlayerUnitCount(gs, enemy, "Worker");
                break;
            case PLAYER_WARRIORS:
                sum = 0;
                sum += getPlayerUnitCount(gs, player, "Light");
                sum += getPlayerUnitCount(gs, player, "Heavy");
                sum += getPlayerUnitCount(gs, player, "Ranged");
                this.value = sum;
                break;
            case ENEMY_WARRIORS:
                sum = 0;
                sum += getPlayerUnitCount(gs, enemy, "Light");
                sum += getPlayerUnitCount(gs, enemy, "Heavy");
                sum += getPlayerUnitCount(gs, enemy, "Ranged");
                this.value = sum;
                break;
            case PLAYER_LIGHT:
                this.value = getPlayerUnitCount(gs, player, "Light");
                break;
            case ENEMY_LIGHT:
                this.value = getPlayerUnitCount(gs, enemy, "Light");
                break;
            case PLAYER_HEAVY:
                this.value = getPlayerUnitCount(gs, player, "Heavy");
                break;
            case ENEMY_HEAVY:
                this.value = getPlayerUnitCount(gs, enemy, "Heavy");
                break;
            case PLAYER_RANGED:
                this.value = getPlayerUnitCount(gs, player, "Ranged");
                break;
            case ENEMY_RANGED:
                this.value = getPlayerUnitCount(gs, enemy, "Ranged");
                break;
            case PLAYER_BARRACKS:
                this.value = getPlayerUnitCount(gs, player, "Barracks");
                break;
            case ENEMY_BARRACKS:
                this.value = getPlayerUnitCount(gs, enemy, "Barracks");
                break;
            case PLAYER_BASE_HEALTH:
                this.value = getPlayerUnitHealthSum(gs, player, "Base");
                break;
            case ENEMY_BASE_HEALTH:
                this.value = getPlayerUnitHealthSum(gs, enemy, "Base");
                break;
            case UNHARVESTED_RESOURCES:
                this.value = getUnharvestedResources(gs);
                break;
            case PLAYER_HARVESTING_WORKERS:
                this.value = getUnitsPerformingAction(gs, player, "Worker", UnitAction.TYPE_HARVEST);
                break;
            default:
                throw new Exception("Not yet implemented game state variable: " + this.gsv);
        }
    }

    @Override
    public String toPlantUML() {
        return "object " + this.name + " {\n" +
            "Value: " + this.value + "\n" +
            "}\n";
    }

    /**
     * The given players total resource count in the current game state.
     * 
     * @param gs
     * @param player
     * @return
     */
    private float getPlayerResource(GameState gs, int player) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        return pgs.getPlayer(player).getResources();
    }

    /**
     * The current count of a specific unit type owned by the given player.
     * 
     * @param gs
     * @param player
     * @param unitName
     * @return
     */
    private float getPlayerUnitCount(GameState gs, int player, String unitName) {
        List<Unit> units = gs.getUnits();
        int count = 0;
        for(Unit unit : units) {
            if (unit.getPlayer() == player && unit.getType().name == unitName) {
                count++;
            }
        }
        return count;
    }

    /**
     * The health sum of hit points of all units of a specific type owned by the given player.
     * 
     * @param gs
     * @param player
     * @param unitName
     * @return
     */
    private float getPlayerUnitHealthSum(GameState gs, int player, String unitName) {
        List<Unit> units = gs.getUnits();
        int count = 0;
        for(Unit unit : units) {
            if (unit.getPlayer() == player && unit.getType().name == unitName) {
                count+= unit.getHitPoints();
            }
        }
        return count;
    }

    /**
     * The unharvested resources in all resource units in the game.
     * 
     * @param gs
     * @return
     */
    private float getUnharvestedResources(GameState gs) {
        List<Unit> units = gs.getUnits();
        int count = 0;
        for(Unit unit : units) {
            if (unit.getType().isResource) {
                count += unit.getResources(); // I'm not sure this is how to get the resource count
            }
        }
        return count;
    }

    /**
     * The number of units of a specific type owned by the given player executing a specific action.
     * 
     * @param gs
     * @param player
     * @param unitName
     * @param actionId
     * @return
     */
    private float getUnitsPerformingAction(GameState gs, int player, String unitName, int actionId) {
        List<Unit> units = gs.getUnits();
        int count = 0;
        for(Unit unit : units) {
            if (unit.getPlayer() == player && unit.getType().name == unitName) {
                UnitActionAssignment uaa = gs.getActionAssignment(unit);
                if (uaa != null && uaa.action.getType() == actionId) {
                    count++;
                }
            }
        }
        return count;
    }
}
