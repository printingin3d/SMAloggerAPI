package eu.printingin3d.smalogger.api.inverter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.eth.EthPacket;
import eu.printingin3d.smalogger.api.exception.InvalidPasswordException;
import eu.printingin3d.smalogger.api.inverterdata.InverterData;
import eu.printingin3d.smalogger.api.inverterdata.InverterDataType;
import eu.printingin3d.smalogger.api.requestvisitor.AbstractInverterRequest;
import eu.printingin3d.smalogger.api.smaconn.Ethernet;
import eu.printingin3d.smalogger.api.smaconn.SmaConnection;
import eu.printingin3d.smalogger.api.smaconn.UserGroup;
import eu.printingin3d.smalogger.api.smajava.Misc;

public class Inverter extends SmaConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(Inverter.class);
	
	public InverterData Data;
	
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
		Data = new InverterData();
	}
	
	/**
	 * Returns the ip adress of this inverter.
	 * @return A string containing the IP adress of the inverter.
	 */
	public String getIP()
	{
		return super.ip;
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
		for(int ch = 0; ch < password.length(); ch++)
    	{
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
	private void logon(UserGroup userGroup, char[] password) throws IOException
	{
		//First initialize the connection.
		initConnection();
		ByteBuffer packet = getPacket();
    	EthPacket pckt2 = new EthPacket(packet);
    	Data.SUSyID = pckt2.getSource().getSUSyID();
    	Data.Serial = pckt2.getSource().getSerial();
		//Then login.
		smaLogin(userGroup, password);
		ByteBuffer packet2 = getPacket();
        EthPacket pckt = new EthPacket(packet2);
        if (ethernet.pcktID != ((pckt.getPacketID()) & 0x7FFF)) {   // InValid Packet ID
        	throw new IOException(String.format("Packet ID mismatch. Expected %d, received %d", ethernet.pcktID, ((pckt.getPacketID()) & 0x7FFF)));
        }

		if (pckt.getErrorCode() == 0x0100) {
	        smaLogoff();
	        throw new InvalidPasswordException("Logon failed. Check '"+userGroup+"' Password");
	    }
	}
	
	/**
	 * Sends a logoff request to the inverter to shut down the connection.
	 * Use this after your done getting data but before shutting down the main connection.
	 * @throws IOException 
	 */
	public void logoff() throws IOException	{
		smaLogoff();
	}
	
	/**
	 * Requests data from the inverter which gets stored in it's Data attribute.
	 * Uses Data.(Name of the value you requested) to get the actual value this method requested.
	 * @param invDataType The type of data you want to retrieve from the inverter.
	 * @return Returns 0 if everything went ok.
	 * @throws IOException 
	 */
	public void getInverterData(InverterDataType invDataType) throws IOException {
		LOGGER.info("getInverterData({})", invDataType);

	    int validPcktID = 0;
	    int value = 0;
	    
	    requestInverterData(invDataType);
	    
        do {
            ByteBuffer packet = getPacket();

            short rcvpcktID = (short) (packet.get(27) & 0x7FFF);
            if (ethernet.pcktID == rcvpcktID)
            {
            	//Check if we received the package from the right inverter, not sure if
            	//this works with multiple inverters.
            	//We do this by checking if the susyd and serial is equal to this inverter object's susyd and serial.
            	boolean rightOne = Data.SUSyID == packet.getShort(15) && Data.Serial == packet.getInt(17);

            	if (rightOne) {
                    validPcktID = 1;

                    for (int ix = 41; ix < packet.limit() - 4;)
                    {                   	
                    	int code = packet.getInt(ix);
                    	
                        //Check this if something doesn't work, int to enum conversion. Should be good now
                        LriDef lri = LriDef.intToEnum((code & 0x00FFFF00));

                        char dataType = (char) (code >> 24);
                        //Not sure if java uses same long date, well it doesn't
                        //Multiply by 1000 cause java uses milliseconds and the inverter uses seconds since epoch.
                        Date datetime = new Date(packet.getInt(ix + 4) * 1000l);                        
                        
                        if ((dataType != 0x10) && (dataType != 0x08))	//Not TEXT or STATUS, so it should be DWORD
                        {
                        	//All data that needs an int value
                            value = packet.getInt(ix + 8);
                            if ((value == Misc.NaN_S32) || (value == Misc.NaN_U32)) {
								value = 0;
							}
                        }                       
                        // fix: We can't rely on dataType because it can be both 0x00 or 0x40 for DWORDs
                        if ((lri == LriDef.MeteringDyWhOut) || (lri == LriDef.MeteringTotWhOut) || (lri == LriDef.MeteringTotFeedTms) || (lri == LriDef.MeteringTotOpTms))	//QWORD
                        {
                        	//All data that needs a long value
                        	long value64 = packet.getLong(ix + 8);
                            if ((value64 == Misc.NaN_S64) || (value64 == Misc.NaN_U64)) {
								value64 = 0;
							}                      
                            
                            Data.SetInverterData64(lri, value64, datetime);
                        }
                        else if(lri == LriDef.NameplateLocation)
                        {
                        	//INV_NAME
                        	int DEVICE_NAME_LENGTH = 33; //32 bytes + terminating zero
                            Data.SetInverterDataINVNAME(new String(Arrays.copyOfRange(packet.array(), ix+8, ix+8+DEVICE_NAME_LENGTH-1)).trim(), datetime);
                        }                       
                        else if(lri == LriDef.NameplatePkgRev)
                        {
                        	//INV_SWVER
                        	char Vtype = (char) packet.get(ix + 24);
                            String ReleaseType;
                            if (Vtype > 5) {
								ReleaseType = String.format("%c", Vtype);
							}
							else {
								ReleaseType = String.format("%c", "NEABRS".charAt(Vtype));//NOREV-EXPERIMENTAL-ALPHA-BETA-RELEASE-SPECIAL
							}
                            char Vbuild = (char) packet.get(ix + 25);
                            char Vminor = (char) packet.get(ix + 26);
                            char Vmajor = (char) packet.get(ix + 27);
                            //Vmajor and Vminor = 0x12 should be printed as '12' and not '18' (BCD)
                            String version = String.format("%c%c.%c%c.%02d.%s", '0'+(Vmajor >> 4), '0'+(Vmajor & 0x0F), '0'+(Vminor >> 4), '0'+(Vminor & 0x0F), (int)Vbuild, ReleaseType);  
                            Data.SetInverterDataSWVER(version, datetime);
                        }
                        else if(lri == LriDef.OperationHealth || lri == LriDef.OperationGriSwStt || lri == LriDef.NameplateMainModel || lri == LriDef.NameplateModel)
                        {
                        	//All cases which need the attribute value
                        	//INV_STATUS
                        	//INV_GRIDRELAY
                        	//INV_CLASS
                        	//INV_TYPE
                        	for (int idx = 8; idx < lri.getRecordSize(); idx += 4)
            		        {
            		            int attribute = packet.getInt(ix + idx) & 0x00FFFFFF;
            		            if (attribute == 0xFFFFFE) {
									break;	//End of attributes
								}
            		            char attValue = (char) packet.get(ix + idx + 3);
            		            if (attValue == 1) {
            		                Data.SetInverterDataAttribute(lri, attribute, datetime);
            		            }
            		        }
                        }
                        else if(lri == LriDef.DcMsWatt || lri == LriDef.DcMsAmp || lri == LriDef.DcMsVol)
                        {
                        	//All cases which need the cls var
                        	//SPOT_PDC1 / SPOT_PDC2
                        	//SPOT_UDC1 / SPOT_UDC2
                        	//SPOT_IDC1 / SPOT_IDC2
                        	long cls = code & 0xFF;
                        	Data.SetInverterDataCls(lri, value, cls, datetime);
                        }
                        else {	
                        	//All other cases go here
                        	if(lri != null) {
                            	Data.SetInverterData(lri, value, datetime);
                        	}
                        }
                        ix += lri==null ? 12 : lri.getRecordSize();
                    }
                }
            	else {
            		LOGGER.info("We received data from the wrong inverter... Expected susyd: {}, received: {}", Data.SUSyID, packet.getShort(15));
            	}
            }
            else {
            	LOGGER.error("Packet ID mismatch. Expected {}, received {}", ethernet.pcktID, rcvpcktID);
            }
        }
        while (validPcktID == 0);
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
				boolean rightOne = Data.SUSyID == packet.getShort(15) && Data.Serial == packet.getInt(17);
				
				if (rightOne) {
					packet.position(41);   // the first non header byte
					
                    for (int ix = 41; ix < packet.limit() - 4;) {
                    	packet.position(ix);
                    	int code = packet.getInt();
                    	
                        LriDef lri = LriDef.intToEnum((code & 0x00FFFF00));
                        
                        request.parseOneSegment(lri, packet);
                        
                        ix += lri==null ? 12 : lri.getRecordSize();
                    }
                    break;
				}
				else {
					LOGGER.info("We received data from the wrong inverter... Expected susyd: {}, received: {}", Data.SUSyID, packet.getShort(15));
				}
			}
			else {
				LOGGER.error("Packet ID mismatch. Expected {}, received {}", ethernet.pcktID, rcvpcktID);
			}
		}
		return request.closeParse();
	}
	
	/**
	 * Calculates the missing DC Spot Values
	 */
	public void calcMissingSpot()
	{
		if (Data.Pdc1 == 0) {
			Data.Pdc1 = (Data.Idc1 * Data.Udc1) / 100000;
		}
		if (Data.Pdc2 == 0) {
			Data.Pdc2 = (Data.Idc2 * Data.Udc2) / 100000;
		}
	}
	
	public void GetDayData()
	{
		
	}
	
	public void GetMonthData()
	{
		
	}
	
	public void GetEventData()
	{
		
	}
}
