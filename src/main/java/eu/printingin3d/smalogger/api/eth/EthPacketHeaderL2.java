package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacketHeaderL2 {
	private final int  MagicNumber;      // Level 2 packet signature 00 10 60 65
    private final byte longWords;        // int(PacketLen/4)
    private final byte ctrl;
    
    public EthPacketHeaderL2(ByteBuffer bb) {
    	MagicNumber = bb.getInt();
        longWords = bb.get();
        ctrl = bb.get();
    }
    
    public int getMagicNumber() {
		return MagicNumber;
	}

	public static int getSize() {
    	int size = 0;
    	size += Integer.SIZE;
    	size += Byte.SIZE;
    	size += Byte.SIZE;
    	return size / 8;
    }
}
