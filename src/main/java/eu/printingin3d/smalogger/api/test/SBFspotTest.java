package eu.printingin3d.smalogger.api.test;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.InvDeviceClass;
import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.inverterdata.InverterDataType;
import eu.printingin3d.smalogger.api.smajava.Misc;
import eu.printingin3d.smalogger.api.smajava.SmaLogger;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

/**
 * This example basically does the exact same as the original SBFspot program minus the logging stuff
 * Mainly used to test all the functionality of the API.
 *
 */
public class SBFspotTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SBFspotTest.class);

	public static void main(String[] args) throws IOException 
	{
		final String PASSWORD = "0000";	//Default password
		try (SmaLogger smaLogger = new SmaLogger()) {
			Inverter inverter;
			
			LOGGER.info("Initializing SMA Logger");
			smaLogger.initialize();
			LOGGER.info("SMA Logger succesfully initialized");
			
			//Manual creation
			inverter = smaLogger.createInverter("192.168.0.12");
	
			LOGGER.info("Inverter {} logging on... ", inverter.getIP());
			inverter.logon(PASSWORD);
			LOGGER.info("Inverter {} logged on. ", inverter.getIP());
			
			System.out.println("Getting some data...");
			inverter.getInverterData(InverterDataType.SoftwareVersion);
		    inverter.getInverterData(InverterDataType.TypeLabel);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.printf("Device Name:      %s\n", inverter.Data.DeviceName);
	        System.out.printf("Device Class:     %s\n", inverter.Data.DeviceClass);
	        System.out.printf("Device Type:      %s\n", inverter.Data.DeviceType);
	        System.out.printf("Software Version: %s\n", inverter.Data.SWVersion);
	        System.out.printf("Serial number:    %d\n", inverter.Data.Serial);
		    inverter.getInverterData(InverterDataType.BatteryChargeStatus);
		    if (inverter.Data.DevClass == InvDeviceClass.BatteryInverter) {
	            System.out.printf("SUSyID: %d - SN: %lu\n", inverter.Data.SUSyID, inverter.Data.Serial);
	            System.out.printf("Batt. Charging Status: %lu%%\n", inverter.Data.BatChaStt);
		    }
	
			inverter.getInverterData(InverterDataType.BatteryInfo);
		    if (inverter.Data.DevClass == InvDeviceClass.BatteryInverter)
		    {
	            System.out.printf("SUSyID: %d - SN: %lu\n", inverter.Data.SUSyID, inverter.Data.Serial);
	            System.out.printf("Batt. Temperature: %3.1f%sC\n", (float)(inverter.Data.BatTmpVal / 10), Misc.SYM_DEGREE); // degree symbol is different on windows/linux
	            System.out.printf("Batt. Voltage    : %3.2fV\n", Misc.toVolt(inverter.Data.BatVol));
	            System.out.printf("Batt. Current    : %2.3fA\n", Misc.toAmp(inverter.Data.BatAmp));
		    }
	
		    inverter.getInverterData(InverterDataType.DeviceStatus);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
			System.out.printf("Device Status:      %s\n", TagDefs.GetInstance().getDesc(inverter.Data.DeviceStatus, "?"));
	
			inverter.getInverterData(InverterDataType.InverterTemperature);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
			//System.out.printf("Device Temperature: %3.1f%sC\n", (float)(inverter.Data.Temperature / 100f), misc.SYM_DEGREE); // degree symbol is different on windows/linux
			System.out.printf("Device Temperature: %3.1f%sC\n", Misc.toCelc(inverter.Data.Temperature), Misc.SYM_DEGREE); // degree symbol is different on windows/linux
	
			if (inverter.Data.DevClass == InvDeviceClass.SolarInverter) {
				inverter.getInverterData(InverterDataType.GridRelayStatus);
	            System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
				System.out.printf("GridRelay Status:      %s\n", TagDefs.GetInstance().getDesc(inverter.Data.GridRelayStatus, "?"));
		    }
	
		    inverter.getInverterData(InverterDataType.MaxACPower);
	        //TODO: REVIEW THIS PART (getMaxACPower & getMaxACPower2 should be 1 function)
	        if (inverter.Data.Pmax1 == 0) {
	        	inverter.getInverterData(InverterDataType.MaxACPower2);
	        }
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.printf("Pac max phase 1: %dW\n", inverter.Data.Pmax1);
	        System.out.printf("Pac max phase 2: %dW\n", inverter.Data.Pmax2);
	        System.out.printf("Pac max phase 3: %dW\n", inverter.Data.Pmax3);
	
		    inverter.getInverterData(InverterDataType.EnergyProduction);
		    inverter.getInverterData(InverterDataType.OperationTime);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.println("Energy Production:");
	        System.out.printf("\tEToday: %.3fkWh\n", Misc.tokWh(inverter.Data.EToday));
	        System.out.printf("\tETotal: %.3fkWh\n", Misc.tokWh(inverter.Data.ETotal));
	        System.out.printf("\tOperation Time: %.2fh\n", Misc.toHour(inverter.Data.OperationTime));
	        System.out.printf("\tFeed-In Time  : %.2fh\n", Misc.toHour(inverter.Data.FeedInTime));
	
		    inverter.getInverterData(InverterDataType.SpotDCPower);
		    inverter.getInverterData(InverterDataType.SpotDCVoltage);
	
		    //Calculate missing DC Spot Values
		    inverter.calcMissingSpot();
	
			inverter.Data.calPdcTot = inverter.Data.Pdc1 + inverter.Data.Pdc2;
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.println("DC Spot Data:");
	        System.out.printf("\tString 1 Pdc: %7.3fkW - Udc: %6.2fV - Idc: %6.3fA\n", Misc.tokW(inverter.Data.Pdc1), Misc.toVolt(inverter.Data.Udc1), Misc.toAmp(inverter.Data.Idc1));
	        System.out.printf("\tString 2 Pdc: %7.3fkW - Udc: %6.2fV - Idc: %6.3fA\n", Misc.tokW(inverter.Data.Pdc2), Misc.toVolt(inverter.Data.Udc2), Misc.toAmp(inverter.Data.Idc2));
	
		    inverter.getInverterData(InverterDataType.SpotACPower);
		    inverter.getInverterData(InverterDataType.SpotACVoltage);
		    inverter.getInverterData(InverterDataType.SpotACTotalPower);
	
		    //Calculate missing AC Spot Values
		    inverter.calcMissingSpot();
	
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.println("AC Spot Data:");
	        System.out.printf("\tPhase 1 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n", Misc.tokW(inverter.Data.Pac1), Misc.toVolt(inverter.Data.Uac1), Misc.toAmp(inverter.Data.Iac1));
	        System.out.printf("\tPhase 2 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n", Misc.tokW(inverter.Data.Pac2), Misc.toVolt(inverter.Data.Uac2), Misc.toAmp(inverter.Data.Iac2));
	        System.out.printf("\tPhase 3 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n", Misc.tokW(inverter.Data.Pac3), Misc.toVolt(inverter.Data.Uac3), Misc.toAmp(inverter.Data.Iac3));
	        System.out.printf("\tTotal Pac   : %7.3fkW\n", Misc.tokW(inverter.Data.TotalPac));
		    
		    inverter.getInverterData(InverterDataType.SpotGridFrequency);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.printf("Grid Freq. : %.2fHz\n", Misc.toHz(inverter.Data.GridFreq));
	
		    if (inverter.Data.DevClass == InvDeviceClass.SolarInverter)
			{
				System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
				if (inverter.Data.InverterDatetime > 0) {
					System.out.printf("Current Inverter Time: %s\n", Misc.printDate(inverter.Data.InverterDatetime));
				}
	
				if (inverter.Data.WakeupTime > 0) {
					System.out.printf("Inverter Wake-Up Time: %s\n", Misc.printDate(inverter.Data.WakeupTime));
				}
	
				if (inverter.Data.SleepTime > 0) {
					System.out.printf("Inverter Sleep Time  : %s\n", Misc.printDate(inverter.Data.SleepTime));
				}
			}
			
			System.out.println("logging off inverter...");
	
			inverter.logoff();
			
			System.out.println("Shutting down SMA Logger.");
		}
	}
}
