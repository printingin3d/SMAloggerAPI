package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SpotAcTotalPowerRequest extends AbstractInverterOneIntRequest {

	public SpotAcTotalPowerRequest() {
		super(LriDef.GridMsTotW);
	}

	@Override
	public long getCommand() {
		return 0x51000200;
	}

	@Override
	public long getFirst() {
		return 0x00263F00;
	}

	@Override
	public long getLast() {
		return 0x00263FFF;
	}

}
