package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class BatteryChargeStatusRequest extends AbstractInverterOneIntRequest<Integer> {

	public BatteryChargeStatusRequest() {
		super(LriDef.BatChaStt, Integer::valueOf);
	}

	@Override
	public long getCommand() {
		return 0x51000200;
	}

	@Override
	public long getFirst() {
		return 0x00295A00;
	}

	@Override
	public long getLast() {
		return 0x00295AFF;
	}

}
