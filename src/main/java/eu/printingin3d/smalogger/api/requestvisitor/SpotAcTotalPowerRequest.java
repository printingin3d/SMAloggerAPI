package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.physics.Power;
import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SpotAcTotalPowerRequest extends AbstractInverterOneIntRequest<Power> {

    public SpotAcTotalPowerRequest() {
        super(LriDef.GridMsTotW, x -> new Power(x));
    }

    @Override
    public int getCommand() {
        return 0x51000200;
    }

    @Override
    public int getFirst() {
        return 0x00263F00;
    }

    @Override
    public int getLast() {
        return 0x00263FFF;
    }

}
