package eu.printingin3d.smalogger.api.requestvisitor;

import java.rmi.UnexpectedException;
import java.util.function.IntFunction;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public abstract class AbstractInverterOneIntRequest<T> extends AbstractInverterValueRequest<T> {
	private final LriDef expected;
	private final IntFunction<T> converter;
	
	private int value;

	public AbstractInverterOneIntRequest(LriDef expected, IntFunction<T> converter) {
		this.expected = expected;
		this.converter = converter;
	}

	@Override
	protected final void putValue(LriDef lri, int cls, int value) throws UnexpectedException {
		if (lri == expected) {
			this.value = value;
		} else {
			throw new UnexpectedException("Unexpected value: "+lri);
		}
	}

	@Override
	public final T closeParse() {
		return converter.apply(value);
	}

}
