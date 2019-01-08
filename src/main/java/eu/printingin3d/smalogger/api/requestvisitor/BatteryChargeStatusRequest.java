package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class BatteryChargeStatusRequest extends AbstractInverterOneIntRequest<Integer> {

	public BatteryChargeStatusRequest() {
		super(LriDef.BatChaStt, Integer::valueOf);
	}

	@Override
	public int getCommand() {
		return 0x51000200;
	}

	@Override
	public int getFirst() {
		return 0x00295A00;
	}

	@Override
	public int getLast() {
		return 0x00295AFF;
	}

}
