package eu.printingin3d.smalogger.api.smaconn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.eth.EthPacketHeaderL1L2;
import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.smajava.Misc;

public class Ethernet implements AutoCloseable {
    public static final int ETH_L2SIGNATURE = 0x65601000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Ethernet.class);

    private final int maxpcktBufsize = 520;

    private final ByteBuffer buf;
    private final short appSUSyID;
    private final int appSerial;
    
    private final DatagramSocket sock;
    private final short port;

    private short pcktID = 1;
    
    /***
     * Connects the socket to the port
     * 
     * @param port The port to connect to.
     * @throws SocketException if the socket could not be opened, or the socket
     *                         could not bind to the specified local port or if
     *                         there is an error in the underlying protocol, such as
     *                         an UDP error.
     */
    public Ethernet(short port, short appSUSyID, int appSerial) throws SocketException {
        LOGGER.info("Initialising Socket...");
        this.port = port;
        this.appSUSyID = appSUSyID;
        this.appSerial = appSerial;

        sock = new DatagramSocket();

        buf = ByteBuffer.allocate(maxpcktBufsize);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // set up parameters for UDP
        sock.setBroadcast(true);
    }

    /**
     * Used to manually create an inverter with a given ip adres, this method gives
     * the inverter a socket connection used for communication.
     * 
     * @param ip The ip adress of the inverter.
     * @return An inverter with the ip adress and a socket connection.
     */
    public Inverter createInverter(String ip) {
        return new Inverter(ip, this);
    }

    public short getPcktID() {
        return pcktID;
    }

    /**
     * Disconnects and closes the socket connection.
     */
    @Override
    public void close() {
        sock.disconnect();
        sock.close();
    }

    /***
     * Reads incoming data from the socket
     * 
     * @param buf The buffer the hold the incoming data.
     * @return Number of bytes read.
     */
    public int read(byte[] buf) throws IOException {
        int bytesRead = 0;
        short timeout = 5; // 5 seconds

        while (true) {
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            sock.setSoTimeout(timeout * 1000);
            sock.receive(recv);
            bytesRead = recv.getLength();

            if (bytesRead > 0) {
                LOGGER.info("Received {} bytes from IP [{}]", bytesRead, recv.getAddress().getHostAddress());
                if (bytesRead == 600) {
                    LOGGER.info(" ==> packet ignored");
                }
            } else {
                LOGGER.warn("recvfrom() returned an error: {}", bytesRead);
            }

            if (bytesRead == 600) {
                timeout--; // decrease timeout if the packet received within the timeout is an energymeter
                           // packet
            } else {
                break;
            }
        }
        return bytesRead;
    }

    /***
     * Sends what's currently stored in the buffer.
     * 
     * @param toIP The ip addres to send it to.
     * @return The number of bytes sent.
     * @throws IOException
     */
    public int send(String toIP) throws IOException {
        if (LOGGER.isTraceEnabled()) {
            Misc.hexDump(buf.array(), buf.position(), 10);
        }

        DatagramPacket p = new DatagramPacket(buf.array(), buf.position(), new InetSocketAddress(toIP, port));
        int bytesSent = p.getLength();
        sock.send(p);
        LOGGER.info(bytesSent + " Bytes sent to IP [" + toIP + "]");
        return bytesSent;
    }

    /***
     * Clears the buffer and sets packetposition to 0.
     */
    public void clearBuffer() {
        buf.clear();
    }

    public void writePacketHeader() {
        buf.position(0);
        // Ignore control and destaddress
        writeInt(0x00414D53); // SMA\0
        writeInt(0xA0020400);
        writeInt(0x01000000);
        writeShort((short) 0);// Placeholder for packet length
    }

    public void writePacketTrailer() {
        writeInt(0);
    }

    public void writePacketLength() {
        short dataLength = (short) (buf.position() - EthPacketHeaderL1L2.getSize());
        buf.put(12, (byte) ((dataLength >> 8) & 0xFF));
        buf.put(13, (byte) (dataLength & 0xFF));
    }

    public void writeArray(char[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            writeByte((byte) bytes[i]);
        }
    }

    public void writePacket(char longwords, char ctrl, short ctrl2, short dstSUSyID, int dstSerial) {
        // Upping the packet id here so it doesn't have to be done manually.
        pcktID++;
        writeInt(ETH_L2SIGNATURE);

        writeByte((byte) longwords);
        writeByte((byte) ctrl);
        writeShort(dstSUSyID);
        writeInt(dstSerial);
        writeShort(ctrl2);
        writeShort(appSUSyID);
        writeInt(appSerial);
        writeShort(ctrl2);
        writeShort((short) 0);
        writeShort((short) 0);
        writeShort((short) (pcktID | 0x8000));
    }

    public void writeInt(int v) {
        buf.putInt(v);
    }

    public void writeShort(short v) {
        buf.putShort(v);
    }

    public void writeByte(byte v) {
        buf.put(v);
    }
}
