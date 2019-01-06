package eu.printingin3d.smalogger.api.requestvisitor;

import java.rmi.UnexpectedException;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

public class DeviceStatusRequest extends AbstractInverterAttributeRequest<String> {
	private int status;

	@Override
	public long getCommand() {
		return 0x51800200;
	}

	@Override
	public long getFirst() {
		return 0x00214800;
	}

	@Override
	public long getLast() {
		return 0x002148FF;
	}

	@Override
	protected final void putValue(LriDef lri, int value) throws UnexpectedException {
		if (lri == LriDef.OperationHealth) {
			this.status = value;
		} else {
			throw new UnexpectedException("Unexpected value: "+lri);
		}
	}

	@Override
	public final String closeParse() {
		return TagDefs.getInstance().getDesc(status, "?");
	}
	
}
