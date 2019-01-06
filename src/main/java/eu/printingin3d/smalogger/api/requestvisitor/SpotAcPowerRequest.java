package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SpotAcPowerRequest extends AbstractInverterThreeIntRequest {
	public SpotAcPowerRequest() {
		super(LriDef.GridMsWphsA, LriDef.GridMsWphsB, LriDef.GridMsWphsC);
	}
	
	@Override
	public long getCommand() {
		return 0x51000200;
	}

	@Override
	public long getFirst() {
		return 0x00464000;
	}

	@Override
	public long getLast() {
		return 0x004642FF;
	}
}
