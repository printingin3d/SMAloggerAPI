package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EthPacketHeaderL1L2 {
	private final EthPacketHeaderL1 pcktHdrL1;
    private final EthPacketHeaderL2 pcktHdrL2;
   
	public EthPacketHeaderL1L2(byte[] packet) {
		ByteBuffer bb = ByteBuffer.wrap(packet);
        bb.order(ByteOrder.LITTLE_ENDIAN);
		pcktHdrL1 = new EthPacketHeaderL1(bb);
		pcktHdrL2 = new EthPacketHeaderL2(bb);
	}
	
	public EthPacketHeaderL1 getPcktHdrL1() {
		return pcktHdrL1;
	}

	public EthPacketHeaderL2 getPcktHdrL2() {
		return pcktHdrL2;
	}

	public static int getSize() {
		return (EthPacketHeaderL1.getSize() + EthPacketHeaderL2.getSize());
	}
}
