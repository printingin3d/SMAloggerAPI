package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.util.function.IntFunction;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
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
    protected final void putValue(LriDef lri, int cls, int value) throws IOException {
        if (lri == expected) {
            this.value = value;
        } else {
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public final T closeParse() {
        return converter.apply(value);
    }

}
