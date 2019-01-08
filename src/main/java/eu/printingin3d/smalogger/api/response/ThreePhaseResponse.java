package eu.printingin3d.smalogger.api.response;

import java.util.function.IntFunction;

public class ThreePhaseResponse<T> {
    private final T value1;
    private final T value2;
    private final T value3;
    private final T total;

    public ThreePhaseResponse(int value1, int value2, int value3, IntFunction<T> convert) {
        this.value1 = convert.apply(value1);
        this.value2 = convert.apply(value2);
        this.value3 = convert.apply(value3);
        this.total = convert.apply(value1 + value2 + value3);
    }

    public ThreePhaseResponse(T value1, T value2, T value3, T total) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.total = total;
    }

    public T getValue1() {
        return value1;
    }

    public T getValue2() {
        return value2;
    }

    public T getValue3() {
        return value3;
    }

    public T getTotal() {
        return total;
    }
}
