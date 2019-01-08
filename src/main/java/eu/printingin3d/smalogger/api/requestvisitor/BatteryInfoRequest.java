package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.BatteryInfoResponse;

public class BatteryInfoRequest extends AbstractInverterValueRequest<BatteryInfoResponse> {
    private int batteryTemperature;
    private int batteryVoltage;
    private int batteryAmpere;

    @Override
    public int getCommand() {
        return 0x51000200;
    }

    @Override
    public int getFirst() {
        return 0x00491E00;
    }

    @Override
    public int getLast() {
        return 0x00495DFF;
    }

    @Override
    protected void putValue(LriDef lri, int cls, int value) throws IOException {
        switch (lri) {
        case BatTmpVal:
            this.batteryTemperature = value;
            break;
        case BatVol:
            this.batteryVoltage = value;
            break;
        case BatAmp:
            this.batteryAmpere = value;
            break;
        default:
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public BatteryInfoResponse closeParse() {
        return new BatteryInfoResponse(batteryTemperature * 0.1, batteryVoltage * 0.01, batteryAmpere * 0.001);
    }
}
