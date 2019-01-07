package eu.printingin3d.smalogger.api.inverter;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.eth.EthPacket;
import eu.printingin3d.smalogger.api.exception.InvalidPasswordException;
import eu.printingin3d.smalogger.api.requestvisitor.AbstractInverterRequest;
import eu.printingin3d.smalogger.api.smaconn.Ethernet;
import eu.printingin3d.smalogger.api.smaconn.SmaConnection;
import eu.printingin3d.smalogger.api.smaconn.UserGroup;

public class Inverter extends SmaConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(Inverter.class);
	
	private short SUSyID;
	private long Serial;
	
	/**
	 * Creates a new inverter.
	 * NOTE: This method is used by the SMALogger and should not be uses manually.
	 * Use {@link eu.printingin3d.smalogger.api.smajava.SmaLogger#createInverter(String)} to create an inverter instead.
	 * @param ip The ip address of the inverter.
	 * @param ethernet The Ethernet connection.
	 */
	public Inverter(String ip, Ethernet ethernet)
	{		
		//Each inverters has his own connection but uses the same ethernet socket.
		//Sma connection constructor
		super(ethernet, ip);
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
	        logoff();
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
			if (ethernet.pcktID == rcvpcktID)
			{
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
