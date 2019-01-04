package smajava;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inverter.Inverter;
import smaconn.Ethernet;

public class SmaLogger implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmaLogger.class);
	public static final String VERSION = "0.2 Remaster";
	
	private final String IP_Broadcast = "239.12.255.254";
	private Ethernet ethernet;
	
	/**
	 * Creates a new instance of the SMALogger and it's ethernet connection.
	 */
	public SmaLogger() {
		ethernet = new Ethernet();
	}
	
	/**
	 * Initializes the SMALogger, also intializes and creates the ethernet
	 * connection.
	 * @return Returns 0 if everything went right.
	 * @throws IOException 
	 */
	public void initialize() throws IOException {
	    //Lets just use the english taglist for now.
	    TagDefs.GetInstance().readall("en-US");
	    
		//So the port was hardcoded in the config so why not hardcode it here, for now...
		ethernet.connect((short)9522);
	}
	
	/**
	 * Shuts down the SMALogger and closes it's ethernet connection.
	 */
	@Override
	public void close() {
		ethernet.close();
	}
	
	public Inverter createInverter(String ip) {
		return ethernet.createInverter(ip);
	}
	
	/**
	 * Sends a broadcast message over the network the detect inverters.
	 * @return A list of found inverters. If no inverters are found, the list is empty.
	 * @throws IOException 
	 */
	public List<Inverter> detectDevices() throws IOException {
		List<Inverter> inverters = new ArrayList<Inverter>();
		boolean foundOne = false;
		
		// Start with UDP broadcast to check for SMA devices on the LAN
		sendBroadcastMessage();
		
		//SMA inverter announces it�s presence in response to the discovery request packet
    	int bytesRead = 1;
    	byte[] CommBuf = new byte[1024];
    	
    	//Untested, the idea is to keep listening if there are multiple inverters.
    	try {
	    	while(bytesRead > 0) {
	    		// if bytesRead < 0, a timeout has occurred
	    		// if bytesRead == 0, no data was received
				bytesRead = ethernet.read(CommBuf);
	    		
	    		//Only do this if we actually got some data
	    		if(bytesRead > 0)
	    		{
		    		//Retrieve the ip adress from the received package.
		    		String ip = String.format("%d.%d.%d.%d", (CommBuf[38] & 0xFF), (CommBuf[39] & 0xFF), (CommBuf[40] & 0xFF), (CommBuf[41] & 0xFF));
		    		
		    		LOGGER.info("Inverter IP address: {} found via broadcastidentification", ip);
		
		    		//Check if the inverter isn't already in the list.
		    		if(!ContainsInverter(inverters, ip))
		    		{
		    			//Create a new inverter with the ip and add it to the result list.
		    			inverters.add(createInverter(ip));
		    		}	
		    		foundOne = true;
	    		}
	    	}
    	} catch (SocketTimeoutException e) {
    		// no more inverter to be found
    	}
		
		if (!foundOne) {
			LOGGER.error("ERROR: No inverter responded to identification broadcast.");
			LOGGER.error("Try to set IP_Address in SBFspot.cfg!");
			return inverters;
		}

    	if (LOGGER.isDebugEnabled()) {
			misc.HexDump(CommBuf, bytesRead, 10);
		}
    	
    	return inverters;
	}
	
	private void sendBroadcastMessage() throws IOException
	{
		//Clear the buffer and set packet position to 0.
		ethernet.clearBuffer();
		
    	ethernet.writeLong(0x00414D53);  //Start of SMA header
    	ethernet.writeLong(0xA0020400);  //Unknown
    	ethernet.writeLong(0xFFFFFFFF);  //Unknown
    	ethernet.writeLong(0x20000000);  //Unknown
    	ethernet.writeLong(0x00000000);  //Unknown

    	ethernet.send(IP_Broadcast);
	}
	
	private boolean ContainsInverter(List<Inverter> inverters, String ip) {
		for (Inverter inv : inverters) {
			if (inv.getIP().equals(ip)) {
				return true;
			}
		}
		return false;
	}
}
