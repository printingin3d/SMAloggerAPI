package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacket {
	private final EthEndpoint Source;
    private final short ErrorCode;
    private final short PacketID;    //Count Up
    
    public EthPacket(ByteBuffer bb) {
        bb.get();
        bb.position(bb.position()+EthPacketHeaderL2.getSize()+EthEndpoint.getSize());

        Source = new EthEndpoint(bb);
        ErrorCode = bb.getShort();
        bb.getShort(); // FragmentID Count Down
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
