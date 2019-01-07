package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.Misc;

public abstract class AbstractInverterValue64Request<T> extends AbstractInverterRequest<T> {
	protected abstract void putValue(LriDef lri, int cls, long value) throws IOException;

	@Override
	protected void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException {
    	long value64 = bb.getLong();
        if ((value64 == Misc.NaN_S64) || (value64 == Misc.NaN_U64)) {
			value64 = 0;
		}
        
        putValue(lri, cls, value64);
	}
}
