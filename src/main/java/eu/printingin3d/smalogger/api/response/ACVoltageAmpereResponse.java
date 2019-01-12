package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.physics.Current;
import eu.printingin3d.physics.Voltage;

public class ACVoltageAmpereResponse {
    private final ThreePhaseResponse<Voltage> voltage;
    private final ThreePhaseResponse<Current> ampere;

    public ACVoltageAmpereResponse(ThreePhaseResponse<Voltage> voltage, ThreePhaseResponse<Current> ampere) {
        this.voltage = voltage;
        this.ampere = ampere;
    }

    public ThreePhaseResponse<Voltage> getVoltage() {
        return voltage;
    }

    public ThreePhaseResponse<Current> getAmpere() {
        return ampere;
    }
}
