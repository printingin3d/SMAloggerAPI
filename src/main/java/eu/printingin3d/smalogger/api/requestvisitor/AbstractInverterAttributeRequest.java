package eu.printingin3d.smalogger.api.requestvisitor;

import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public abstract class AbstractInverterAttributeRequest<T> extends AbstractInverterRequest<T> {
	protected abstract void putValue(LriDef lri, int value) throws UnexpectedException;

	@Override
	protected final void parse(LriDef lri, ByteBuffer bb) throws UnexpectedException {
    	for (int idx = 8; idx < lri.getRecordSize(); idx += 4) {
			int value = bb.getInt();
            if (value == 0xFFFFFE) {
				break;	//End of attributes
			}
			
	        int attribute = value & 0x00FFFFFF;
	        if ((value & 0xFF000000) == 0x01000000) {
	            putValue(lri, attribute);
	        }
        }
	}
}
