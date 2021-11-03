package ai.utilitySystem;
import java.util.List;

import rts.*;
import rts.units.Unit;

public class USVariable extends USNode {
    GameStateVariables gsv;

    enum GameStateVariables {
        PLAYER_RESOURCE,
        ENEMY_RESOURCE,
        PLAYER_WORKERS,
        ENEMY_WORKERS,
        PLAYER_LIGHT,
        ENEMY_LIGHT,
        PLAYER_HEAVY,
        ENEMY_HEAVY,
        PLAYER_RANGED,
        ENEMY_RANGED,
        PLAYER_BARRACKS,
        ENEMY_BARRACKS,
        PLAYER_BASE_HEALTH,
        ENEMY_BASE_HEALTH,
        UNHARVESTED_RESOURCES
    }

    public USVariable(String name, GameStateVariables gsv) {
        this.name = name;
        this.gsv = gsv;
    }

    @Override
    public void calculateValue(GameState gs, int player) throws Exception {
        final int enemy = player == 0 ? 1 : 0;
        switch (this.gsv) {
            case PLAYER_RESOURCE:
                this.value = getPlayerResource(gs, player);
                break;
            case ENEMY_RESOURCE:
                // player ids are 0 and 1, so invert it to get the enemy's id.
                this.value = getPlayerResource(gs, enemy);
                break;
            case PLAYER_WORKERS:
                this.value = getPlayerUnitCount(gs, player, "Worker");
                break;
            case ENEMY_WORKERS:
                this.value = getPlayerUnitCount(gs, enemy, "Worker");
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

    private float getPlayerResource(GameState gs, int player) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        return pgs.getPlayer(player).getResources();
    }

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
}
