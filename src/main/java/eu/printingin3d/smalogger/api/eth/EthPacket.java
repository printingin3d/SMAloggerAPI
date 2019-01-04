package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacket 
{
	public EthPacketHeaderL2 pcktHdrL2;
	public EthEndpoint Destination;
	public EthEndpoint Source;
    public short ErrorCode;
    public short FragmentID;  //Count Down
    public short PacketID;    //Count Up
    
    public EthPacket(ByteBuffer bb)
    {
        bb.get();

        pcktHdrL2 = new EthPacketHeaderL2(bb);
        Destination = new EthEndpoint(bb);
        Source = new EthEndpoint(bb);
        ErrorCode = bb.getShort();
        FragmentID = bb.getShort();
        PacketID = bb.getShort();
    }
}
