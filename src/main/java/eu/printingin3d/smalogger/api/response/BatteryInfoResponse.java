package eu.printingin3d.smalogger.api.response;

public class BatteryInfoResponse {
    private final double batteryTemperature;
    private final double batteryVoltage;
    private final double batteryAmpere;

    public BatteryInfoResponse(double batteryTemperature, double batteryVoltage, double batteryAmpere) {
        this.batteryTemperature = batteryTemperature;
        this.batteryVoltage = batteryVoltage;
        this.batteryAmpere = batteryAmpere;
    }

    public double getBatteryTemperature() {
        return batteryTemperature;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public double getBatteryAmpere() {
        return batteryAmpere;
    }
}
