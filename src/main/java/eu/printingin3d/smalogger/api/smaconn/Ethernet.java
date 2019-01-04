package eu.printingin3d.smalogger.api.smaconn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.Inverter;

public class Ethernet extends SBFNet implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Ethernet.class);
	
	private DatagramSocket sock;
	private short port;
	
	/***
	 * Connects the socket to the port
	 * @param port The port to connect to.
	 * @return 0 if success, -1 if connection failed.
	 */
	public void connect(short port) throws SocketException {
		LOGGER.info("Initialising Socket...");
		sock = new DatagramSocket();
		
	    // set up parameters for UDP
		this.port = port;
		sock.setBroadcast(true);
	}
	
	/**
	 * Used to manually create an inverter with a given ip adres, this method
	 * gives the inverter a socket connection used for communication.
	 * @param ip The ip adress of the inverter.
	 * @return An inverter with the ip adress and a socket connection.
	 */
	public Inverter createInverter(String ip) {
		return new Inverter(ip, this);
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
	 * @param buf The buffer the hold the incoming data.
	 * @return Number of bytes read.
	 */
	public int read(byte[] buf) throws IOException {
	    int bytes_read = 0;
	    short timeout = 5; //5 seconds
	    
	    while (true) {
	    	DatagramPacket recv = new DatagramPacket(buf, buf.length);
			sock.setSoTimeout(timeout * 1000);
			sock.receive(recv);
			bytes_read = recv.getLength();
			
			if ( bytes_read > 0)
			{
				LOGGER.info("Received {} bytes from IP [{}]", bytes_read, recv.getAddress().getHostAddress());
		   		if (bytes_read == 600) {
		   			LOGGER.info(" ==> packet ignored");
				}
			} else {
				LOGGER.warn("recvfrom() returned an error: {}", bytes_read);
			}

			if (bytes_read == 600) {
				timeout--;	// decrease timeout if the packet received within the timeout is an energymeter packet
			} else {
				break;
			}
	    }
	    return bytes_read;
	}
	
	/***
	 * Sends what's currently stored in the buffer.
	 * @param toIP The ip addres to send it to.
	 * @return The number of bytes sent.
	 * @throws IOException 
	 */
	public int send(String toIP) throws IOException {
		DatagramPacket p = createPacket(toIP, port);
		int bytes_sent = p.getLength();
		sock.send(p);
		LOGGER.info(bytes_sent + " Bytes sent to IP [" + toIP + "]");
	    return bytes_sent;
	}
}
