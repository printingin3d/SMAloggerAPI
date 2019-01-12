package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.util.function.IntFunction;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.ThreePhaseResponse;

public abstract class AbstractInverterThreeIntRequest<T>
        extends AbstractInverterValueRequest<ThreePhaseResponse<T>> {
    private final LriDef lri1;
    private final LriDef lri2;
    private final LriDef lri3;
    private final IntFunction<T> convert;

    private int value1;
    private int value2;
    private int value3;

    protected AbstractInverterThreeIntRequest(LriDef lri1, LriDef lri2, LriDef lri3, IntFunction<T> convert) {
        this.lri1 = lri1;
        this.lri2 = lri2;
        this.lri3 = lri3;
        this.convert = convert;
    }

    @Override
    protected final void putValue(LriDef lri, int cls, int value) throws IOException {
        if (lri == lri1) {
            this.value1 = value;
        } else if (lri == lri2) {
            this.value2 = value;
        } else if (lri == lri3) {
            this.value3 = value;
        } else {
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public final ThreePhaseResponse<T> closeParse() {
        return new ThreePhaseResponse<>(value1, value2, value3, convert);
    }

}
