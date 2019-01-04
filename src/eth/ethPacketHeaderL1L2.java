package eth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EthPacketHeaderL1L2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(EthPacketHeaderL1L2.class);
	
	public EthPacketHeaderL1 pcktHdrL1;
    public EthPacketHeaderL2 pcktHdrL2;
   
	public EthPacketHeaderL1L2(byte[] packet)
	{
		ByteBuffer bb = ByteBuffer.wrap(packet);
        bb.order(ByteOrder.LITTLE_ENDIAN);
		pcktHdrL1 = new EthPacketHeaderL1(bb);
		pcktHdrL2 = new EthPacketHeaderL2(bb);
	}
	
	public void setSize(int packetposition)
	{
		short dataLength = (short)(packetposition - getSize());
		LOGGER.debug("=========== size is: " + dataLength);
        pcktHdrL1.hiPacketLen = (byte)((dataLength >> 8) & 0xFF);
        pcktHdrL1.loPacketLen = (byte)(dataLength & 0xFF);
	}
	
	public static short getSize()
	{
		return (short)(EthPacketHeaderL1.getSize() + EthPacketHeaderL2.getSize());
	}
}
