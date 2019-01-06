package eu.printingin3d.smalogger.api.requestvisitor;

import java.rmi.UnexpectedException;
import java.util.Date;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.ThreePhaseResponse;

public abstract class AbstractInverterThreeIntRequest extends AbstractInverterValueRequest<ThreePhaseResponse<Integer>> {
	private final LriDef lri1;
	private final LriDef lri2;
	private final LriDef lri3;
	
	private int value1;
	private int value2;
	private int value3;

	protected AbstractInverterThreeIntRequest(LriDef lri1, LriDef lri2, LriDef lri3) {
		this.lri1 = lri1;
		this.lri2 = lri2;
		this.lri3 = lri3;
	}

	@Override
	protected final void putValue(LriDef lri, int value, Date datetime) throws UnexpectedException {
		if (lri == lri1) {
			this.value1 = value;
		} else if (lri == lri2) {
			this.value2 = value;
		} else if (lri == lri3) {
			this.value3 = value;
		} else {
			throw new UnexpectedException("Unexpected value: "+lri);
		}
	}

	@Override
	public final ThreePhaseResponse<Integer> closeParse() {
		return new ThreePhaseResponse<>(value1, value2, value3, Integer::valueOf);
	}

}
