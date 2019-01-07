package eu.printingin3d.smalogger.api.requestvisitor;

public class MaxACPower2Request extends MaxACPowerRequest {
	@Override
	public long getCommand() {
		return 0x51000200;
	}

	@Override
	public long getFirst() {
		return 0x00832A00;
	}

	@Override
	public long getLast() {
		return 0x00832AFF;
	}

}
