package eu.printingin3d.smalogger.api.smaconn;

public enum UserGroup {
    USER(0x07, (char) 0x88, "0000"), INSTALLER(0x0A, (char) 0xBB, "1111");

    private final int value;
    private final char encChar;
    private final String password;

    UserGroup(int value, char encChar, String password) {
        this.value = value;
        this.encChar = encChar;
        this.password = password;
    }

    public char getEncChar() {
        return encChar;
    }

    public int getValue() {
        return value;
    }

    public String getPassword() {
        return password;
    }
}
