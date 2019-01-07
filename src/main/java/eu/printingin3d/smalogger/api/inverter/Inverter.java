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
	
	private static final int COMMBUFSIZE = 1024;
	private final short anySUSyID = (short)0xFFFF;
	private final long anySerial = 0xFFFFFFFF;
	
	private short SUSyID;
	private long Serial;
	private final Ethernet ethernet;
	private final String ip;

	/**
	 * Creates a new inverter.
	 * NOTE: This method is used by the SMALogger and should not be used manually.
	 * Use {@link eu.printingin3d.smalogger.api.smajava.SmaLogger#createInverter(String)} to create an inverter instead.
	 * @param ip The ip address of the inverter.
	 * @param ethernet The Ethernet connection.
	 */
	public Inverter(String ip, Ethernet ethernet) {		
		//Each inverters has his own connection but uses the same ethernet socket.
		//Sma connection constructor
		this.ethernet = ethernet;
		this.ip = ip;
	}

	private void initConnection() throws IOException {
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
	
	private void smaLogin(UserGroup userGroup, char[] password) throws IOException {
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

	    //I believe the inverter times is using seconds instead of milliseconds.
        long now = System.currentTimeMillis() / 1000l;
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

        ethernet.send(ip);
	}
	
	@Override
	public void close() throws IOException {
		LOGGER.info("SMALogoff()");
        
        ethernet.writePacketHeader();
        ethernet.writePacket((char)0x08, (char)0xA0, (short)0x0300, anySUSyID, anySerial);
        ethernet.writeLong(0xFFFD010E);
        ethernet.writeLong(0xFFFFFFFF);
        ethernet.writePacketTrailer();
        ethernet.writePacketLength();

        ethernet.send(ip);
	}
	
	private void requestInverterData(IInverterCommand dataType) throws IOException {
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
	
	private ByteBuffer getPacket() throws IOException {
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
	                    // Dummy byte to align with BTH (7E)
	                    // We need last 6 bytes of ethPacketHeader too
	                    System.arraycopy(commBuf, EthPacketHeaderL1.getSize(), bb.array(), 1, bib - EthPacketHeaderL1.getSize());

	                    if (LOGGER.isTraceEnabled()) {
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
	
	/**
	 * Returns the ip adress of this inverter.
	 * @return A string containing the IP adress of the inverter.
	 */
	public String getIP() {
		return ip;
	}
	
	public short getSUSyID() {
		return SUSyID;
	}

	public long getSerial() {
		return Serial;
	}

	/**
	 * Sends a logon request to the inverter which creates a connection.
	 * Use this before getting data from the inverter but after the main connection was created.
	 * NOTE: Only able to log on as a user now!
	 * @param password The password used to login to the inverter.
	 * @return Returns 0 if everything went ok.
	 * @throws IOException 
	 */
	public void logon(String password) throws IOException
	{
		//The api needs the password in chararray form of a fixed size. 
		//We convert it here so the user doesn't have to hassle with it and can just input a string.
		char[] passArray = new char[13];
		for(int ch = 0; ch < password.length(); ch++) {
			passArray[ch] = password.charAt(ch);
    	}
		logon(UserGroup.USER, passArray);
	}
	
	/**
	 * Sends a logon request to the inverter which creates a connection.
	 * Use this before getting data from the inverter but after the main connection was created.
	 * @param userGroup The usergroup: (USER or INSTALLER)
	 * @param password The password used to login to the inverter.
	 * @return Returns 0 if everything went ok.
	 * @throws IOException 
	 */
	private void logon(UserGroup userGroup, char[] password) throws IOException {
		//First initialize the connection.
		initConnection();
		ByteBuffer packet = getPacket();
    	EthPacket pckt2 = new EthPacket(packet);
    	SUSyID = pckt2.getSource().getSUSyID();
    	Serial = pckt2.getSource().getSerial();
		//Then login.
		smaLogin(userGroup, password);
		ByteBuffer packet2 = getPacket();
        EthPacket pckt = new EthPacket(packet2);
        if (ethernet.pcktID != ((pckt.getPacketID()) & 0x7FFF)) {   // InValid Packet ID
        	throw new IOException(String.format("Packet ID mismatch. Expected %d, received %d", ethernet.pcktID, ((pckt.getPacketID()) & 0x7FFF)));
        }

		if (pckt.getErrorCode() == 0x0100) {
	        close();
	        throw new InvalidPasswordException("Logon failed. Check '"+userGroup+"' Password");
	    }
	}

	/**
	 * Requests data from the inverter which gets stored in it's Data attribute.
	 * Uses Data.(Name of the value you requested) to get the actual value this method requested.
	 * @param invDataType The type of data you want to retrieve from the inverter.
	 * @return Returns 0 if everything went ok.
	 * @throws IOException 
	 */
	public <T> T getInverterData(AbstractInverterRequest<T> request) throws IOException {
		LOGGER.info("getInverterData({})", request.getClass().getName());
		
		requestInverterData(request);
		
		while(true) {
			ByteBuffer packet = getPacket();
	        
			short rcvpcktID = (short) (packet.get(27) & 0x7FFF);
			if (ethernet.pcktID == rcvpcktID) {
				//Check if we received the package from the right inverter, not sure if
				//this works with multiple inverters.
				//We do this by checking if the susyd and serial is equal to this inverter object's susyd and serial.
				boolean rightOne = SUSyID == packet.getShort(15) && Serial == packet.getInt(17);
				
				if (rightOne) {
					packet.position(41);   // the first non header byte
					
                    for (int ix = 41; ix < packet.limit() - 4;) {
                    	packet.position(ix);
                    	int code = packet.getInt();
                    	int cls = code & 0xFF;
                    	
                        LriDef lri = LriDef.intToEnum((code & 0x00FFFF00));
                        
                        request.parseOneSegment(lri, cls, packet);
                        
                        ix += lri==null ? 12 : lri.getRecordSize();
                    }
                    break;
				}
				else {
					LOGGER.info("We received data from the wrong inverter... Expected susyd: {}, received: {}", SUSyID, packet.getShort(15));
				}
			}
			else {
				LOGGER.error("Packet ID mismatch. Expected {}, received {}", ethernet.pcktID, rcvpcktID);
			}
		}
		return request.closeParse();
	}
}
