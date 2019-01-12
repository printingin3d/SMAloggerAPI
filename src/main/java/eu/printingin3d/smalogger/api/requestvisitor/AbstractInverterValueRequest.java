package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public abstract class AbstractInverterValueRequest<T> extends AbstractInverterRequest<T> {
    private static final int NAN_S32 = (int) 0x80000000L; // "Not a Number" representation for LONG (converted to 0)
    private static final int NAN_U32 = (int) 0xFFFFFFFFL; // "Not a Number" representation for ULONG (converted to 0)
    
    protected abstract void putValue(LriDef lri, int cls, int value) throws IOException;

    @Override
    protected final void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException {
        // All data that needs an int value
        int value = bb.getInt();
        if ((value == NAN_S32) || (value == NAN_U32)) {
            value = 0;
        }

        putValue(lri, cls, value);
    }

}
