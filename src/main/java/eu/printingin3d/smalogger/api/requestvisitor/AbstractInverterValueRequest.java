package eu.printingin3d.smalogger.api.requestvisitor;

import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;
import java.util.Date;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.Misc;

public abstract class AbstractInverterValueRequest<T> extends AbstractInverterRequest<T> {
	protected abstract void putValue(LriDef lri, int value, Date datetime) throws UnexpectedException;

	@Override
	protected final void parse(LriDef lri, ByteBuffer bb, Date datetime) throws UnexpectedException {
    	//All data that needs an int value
        int value = bb.getInt();
        if ((value == Misc.NaN_S32) || (value == Misc.NaN_U32)) {
			value = 0;
		}

        putValue(lri, value, datetime);
	}

}
