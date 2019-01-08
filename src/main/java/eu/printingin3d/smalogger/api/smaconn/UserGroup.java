package eu.printingin3d.smalogger.api.smaconn;

public enum UserGroup {
	USER(0x07, (char)0x88),
	INSTALLER(0x0A, (char)0xBB);

	private final int value;
	private final char encChar;
	
	UserGroup(int value, char encChar) {
		this.value = value;
		this.encChar = encChar;
	}

	public char getEncChar() {
		return encChar;
	}

	public int getValue() {
		return value;
	}

}
