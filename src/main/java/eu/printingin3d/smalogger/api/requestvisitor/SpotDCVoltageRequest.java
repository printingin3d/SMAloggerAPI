package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.physics.Current;
import eu.printingin3d.physics.Voltage;
import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.SpotDCVoltageResponse;

public class SpotDCVoltageRequest extends AbstractInverterValueRequest<SpotDCVoltageResponse> {
    private int vdc1;
    private int vdc2;
    private int idc1;
    private int idc2;

    @Override
    public int getCommand() {
        return 0x53800200;
    }

    @Override
    public int getFirst() {
        return 0x00451F00;
    }

    @Override
    public int getLast() {
        return 0x004521FF;
    }

    @Override
    protected void putValue(LriDef lri, int cls, int value) throws IOException {
        switch (lri) {
        case DcMsVol:
            if (cls == 1) {
                vdc1 = value;
            } else if (cls == 2) {
                vdc2 = value;
            } else {
                throw new UnexpectedValueException("Unexpected cls value: " + cls);
            }
            break;
        case DcMsAmp:
            if (cls == 1) {
                idc1 = value;
            } else if (cls == 2) {
                idc2 = value;
            } else {
                throw new UnexpectedValueException("Unexpected cls value: " + cls);
            }
            break;
        default:
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public SpotDCVoltageResponse closeParse() {
        return new SpotDCVoltageResponse(new Voltage(vdc1 * 0.01), new Voltage(vdc2 * 0.01), 
                new Current(idc1 * 0.001), new Current(idc2 * 0.001));
    }

}
