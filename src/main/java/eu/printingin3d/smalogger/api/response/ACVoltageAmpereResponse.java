package eu.printingin3d.smalogger.api.response;

public class ACVoltageAmpereResponse {
	private final ThreePhaseResponse<Double> voltage;
	private final ThreePhaseResponse<Double> ampere;
	
	public ACVoltageAmpereResponse(ThreePhaseResponse<Double> voltage, ThreePhaseResponse<Double> ampere) {
		this.voltage = voltage;
		this.ampere = ampere;
	}

	public ThreePhaseResponse<Double> getVoltage() {
		return voltage;
	}

	public ThreePhaseResponse<Double> getAmpere() {
		return ampere;
	}
}
