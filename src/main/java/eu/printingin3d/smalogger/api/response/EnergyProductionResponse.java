package eu.printingin3d.smalogger.api.response;

public class EnergyProductionResponse {
    private final long totalEnergy;
    private final long todayEnergy;

    public EnergyProductionResponse(long totalEnergy, long todayEnergy) {
        this.totalEnergy = totalEnergy;
        this.todayEnergy = todayEnergy;
    }

    public long getTotalEnergy() {
        return totalEnergy;
    }

    public long getTodayEnergy() {
        return todayEnergy;
    }
}
