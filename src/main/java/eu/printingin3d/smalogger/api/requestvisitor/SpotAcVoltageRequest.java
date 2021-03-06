package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.physics.Current;
import eu.printingin3d.physics.Voltage;
import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.ACVoltageAmpereResponse;
import eu.printingin3d.smalogger.api.response.ThreePhaseResponse;

public class SpotAcVoltageRequest extends AbstractInverterValueRequest<ACVoltageAmpereResponse> {
    private int volt1;
    private int volt2;
    private int volt3;

    private int amp1;
    private int amp2;
    private int amp3;

    @Override
    public int getCommand() {
        return 0x51000200;
    }

    @Override
    public int getFirst() {
        return 0x00464800;
    }

    @Override
    public int getLast() {
        return 0x004655FF;
    }

    @Override
    public ACVoltageAmpereResponse closeParse() {
        return new ACVoltageAmpereResponse(
                new ThreePhaseResponse<Voltage>(volt1, volt2, volt3, x -> new Voltage(x * 0.01)),
                new ThreePhaseResponse<Current>(amp1, amp2, amp3, x -> new Current(x * 0.001)));
    }

    @Override
    protected void putValue(LriDef lri, int cls, int value) throws IOException {
        switch (lri) {
        case GridMsPhVphsA:
            this.volt1 = value;
            break;
        case GridMsPhVphsB:
            this.volt2 = value;
            break;
        case GridMsPhVphsC:
            this.volt3 = value;
            break;
        case GridMsAphsA_1:
        case GridMsAphsA:
            this.amp1 = value;
            break;
        case GridMsAphsB_1:
        case GridMsAphsB:
            this.amp2 = value;
            break;
        case GridMsAphsC_1:
        case GridMsAphsC:
            this.amp3 = value;
            break;
        default:
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

}
