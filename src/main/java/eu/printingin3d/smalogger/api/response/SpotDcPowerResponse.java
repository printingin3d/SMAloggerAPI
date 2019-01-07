package eu.printingin3d.smalogger.api.response;

public class SpotDcPowerResponse {
	private final int pdc1;
	private final int pdc2;
	
	public SpotDcPowerResponse(int pdc1, int pdc2) {
		this.pdc1 = pdc1;
		this.pdc2 = pdc2;
	}

	public int getPdc1() {
		return pdc1;
	}

	public int getPdc2() {
		return pdc2;
	}
}
