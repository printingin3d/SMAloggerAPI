package eth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EthPacket 
{
	public EthPacketHeaderL2 pcktHdrL2;
	public EthEndpoint Destination;
	public EthEndpoint Source;
    public short ErrorCode;
    public short FragmentID;  //Count Down
    public short PacketID;    //Count Up
    
    public EthPacket(byte[] packet)
    {
    	ByteBuffer bb = ByteBuffer.wrap(packet);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.get();

        pcktHdrL2 = new EthPacketHeaderL2(bb);
        Destination = new EthEndpoint(bb);
        Source = new EthEndpoint(bb);
        ErrorCode = bb.getShort();
        FragmentID = bb.getShort();
        PacketID = bb.getShort();
    }
}
