package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthEndpoint {
	private final short SUSyID;
	private final int   Serial;
	private final short Ctrl;
	
	public EthEndpoint(ByteBuffer bb) {
		SUSyID = bb.getShort();
		Serial = bb.getInt();
		Ctrl = bb.getShort();
	}
    
    public short getSUSyID() {
		return SUSyID;
	}

	public int getSerial() {
		return Serial;
	}

	public static int getSize() {
    	int size = 0;
    	size += Short.SIZE;
    	size += Integer.SIZE;
    	size += Short.SIZE;
    	return size;
    }

}
