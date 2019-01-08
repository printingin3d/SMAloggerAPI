package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthEndpoint {
    private final short sUSyID;
    private final int serial;

    public EthEndpoint(ByteBuffer bb) {
        sUSyID = bb.getShort();
        serial = bb.getInt();
        bb.getShort();
    }

    public short getSUSyID() {
        return sUSyID;
    }

    public int getSerial() {
        return serial;
    }

    public static int getSize() {
        int size = 0;
        size += Short.SIZE;
        size += Integer.SIZE;
        size += Short.SIZE;
        return size / 8;
    }

}
