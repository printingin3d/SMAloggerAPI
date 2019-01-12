package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.physics.Power;

public class SpotDcPowerResponse {
    private final Power pdc1;
    private final Power pdc2;

    public SpotDcPowerResponse(Power pdc1, Power pdc2) {
        this.pdc1 = pdc1;
        this.pdc2 = pdc2;
    }

    public Power getPdc1() {
        return pdc1;
    }

    public Power getPdc2() {
        return pdc2;
    }
}
