package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.physics.Energy;

public class EnergyProductionResponse {
    private final Energy totalEnergy;
    private final Energy todayEnergy;

    public EnergyProductionResponse(Energy totalEnergy, Energy todayEnergy) {
        this.totalEnergy = totalEnergy;
        this.todayEnergy = todayEnergy;
    }

    public Energy getTotalEnergy() {
        return totalEnergy;
    }

    public Energy getTodayEnergy() {
        return todayEnergy;
    }
}
