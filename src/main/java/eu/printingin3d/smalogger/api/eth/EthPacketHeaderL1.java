package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacketHeaderL1 
{
    public int  MagicNumber;      // Packet signature 53 4d 41 00 (SMA\0)
    public byte hiPacketLen;      // Packet length stored as big endian
    public byte loPacketLen ;     // Packet length Low Byte
    
    public EthPacketHeaderL1(ByteBuffer bb)
    {
    	MagicNumber = bb.getInt();
    	// Just skip 2x32 bit of unknown data
    	// 00 04 02 a0
        // 00 00 00 01
        bb.getInt();
        bb.getInt();
        hiPacketLen = bb.get();
        loPacketLen = bb.get();
    }
    
    public static short getSize()
    {
    	short size = 0;
    	size += Integer.SIZE / 8;
    	size += Integer.SIZE / 8;
    	size += Integer.SIZE / 8;
    	size += Byte.SIZE / 8;
    	size += Byte.SIZE / 8;
    	return size;
    }
}
