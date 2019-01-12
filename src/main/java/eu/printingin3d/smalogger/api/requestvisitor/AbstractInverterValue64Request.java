package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public abstract class AbstractInverterValue64Request<T> extends AbstractInverterRequest<T> {
    private static final long NAN_S64 = 0x8000000000000000L; // "Not a Number" representation for LONGLONG 
                                                            // (converted to 0)
    private static final long NAN_U64 = 0xFFFFFFFFFFFFFFFFL; // "Not a Number" representation for ULONGLONG 
                                                            // (converted to 0)
    
    protected abstract void putValue(LriDef lri, int cls, long value) throws IOException;

    @Override
    protected void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException {
        long value64 = bb.getLong();
        if ((value64 == NAN_S64) || (value64 == NAN_U64)) {
            value64 = 0;
        }

        putValue(lri, cls, value64);
    }
}
