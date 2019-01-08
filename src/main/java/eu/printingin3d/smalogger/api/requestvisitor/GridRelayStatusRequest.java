package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

public class GridRelayStatusRequest extends AbstractInverterAttributeRequest<String> {
	private int gridStatus;

	@Override
	public int getCommand() {
		return 0x51800200;
	}

	@Override
	public int getFirst() {
		return 0x00416400;
	}

	@Override
	public int getLast() {
		return 0x004164FF;
	}

	@Override
	protected void putValue(LriDef lri, int cls, int value) throws IOException {
		if (lri != LriDef.OperationGriSwStt) {
			throw new UnexpectedValueException("Unexpected value: "+lri);
		}
		
		this.gridStatus = value;
	}

	@Override
	public String closeParse() {
		return TagDefs.getInstance().getDesc(gridStatus, "?");
	}

}
