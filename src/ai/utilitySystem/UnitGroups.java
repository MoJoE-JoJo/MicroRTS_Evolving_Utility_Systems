package ai.utilitySystem;

import java.util.HashSet;

import rts.units.Unit;

public class UnitGroups {
    public HashSet<Unit> passiveUnits;
    public HashSet<Unit> harvestingWorkers;
    public HashSet<Unit> buildingWorkers;
    public HashSet<Unit> attackingUnits;
    public HashSet<Unit> defendingUnits;

    public UnitGroups(HashSet<Unit> passiveUnits, HashSet<Unit> harvestingWorkers, HashSet<Unit> buildingWorkers, 
                        HashSet<Unit> attackingUnits, HashSet<Unit> defendingUnits) {
        this.passiveUnits = passiveUnits;
        this.harvestingWorkers = harvestingWorkers;
        this.buildingWorkers = buildingWorkers;
        this.attackingUnits = attackingUnits;
        this.defendingUnits = defendingUnits;
    }
}
