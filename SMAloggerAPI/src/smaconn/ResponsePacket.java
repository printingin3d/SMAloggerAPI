package smaconn;

public class ResponsePacket {
	private static final int MAX_BUF_SIZE = 520;
	
	public int packetposition = 0;
	public byte[] pcktBuf = new byte[MAX_BUF_SIZE];

}
