package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.physics.Current;
import eu.printingin3d.physics.Temperature;
import eu.printingin3d.physics.Voltage;

public class BatteryInfoResponse {
    private final Temperature batteryTemperature;
    private final Voltage batteryVoltage;
    private final Current batteryAmpere;

    public BatteryInfoResponse(Temperature batteryTemperature, Voltage batteryVoltage, Current batteryAmpere) {
        this.batteryTemperature = batteryTemperature;
        this.batteryVoltage = batteryVoltage;
        this.batteryAmpere = batteryAmpere;
    }

    public Temperature getBatteryTemperature() {
        return batteryTemperature;
    }

    public Voltage getBatteryVoltage() {
        return batteryVoltage;
    }

    public Current getBatteryAmpere() {
        return batteryAmpere;
    }
}
