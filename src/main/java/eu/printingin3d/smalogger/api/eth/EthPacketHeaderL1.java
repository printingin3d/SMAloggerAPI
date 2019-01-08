package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacketHeaderL1 {
    private final byte hiPacketLen; // Packet length stored as big endian
    private final byte loPacketLen; // Packet length Low Byte

    public EthPacketHeaderL1(ByteBuffer bb) {
        bb.getInt(); // MagicNumber = Packet signature 53 4d 41 00 (SMA\0)
        // Just skip 2x32 bit of unknown data
        // 00 04 02 a0
        // 00 00 00 01
        bb.getInt();
        bb.getInt();
        hiPacketLen = bb.get();
        loPacketLen = bb.get();
    }

    public byte getHiPacketLen() {
        return hiPacketLen;
    }

    public byte getLoPacketLen() {
        return loPacketLen;
    }

    public static int getSize() {
        int size = 0;
        size += Integer.SIZE;
        size += Integer.SIZE;
        size += Integer.SIZE;
        size += Byte.SIZE;
        size += Byte.SIZE;
        return size / 8;
    }
}
