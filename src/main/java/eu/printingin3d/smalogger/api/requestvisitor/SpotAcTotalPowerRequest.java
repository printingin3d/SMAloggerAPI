package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SpotAcTotalPowerRequest extends AbstractInverterOneIntRequest<Integer> {

    public SpotAcTotalPowerRequest() {
        super(LriDef.GridMsTotW, Integer::valueOf);
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
