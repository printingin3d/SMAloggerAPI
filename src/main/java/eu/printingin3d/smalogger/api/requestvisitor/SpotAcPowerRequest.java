package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.physics.Power;
import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SpotAcPowerRequest extends AbstractInverterThreeIntRequest<Power> {
    public SpotAcPowerRequest() {
        super(LriDef.GridMsWphsA, LriDef.GridMsWphsB, LriDef.GridMsWphsC,
                x -> new Power(x));
    }

    @Override
    public int getCommand() {
        return 0x51000200;
    }

    @Override
    public int getFirst() {
        return 0x00464000;
    }

    @Override
    public int getLast() {
        return 0x004642FF;
    }
}
