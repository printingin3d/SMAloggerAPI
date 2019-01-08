package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthPacketHeaderL2 {
    private final int magicNumber; // Level 2 packet signature 00 10 60 65

    public EthPacketHeaderL2(ByteBuffer bb) {
        magicNumber = bb.getInt();
        bb.get(); // longWords = int(PacketLen/4)
        bb.get(); // ctrl
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public static int getSize() {
        int size = 0;
        size += Integer.SIZE;
        size += Byte.SIZE;
        size += Byte.SIZE;
        return size / 8;
    }
}
