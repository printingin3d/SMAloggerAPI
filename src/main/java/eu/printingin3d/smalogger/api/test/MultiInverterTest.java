package eu.printingin3d.smalogger.api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.requestvisitor.EnergyProductionRequest;
import eu.printingin3d.smalogger.api.requestvisitor.OperationTimeRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SoftwareVersionRequest;
import eu.printingin3d.smalogger.api.requestvisitor.TypeLabelRequest;
import eu.printingin3d.smalogger.api.response.EnergyProductionResponse;
import eu.printingin3d.smalogger.api.response.OperationTimeResponse;
import eu.printingin3d.smalogger.api.response.TypeLabelResponse;
import eu.printingin3d.smalogger.api.smajava.Misc;
import eu.printingin3d.smalogger.api.smajava.SmaLogger;

/**
 * Example of how using this api for multiple inverters should work.
 * NOTE: this is untested by me as I only have access to one inverter.
 *
 */
public class MultiInverterTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiInverterTest.class);

	public static void main(String[] args) throws IOException {
		final String PASSWORD = "0000";	//Default password
		List<Inverter> inverters = new ArrayList<>();
		
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
			    
				TypeLabelResponse labels = inverter.getInverterData(new TypeLabelRequest());
	            System.out.printf("SUSyID: %d - SN: %d\n", inverter.getSUSyID(), inverter.getSerial());
		        System.out.printf("Device Name:      %s\n", labels.getDeviceName());
		        System.out.printf("Device Class:     %s\n", labels.getDeviceClass());
		        System.out.printf("Device Type:      %s\n", labels.getDeviceType());
	            System.out.printf("Software Version: %s\n", inverter.getInverterData(new SoftwareVersionRequest()));
	            System.out.printf("Serial number:    %d\n", inverter.getSerial());
			    
		        EnergyProductionResponse energyProduction = inverter.getInverterData(new EnergyProductionRequest());
		        OperationTimeResponse operationTime = inverter.getInverterData(new OperationTimeRequest());
	            System.out.println("Energy Production:");
		        System.out.printf("\tEToday: %.3fkWh\n", Misc.tokWh(energyProduction.getTodayEnergy()));
		        System.out.printf("\tETotal: %.3fkWh\n", Misc.tokWh(energyProduction.getTotalEnergy()));
		        System.out.printf("\tOperation Time: %.2fh\n", Misc.toHour(operationTime.getOperationTime()));
		        System.out.printf("\tFeed-In Time  : %.2fh\n", Misc.toHour(operationTime.getFeedInTime()));
			}
			
			System.out.println("logging off inverters...");
	
			for(Inverter inverter : inverters)
			{
				inverter.logoff();
			}
			
			System.out.println("Shutting down SMA Logger.");
		}
	}
}
