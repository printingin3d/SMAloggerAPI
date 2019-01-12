package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.physics.Power;
import eu.printingin3d.smalogger.api.inverter.LriDef;

public class MaxACPowerRequest extends AbstractInverterThreeIntRequest<Power> {

    public MaxACPowerRequest() {
        super(LriDef.OperationHealthSttOk, LriDef.OperationHealthSttWrn, LriDef.OperationHealthSttAlm,
                x -> new Power(x));
    }

    @Override
    public int getCommand() {
        return 0x51000200;
    }

    @Override
    public int getFirst() {
        return 0x00411E00;
    }

    @Override
    public int getLast() {
        return 0x004120FF;
    }
}
