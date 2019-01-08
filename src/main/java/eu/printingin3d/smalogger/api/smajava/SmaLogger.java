package eu.printingin3d.smalogger.api.smajava;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.smaconn.Ethernet;

public class SmaLogger implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmaLogger.class);
    private static final String IP_BROADCAST = "239.12.255.254";
    
    private Ethernet ethernet;

    /**
     * Creates a new instance of the SMALogger and it's ethernet connection.
     * 
     * @throws IOException
     */
    public SmaLogger(short port, short appSUSyID, int appSerial) throws IOException {
        // Lets just use the english taglist for now.
        TagDefs.getInstance().readall("en-US");

        ethernet = new Ethernet(port, appSUSyID, appSerial);
    }

    /**
     * Creates a new instance of the SMALogger and it's ethernet connection with the
     * default port: 9522, appSUSyID and appSerial
     * 
     * @throws IOException
     */
    public SmaLogger() throws IOException {
        this((short) 9522, (short) 0x1234, 0x12345678);
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
     * 
     * @return A list of found inverters. If no inverters are found, the list is
     *         empty.
     * @throws IOException
     */
    public List<Inverter> detectDevices() throws IOException {
        List<Inverter> inverters = new ArrayList<>();
        boolean foundOne = false;

        // Start with UDP broadcast to check for SMA devices on the LAN
        sendBroadcastMessage();

        // SMA inverter announces it�s presence in response to the discovery request
        // packet
        int bytesRead = 1;
        byte[] buf = new byte[1024];

        // Untested, the idea is to keep listening if there are multiple inverters.
        try {
            while (bytesRead > 0) {
                // if bytesRead < 0, a timeout has occurred
                // if bytesRead == 0, no data was received
                bytesRead = ethernet.read(buf);

                // Only do this if we actually got some data
                if (bytesRead > 0) {
                    // Retrieve the ip adress from the received package.
                    String ip = String.format("%d.%d.%d.%d", (buf[38] & 0xFF), (buf[39] & 0xFF),
                            (buf[40] & 0xFF), (buf[41] & 0xFF));

                    LOGGER.info("Inverter IP address: {} found via broadcastidentification", ip);

                    // Check if the inverter isn't already in the list.
                    if (!containsInverter(inverters, ip)) {
                        // Create a new inverter with the ip and add it to the result list.
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
            Misc.hexDump(buf, bytesRead, 10);
        }

        return inverters;
    }

    private void sendBroadcastMessage() throws IOException {
        // Clear the buffer and set packet position to 0.
        ethernet.clearBuffer();

        ethernet.writeInt(0x00414D53); // Start of SMA header
        ethernet.writeInt(0xA0020400); // Unknown
        ethernet.writeInt(0xFFFFFFFF); // Unknown
        ethernet.writeInt(0x20000000); // Unknown
        ethernet.writeInt(0x00000000); // Unknown

        ethernet.send(IP_BROADCAST);
    }

    private static boolean containsInverter(List<Inverter> inverters, String ip) {
        for (Inverter inv : inverters) {
            if (inv.getIP().equals(ip)) {
                return true;
            }
        }
        return false;
    }
}
