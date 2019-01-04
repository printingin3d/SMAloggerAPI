package eu.printingin3d.smalogger.api.smaconn;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.eth.EthPacketHeaderL1L2;
import eu.printingin3d.smalogger.api.smajava.misc;

public class SBFNet {
	private static final Logger LOGGER = LoggerFactory.getLogger(SBFNet.class);
	
	private final int maxpcktBufsize = 520;
	public final long ETH_L2SIGNATURE = 0x65601000;

	private int packetposition = 0;
	public byte[] pcktBuf = new byte[maxpcktBufsize];

	public short pcktID = 1;

	public short AppSUSyID;
	public long AppSerial;

	/***
	 * Clears the buffer and sets packetposition to 0.
	 */
	public void clearBuffer() {
		this.packetposition = 0;
		pcktBuf = new byte[maxpcktBufsize];
	}

	public void writePacketHeader() {
		packetposition = 0;
		// Ignore control and destaddress
		writeLong(0x00414D53); // SMA\0
		writeLong(0xA0020400);
		writeLong(0x01000000);
		writeByte((byte) 0);
		writeByte((byte) 0); // Placeholder for packet length
	}

	public void writePacketTrailer() {
		writeLong(0);
	}

	public void writePacketLength() {
		short dataLength = (short) (packetposition - EthPacketHeaderL1L2.getSize());
		pcktBuf[12] = (byte) ((dataLength >> 8) & 0xFF);
		pcktBuf[13] = (byte) (dataLength & 0xFF);
		// ethPacketHeaderL1L2 hdr = new ethPacketHeaderL1L2(pcktBuf);
		// hdr.setSize(packetposition);
		// short dataLength = (short)(packetposition - hdr.getSize());
		// hdr.pcktHdrL1.hiPacketLen = (byte)((dataLength >> 8) & 0xFF);
		// hdr.pcktHdrL1.loPacketLen = (byte)(dataLength & 0xFF);
	}

	public void writeArray(char bytes[]) {
		for (int i = 0; i < bytes.length; i++) {
			writeByte((byte) bytes[i]);
		}
	}

	public void writePacket(char longwords, char ctrl, short ctrl2, short dstSUSyID, long dstSerial) {
		// Upping the packet id here so it doesn't have to be done manually.
		pcktID++;
		writeLong(ETH_L2SIGNATURE);

		writeByte((byte) longwords);
		writeByte((byte) ctrl);
		writeShort(dstSUSyID);
		writeLong(dstSerial);
		writeShort(ctrl2);
		writeShort(AppSUSyID);
		writeLong(AppSerial);
		writeShort(ctrl2);
		writeShort((short) 0);
		writeShort((short) 0);
		writeShort((short) (pcktID | 0x8000));
	}

	public void writeLong(long v) {
		writeByte((byte) ((v >> 0) & 0xFF));
		writeByte((byte) ((v >> 8) & 0xFF));
		writeByte((byte) ((v >> 16) & 0xFF));
		writeByte((byte) ((v >> 24) & 0xFF));
	}

	public void writeShort(short v) {
		writeByte((byte) ((v >> 0) & 0xFF));
		writeByte((byte) ((v >> 8) & 0xFF));
	}

	public void writeByte(byte v) {
		pcktBuf[packetposition++] = v;
	}

	protected DatagramPacket createPacket(String toIP, short port) {
		if (LOGGER.isTraceEnabled()) {
			misc.HexDump(pcktBuf, packetposition, 10);
		}

		return new DatagramPacket(pcktBuf, packetposition, new InetSocketAddress(toIP, port));
	}
}
