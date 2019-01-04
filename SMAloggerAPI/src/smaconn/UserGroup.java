package smaconn;

public enum UserGroup {
	USER(0x07L, (char)0x88),
	INSTALLER(0x0AL, (char)0xBB);

	private final long value;
	private final char encChar;
	
	UserGroup(long value, char encChar) {
		this.value = value;
		this.encChar = encChar;
	}

	public char getEncChar() {
		return encChar;
	}

	public long getValue() {
		return value;
	}

}
