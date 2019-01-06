package eu.printingin3d.smalogger.api.requestvisitor;

import java.rmi.UnexpectedException;
import java.util.Date;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public abstract class AbstractInverterOneIntRequest extends AbstractInverterValueRequest<Integer> {
	private final LriDef expected;
	
	private int value;

	public AbstractInverterOneIntRequest(LriDef expected) {
		this.expected = expected;
	}

	@Override
	protected final void putValue(LriDef lri, int value, Date datetime) throws UnexpectedException {
		if (lri == expected) {
			this.value = value;
		} else {
			throw new UnexpectedException("Unexpected value: "+lri);
		}
	}

	@Override
	public final Integer closeParse() {
		return Integer.valueOf(value);
	}

}
