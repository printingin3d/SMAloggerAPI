package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacket {
    private final EthEndpoint source;
    private final short errorCode;
    private final short packetID; // Count Up

    public EthPacket(ByteBuffer bb) {
        bb.get();
        bb.position(bb.position() + EthPacketHeaderL2.getSize() + EthEndpoint.getSize());

        source = new EthEndpoint(bb);
        errorCode = bb.getShort();
        bb.getShort(); // FragmentID Count Down
        packetID = bb.getShort();
    }

    public EthEndpoint getSource() {
        return source;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public short getPacketID() {
        return packetID;
    }

}
