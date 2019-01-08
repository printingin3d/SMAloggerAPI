package eu.printingin3d.smalogger.api.inverter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.eth.EthPacket;
import eu.printingin3d.smalogger.api.eth.EthPacketHeaderL1;
import eu.printingin3d.smalogger.api.eth.EthPacketHeaderL1L2;
import eu.printingin3d.smalogger.api.exception.InvalidPasswordException;
import eu.printingin3d.smalogger.api.exception.NoDataReceivedException;
import eu.printingin3d.smalogger.api.inverterdata.IInverterCommand;
import eu.printingin3d.smalogger.api.requestvisitor.AbstractInverterRequest;
import eu.printingin3d.smalogger.api.smaconn.Ethernet;
import eu.printingin3d.smalogger.api.smaconn.UserGroup;
import eu.printingin3d.smalogger.api.smajava.Misc;

public class Inverter implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Inverter.class);

    private static final int MAX_PWLENGTH = 12;
    private static final int COMMBUFSIZE = 1024;
    private final short anySUSyID = (short) 0xFFFF;
    private final int anySerial = 0xFFFFFFFF;

    private short sUSyID;
    private long serial;
    private final Ethernet ethernet;
    private final String ip;

    /**
     * Creates a new inverter. NOTE: This method is used by the SMALogger and should
     * not be used manually. Use
     * {@link eu.printingin3d.smalogger.api.smajava.SmaLogger#createInverter(String)}
     * to create an inverter instead.
     * 
     * @param ip       The ip address of the inverter.
     * @param ethernet The Ethernet connection.
     */
    public Inverter(String ip, Ethernet ethernet) {
        // Each inverters has his own connection but uses the same ethernet socket.
        // Sma connection constructor
        this.ethernet = ethernet;
        this.ip = ip;
    }

    private void initConnection() throws IOException {
        ethernet.writePacketHeader();
        ethernet.writePacket((char) 0x09, (char) 0xA0, (short) 0, anySUSyID, anySerial);
        ethernet.writeInt(0x00000200);
        ethernet.writeInt(0);
        ethernet.writeInt(0);
        ethernet.writeInt(0);
        ethernet.writePacketLength();

        // Send packet to first inverter
        ethernet.send(ip);
    }

    private void smaLogin(UserGroup userGroup) throws IOException {
        char[] pw = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        LOGGER.info("smaLogin()");

        char[] password = userGroup.getPassword().toCharArray();
        char encChar = userGroup.getEncChar();
        // Encode password
        int idx;
        for (idx = 0; idx < password.length && password[idx] != 0 && idx <= pw.length; idx++) {
            pw[idx] = (char) (password[idx] + encChar);
        }
        for (; idx < MAX_PWLENGTH; idx++) {
            pw[idx] = encChar;
        }

        // I believe the inverter times is using seconds instead of milliseconds.
        int now = (int) (System.currentTimeMillis() / 1000);
        ethernet.writePacketHeader();
        ethernet.writePacket((char) 0x0E, (char) 0xA0, (short) 0x0100, anySUSyID, anySerial);
        ethernet.writeInt(0xFFFD040C);
        ethernet.writeInt(userGroup.getValue());
        ethernet.writeInt(0x00000384);
        ethernet.writeInt(now);
        ethernet.writeInt(0);
        ethernet.writeArray(pw);
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();

        ethernet.send(ip);
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("SMALogoff()");

        ethernet.writePacketHeader();
        ethernet.writePacket((char) 0x08, (char) 0xA0, (short) 0x0300, anySUSyID, anySerial);
        ethernet.writeInt(0xFFFD010E);
        ethernet.writeInt(0xFFFFFFFF);
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();

        ethernet.send(ip);
    }

    private void requestInverterData(IInverterCommand dataType) throws IOException {
        ethernet.writePacketHeader();
        ethernet.writePacket((char) 0x09, (char) 0xA0, (short) 0, anySUSyID, anySerial);
        // Get the command values stored in the enum.
        ethernet.writeInt(dataType.getCommand());
        ethernet.writeInt(dataType.getFirst());
        ethernet.writeInt(dataType.getLast());
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();

        ethernet.send(ip);
    }

    private ByteBuffer getPacket() throws IOException {
        byte[] commBuf = new byte[COMMBUFSIZE];
        LOGGER.info("ethGetPacket()");

        while (true) {
            int bib = ethernet.read(commBuf);

            if (bib <= 0) {
                throw new NoDataReceivedException("No data!");
            } else {
                EthPacketHeaderL1L2 pkHdr = new EthPacketHeaderL1L2(commBuf);
                int pkLen = ((pkHdr.getPcktHdrL1().getHiPacketLen() << 8) + pkHdr.getPcktHdrL1().getLoPacketLen())
                        & 0xff; // 0xff to convert it to unsigned?

                // More data after header?
                if (pkLen > 0) {
                    if (LOGGER.isTraceEnabled()) {
                        Misc.hexDump(commBuf, bib, 10);
                    }

                    if (pkHdr.getPcktHdrL2().getMagicNumber() == Ethernet.ETH_L2SIGNATURE) {
                        ByteBuffer bb = ByteBuffer.allocate(bib - EthPacketHeaderL1.getSize() + 1);
                        bb.order(ByteOrder.LITTLE_ENDIAN);
                        // Dummy byte to align with BTH (7E)
                        // We need last 6 bytes of ethPacketHeader too
                        System.arraycopy(commBuf, EthPacketHeaderL1.getSize(), bb.array(), 1,
                                bib - EthPacketHeaderL1.getSize());

                        if (LOGGER.isTraceEnabled()) {
                            LOGGER.debug("<<<====== Content of pcktBuf =======>>>");
                            Misc.hexDump(bb.array(), bib - EthPacketHeaderL1.getSize(), 16);
                            LOGGER.debug("<<<=================================>>>");
                        }
                        return bb;
                    } else {
                        LOGGER.info("L2 header not found.\n");
                    }
                } else {
                    throw new NoDataReceivedException("No data!");
                }
            }
        }
    }

    /**
     * Returns the ip adress of this inverter.
     * 
     * @return A string containing the IP adress of the inverter.
     */
    public String getIP() {
        return ip;
    }

    public short getSUSyID() {
        return sUSyID;
    }

    public long getSerial() {
        return serial;
    }

    /**
     * Sends a logon request to the inverter which creates a connection. Use this
     * before getting data from the inverter but after the main connection was
     * created. NOTE: Only able to log on as a user now!
     * 
     * @param password The password used to login to the inverter.
     * @return Returns 0 if everything went ok.
     * @throws IOException
     */
    public void logon() throws IOException {
        logon(UserGroup.USER);
    }

    /**
     * Sends a logon request to the inverter which creates a connection. Use this
     * before getting data from the inverter but after the main connection was
     * created.
     * 
     * @param userGroup The usergroup: (USER or INSTALLER)
     * @param password  The password used to login to the inverter.
     * @return Returns 0 if everything went ok.
     * @throws IOException
     */
    private void logon(UserGroup userGroup) throws IOException {
        // First initialize the connection.
        initConnection();
        ByteBuffer packet = getPacket();
        EthPacket pckt2 = new EthPacket(packet);
        sUSyID = pckt2.getSource().getSUSyID();
        serial = pckt2.getSource().getSerial();
        // Then login.
        smaLogin(userGroup);
        ByteBuffer packet2 = getPacket();
        EthPacket pckt = new EthPacket(packet2);
        if (ethernet.getPcktID() != ((pckt.getPacketID()) & 0x7FFF)) { // InValid Packet ID
            throw new IOException(String.format("Packet ID mismatch. Expected %d, received %d", ethernet.getPcktID(),
                    ((pckt.getPacketID()) & 0x7FFF)));
        }

        if (pckt.getErrorCode() == 0x0100) {
            close();
            throw new InvalidPasswordException("Logon failed. Check '" + userGroup + "' Password");
        }
    }

    /**
     * Requests data from the inverter which gets stored in it's Data attribute.
     * Uses Data.(Name of the value you requested) to get the actual value this
     * method requested.
     * 
     * @param invDataType The type of data you want to retrieve from the inverter.
     * @return Returns 0 if everything went ok.
     * @throws IOException
     */
    public <T> T getInverterData(AbstractInverterRequest<T> request) throws IOException {
        LOGGER.info("getInverterData({})", request.getClass().getName());

        requestInverterData(request);

        while (true) {
            ByteBuffer packet = getPacket();

            byte c = packet.get(25);
            System.out.println("Counter: " + c);

            short rcvpcktID = (short) (packet.get(27) & 0x7FFF);
            if (ethernet.getPcktID() == rcvpcktID) {
                // Check if we received the package from the right inverter, not sure if
                // this works with multiple inverters.
                // We do this by checking if the susyd and serial is equal to this inverter
                // object's susyd and serial.
                boolean rightOne = sUSyID == packet.getShort(15) && serial == packet.getInt(17);

                if (rightOne) {
                    // the first non header byte is at position 41
                    request.parseBody(packet);
                    if (c == 0) {
                        break;
                    }
                } else {
                    LOGGER.info("We received data from the wrong inverter... Expected susyd: {}, received: {}", sUSyID,
                            packet.getShort(15));
                }
            } else {
                LOGGER.error("Packet ID mismatch. Expected {}, received {}", ethernet.getPcktID(), rcvpcktID);
            }
        }
        return request.closeParse();
    }
}
