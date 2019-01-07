package eu.printingin3d.smalogger.api.requestvisitor;

import java.rmi.UnexpectedException;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

public class GridRelayStatusRequest extends AbstractInverterAttributeRequest<String> {
	private int gridStatus;

	@Override
	public long getCommand() {
		return 0x51800200;
	}

	@Override
	public long getFirst() {
		return 0x00416400;
	}

	@Override
	public long getLast() {
		return 0x004164FF;
	}

	@Override
	protected void putValue(LriDef lri, int cls, int value) throws UnexpectedException {
		if (lri != LriDef.OperationGriSwStt) {
			throw new UnexpectedException("Unexpected value: "+lri);
		}
		
		this.gridStatus = value;
	}

	@Override
	public String closeParse() {
		return TagDefs.getInstance().getDesc(gridStatus, "?");
	}

}
