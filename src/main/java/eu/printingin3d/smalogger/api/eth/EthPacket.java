package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacket {
	private final EthEndpoint Destination;
	private final EthEndpoint Source;
    private final short ErrorCode;
    private final short FragmentID;  //Count Down
    private final short PacketID;    //Count Up
    
    public EthPacket(ByteBuffer bb) {
        bb.get();
        bb.position(bb.position()+EthPacketHeaderL2.getSize());

        Destination = new EthEndpoint(bb);
        Source = new EthEndpoint(bb);
        ErrorCode = bb.getShort();
        FragmentID = bb.getShort();
        PacketID = bb.getShort();
    }

	public EthEndpoint getSource() {
		return Source;
	}

	public short getErrorCode() {
		return ErrorCode;
	}

	public short getPacketID() {
		return PacketID;
	}
    
}
