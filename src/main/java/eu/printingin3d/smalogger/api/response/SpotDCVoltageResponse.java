package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.physics.Current;
import eu.printingin3d.physics.Voltage;

public class SpotDCVoltageResponse {
    private final Voltage vdc1;
    private final Voltage vdc2;
    private final Current idc1;
    private final Current idc2;

    public SpotDCVoltageResponse(Voltage vdc1, Voltage vdc2, Current idc1, Current idc2) {
        this.vdc1 = vdc1;
        this.vdc2 = vdc2;
        this.idc1 = idc1;
        this.idc2 = idc2;
    }

    public Voltage getVdc1() {
        return vdc1;
    }

    public Voltage getVdc2() {
        return vdc2;
    }

    public Current getIdc1() {
        return idc1;
    }

    public Current getIdc2() {
        return idc2;
    }
}
