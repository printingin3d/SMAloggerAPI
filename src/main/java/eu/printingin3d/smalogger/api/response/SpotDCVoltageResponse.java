package eu.printingin3d.smalogger.api.response;

public class SpotDCVoltageResponse {
    private final double vdc1;
    private final double vdc2;
    private final double idc1;
    private final double idc2;

    public SpotDCVoltageResponse(double vdc1, double vdc2, double idc1, double idc2) {
        this.vdc1 = vdc1;
        this.vdc2 = vdc2;
        this.idc1 = idc1;
        this.idc2 = idc2;
    }

    public double getVdc1() {
        return vdc1;
    }

    public double getVdc2() {
        return vdc2;
    }

    public double getIdc1() {
        return idc1;
    }

    public double getIdc2() {
        return idc2;
    }
}
