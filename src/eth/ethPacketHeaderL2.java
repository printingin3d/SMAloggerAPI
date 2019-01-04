package eth;

import java.nio.ByteBuffer;

public class EthPacketHeaderL2 
{
	public int      MagicNumber;      // Level 2 packet signature 00 10 60 65
    public byte longWords;        // int(PacketLen/4)
    public byte ctrl;
    
    public EthPacketHeaderL2(ByteBuffer bb)
    {
    	MagicNumber = bb.getInt();
        longWords = bb.get();
        ctrl = bb.get();
    }
    
    public static short getSize()
    {
    	short size = 0;
    	size += Integer.SIZE / 8;
    	size += Byte.SIZE / 8;
    	size += Byte.SIZE / 8;
    	return size;
    }
}
