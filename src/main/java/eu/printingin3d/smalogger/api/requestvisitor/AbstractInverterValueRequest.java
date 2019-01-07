package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.Misc;

public abstract class AbstractInverterValueRequest<T> extends AbstractInverterRequest<T> {
	protected abstract void putValue(LriDef lri, int cls, int value) throws IOException;

	@Override
	protected final void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException {
    	//All data that needs an int value
        int value = bb.getInt();
        if ((value == Misc.NaN_S32) || (value == Misc.NaN_U32)) {
			value = 0;
		}

        putValue(lri, cls, value);
	}

}
