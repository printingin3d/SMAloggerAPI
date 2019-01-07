package eu.printingin3d.smalogger.api.requestvisitor;

import java.rmi.UnexpectedException;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.SpotDCVoltageResponse;

public class SpotDCVoltageRequest extends AbstractInverterValueRequest<SpotDCVoltageResponse> {
	private int vdc1;
	private int vdc2;
	private int idc1;
	private int idc2;

	@Override
	public long getCommand() {
		return 0x53800200;
	}

	@Override
	public long getFirst() {
		return 0x00451F00;
	}

	@Override
	public long getLast() {
		return 0x004521FF;
	}

	@Override
	protected void putValue(LriDef lri, int cls, int value) throws UnexpectedException {
		switch (lri) {
		case DcMsVol:
			if (cls==1) {
				vdc1 = value;
			} else if (cls==2) {
				vdc2 = value;
			} else {
				throw new UnexpectedException("Unexpected cls value: "+cls);
			}
			break;
		case DcMsAmp:
			if (cls==1) {
				idc1 = value;
			} else if (cls==2) {
				idc2 = value;
			} else {
				throw new UnexpectedException("Unexpected cls value: "+cls);
			}
			break;
		default:
			throw new UnexpectedException("Unexpected value: "+lri);
		}
	}

	@Override
	public SpotDCVoltageResponse closeParse() {
		return new SpotDCVoltageResponse(vdc1 * 0.01, vdc2 * 0.01, idc1 * 0.001, idc2 * 0.001);
	}

}
