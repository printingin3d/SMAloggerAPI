package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SpotGridFrequencyRequest extends AbstractInverterOneIntRequest<Double> {

	public SpotGridFrequencyRequest() {
		super(LriDef.GridMsHz, x -> Double.valueOf(x * 0.01));
	}

	@Override
	public int getCommand() {
		return 0x51000200;
	}

	@Override
	public int getFirst() {
		return 0x00465700;
	}

	@Override
	public int getLast() {
		return 0x004657FF;
	}

}
