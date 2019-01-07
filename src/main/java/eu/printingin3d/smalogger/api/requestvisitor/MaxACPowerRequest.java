package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class MaxACPowerRequest extends AbstractInverterThreeIntRequest {

	public MaxACPowerRequest() {
		super(LriDef.OperationHealthSttOk, LriDef.OperationHealthSttWrn, LriDef.OperationHealthSttAlm);
	}

	@Override
	public long getCommand() {
		return 0x51000200;
	}

	@Override
	public long getFirst() {
		return 0x00411E00;
	}

	@Override
	public long getLast() {
		return 0x004120FF;
	}
}
