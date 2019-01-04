package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inverter.Inverter;
import inverterdata.InverterDataType;
import smajava.SmaLogger;
import smajava.misc;

/**
 * Example of how using this api for multiple inverters should work.
 * NOTE: this is untested by me as I only have access to one inverter.
 *
 */
public class MultiInverterTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiInverterTest.class);

	public static void main(String[] args) throws IOException {
		final String PASSWORD = "0000";	//Default password
		List<Inverter> inverters = new ArrayList<Inverter>();
		
		try (SmaLogger smaLogger = new SmaLogger()) {
			LOGGER.info("Initializing SMA Logger");
			smaLogger.initialize();
			LOGGER.info("SMA Logger succesfully initialized");
			
			//Network detection
			inverters = smaLogger.detectDevices();		
			
			if(!inverters.isEmpty()) {
				LOGGER.info("Found {} inverter(s)...", inverters.size());
				for(int i = 0; i < inverters.size(); i++)
				{
					LOGGER.info("\t{}  -  {}", i, inverters.get(i).getIP());
				}
			}
			else {
				LOGGER.info("No inverters detected...");
				System.exit(0);
			}
			
			LOGGER.info("logging on inverters...");
			
			for(Inverter inverter : inverters)
			{
				System.out.printf("Inverter %s logged on... ", inverter.getIP());
				inverter.logon(PASSWORD);
			}
			
			System.out.println("Getting some data...");
			
			for(Inverter inverter : inverters)
			{
				System.out.printf("#####\n"
						+ "\tInverter data for %s\n"
						+ "#####\n", inverter.getIP());
				inverter.GetInverterData(InverterDataType.SoftwareVersion);
			    
			    inverter.GetInverterData(InverterDataType.TypeLabel);
	            System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	            System.out.printf("Device Name:      %s\n", inverter.Data.DeviceName);
	            System.out.printf("Device Class:     %s\n", inverter.Data.DeviceClass);
	            System.out.printf("Device Type:      %s\n", inverter.Data.DeviceType);
	            System.out.printf("Software Version: %s\n", inverter.Data.SWVersion);
	            System.out.printf("Serial number:    %d\n", inverter.Data.Serial);
			    
			    inverter.GetInverterData(InverterDataType.EnergyProduction);
			    inverter.GetInverterData(InverterDataType.OperationTime);
	            System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	            System.out.println("Energy Production:");
	            System.out.printf("\tEToday: %.3fkWh\n", misc.tokWh(inverter.Data.EToday));
	            System.out.printf("\tETotal: %.3fkWh\n", misc.tokWh(inverter.Data.ETotal));
	            System.out.printf("\tOperation Time: %.2fh\n", misc.toHour(inverter.Data.OperationTime));
	            System.out.printf("\tFeed-In Time  : %.2fh\n", misc.toHour(inverter.Data.FeedInTime));
			}
			
			System.out.println("logging off inverters...");
	
			for(Inverter inverter : inverters)
			{
				inverter.Logoff();
			}
			
			System.out.println("Shutting down SMA Logger.");
		}
	}
}
