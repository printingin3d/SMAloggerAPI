package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.SpotDcPowerResponse;

public class SpotDcPowerRequest extends AbstractInverterValueRequest<SpotDcPowerResponse> {
	private int pdc1;
	private int pdc2;

	@Override
	public long getCommand() {
		return 0x53800200;
	}

	@Override
	public long getFirst() {
		return 0x00251E00;
	}

	@Override
	public long getLast() {
		return 0x00251EFF;
	}

	@Override
	protected void putValue(LriDef lri, int cls, int value) throws IOException {
		if (lri != LriDef.DcMsWatt) {
			throw new UnexpectedValueException("Unexpected value: "+lri);
		}
		
		if (cls == 1) {
			pdc1 = value;
		} else if (cls == 2) {
			pdc2 = value;
		} else {
			throw new UnexpectedValueException("Unexpected cls value: "+cls);
		}
	}

	@Override
	public SpotDcPowerResponse closeParse() {
		return new SpotDcPowerResponse(pdc1, pdc2);
	}

}
