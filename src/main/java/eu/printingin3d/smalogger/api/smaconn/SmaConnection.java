package eu.printingin3d.smalogger.api.smaconn;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.eth.EthPacketHeaderL1;
import eu.printingin3d.smalogger.api.eth.EthPacketHeaderL1L2;
import eu.printingin3d.smalogger.api.exception.NoDataReceivedException;
import eu.printingin3d.smalogger.api.inverterdata.IInverterCommand;
import eu.printingin3d.smalogger.api.smajava.Misc;

public class SmaConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmaConnection.class);

	public class E_SBFSPOT
	{
		public final static int E_OK			= 0;
		public final static int E_NODATA		= -1;	// Bluetooth buffer empty
		public final static int E_BADARG		= -2;	// Unknown command line argument
		public final static int E_CHKSUM		= -3;	// Invalid Checksum
		public final static int E_BUFOVRFLW		= -4;	// Buffer overflow
		public final static int E_ARCHNODATA	= -5;	// No archived data found for given timespan
		public final static int E_INIT			= -6;	// Unable to initialize
		public final static int E_INVPASSW		= -7;	// Invalid password
		public final static int E_RETRY			= -8;	// Retry the last action
		public final static int E_EOF			= -9;	// End of data
	}
	
	private final short anySUSyID = (short)0xFFFF;
	private final long anySerial = 0xFFFFFFFF;
	private static final int COMMBUFSIZE = 1024;
	
	protected Ethernet ethernet;
	
	protected String ip;
	
	public SmaConnection(Ethernet eth, String inverterIP)
	{
		this.ethernet = eth;
		this.ip = inverterIP;
	}
	
	protected void initConnection() throws IOException
	{
		ethernet.writePacketHeader();
	    ethernet.writePacket((char)0x09, (char)0xA0, (short)0, anySUSyID, anySerial);
	    ethernet.writeLong(0x00000200);
	    ethernet.writeLong(0);
	    ethernet.writeLong(0);
	    ethernet.writeLong(0);
	    ethernet.writePacketLength();
        
	    //Send packet to first inverter
	    ethernet.send(ip);
	}
	
	protected void smaLogin(UserGroup userGroup, char[] password) throws IOException
	{
		final int MAX_PWLENGTH = 12;
	    char pw[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	    LOGGER.info("smaLogin()");

	    char encChar = userGroup.getEncChar();
	    //Encode password
	    int idx;
	    for (idx = 0; (password[idx] != 0) && (idx <= pw.length); idx++)
	    {
	        pw[idx] = (char) (password[idx] + encChar);
	    }
	    for (; idx < MAX_PWLENGTH; idx++) {
			pw[idx] = encChar;
		}

	    long now;

	    //I believe the inverter times is using seconds instead of milliseconds.
        now = System.currentTimeMillis() / 1000l;
        ethernet.writePacketHeader();
        ethernet.writePacket((char)0x0E, (char)0xA0, (short)0x0100, anySUSyID, anySerial);
        ethernet.writeLong(0xFFFD040C);
        ethernet.writeLong(userGroup.getValue());
        ethernet.writeLong(0x00000384);
        ethernet.writeLong(now);
        ethernet.writeLong(0);
        ethernet.writeArray(pw);
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();

        //TODO: make this work for multiple inverters, kinda did this with the whole api thing.
        ethernet.send(ip);
	}
	
	protected void smaLogoff() throws IOException
	{
		LOGGER.info("SMALogoff()");
        
        ethernet.writePacketHeader();
        ethernet.writePacket((char)0x08, (char)0xA0, (short)0x0300, anySUSyID, anySerial);
        ethernet.writeLong(0xFFFD010E);
        ethernet.writeLong(0xFFFFFFFF);
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();

        //TODO: make this work for multiple inverters
        ethernet.send(ip);
	}
	
	protected void requestInverterData(IInverterCommand dataType) throws IOException
	{
		ethernet.writePacketHeader();
        ethernet.writePacket((char)0x09, (char)0xA0, (short)0, anySUSyID, anySerial);
        //Get the command values stored in the enum.
        ethernet.writeLong(dataType.getCommand());
        ethernet.writeLong(dataType.getFirst());
        ethernet.writeLong(dataType.getLast());
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();
        
        ethernet.send(ip);
	}
	
	protected ByteBuffer getPacket() throws IOException {
		byte[] commBuf = new byte[COMMBUFSIZE];
		LOGGER.info("ethGetPacket()");
	    
	    while (true) {
	    	int bib = ethernet.read(commBuf);

	    	if (bib <= 0) {
	    		throw new NoDataReceivedException("No data!");
	        }
	        else {
	        	EthPacketHeaderL1L2 pkHdr = new EthPacketHeaderL1L2(commBuf);
	        	int pkLen = ((pkHdr.getPcktHdrL1().getHiPacketLen() << 8) + pkHdr.getPcktHdrL1().getLoPacketLen()) & 0xff;	//0xff to convert it to unsigned?

	            //More data after header?
	            if (pkLen > 0) {
	            	if (LOGGER.isTraceEnabled()) {
						Misc.HexDump(commBuf, bib, 10);
					}

	                if (pkHdr.getPcktHdrL2().getMagicNumber() == ethernet.ETH_L2SIGNATURE) {
	                	ByteBuffer bb = ByteBuffer.allocate(bib - EthPacketHeaderL1.getSize()+1);
	                	bb.order(ByteOrder.LITTLE_ENDIAN);
	                	// Copy CommBuf to packetbuffer
	                    // Dummy byte to align with BTH (7E)
	                	//result.pcktBuf[0]= 0;
	                    // We need last 6 bytes of ethPacketHeader too
	                    System.arraycopy(commBuf, EthPacketHeaderL1.getSize(), bb.array(), 1, bib - EthPacketHeaderL1.getSize());
	                    
	                    // Point packetposition at last byte in our buffer
						// This is different from BTH
//	                    result.packetposition = bib - EthPacketHeaderL1.getSize();

	                    if (LOGGER.isTraceEnabled())
	                    {
	                        LOGGER.debug("<<<====== Content of pcktBuf =======>>>");
	                        Misc.HexDump(bb.array(), bb.limit(), 10);
	                        LOGGER.debug("<<<=================================>>>");
	                    }
	                    return bb;
	                }
	                else {
	                	LOGGER.info("L2 header not found.\n");
	                }
	            } else {
		    		throw new NoDataReceivedException("No data!");
				}
	    	}
		}
	}
}
