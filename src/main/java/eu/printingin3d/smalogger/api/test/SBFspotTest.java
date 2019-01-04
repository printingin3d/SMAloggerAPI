package eu.printingin3d.smalogger.api.test;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.InvDeviceClass;
import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.inverterdata.InverterDataType;
import eu.printingin3d.smalogger.api.smajava.SmaLogger;
import eu.printingin3d.smalogger.api.smajava.TagDefs;
import eu.printingin3d.smalogger.api.smajava.misc;

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
			inverter.GetInverterData(InverterDataType.SoftwareVersion);
		    inverter.GetInverterData(InverterDataType.TypeLabel);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.printf("Device Name:      %s\n", inverter.Data.DeviceName);
	        System.out.printf("Device Class:     %s\n", inverter.Data.DeviceClass);
	        System.out.printf("Device Type:      %s\n", inverter.Data.DeviceType);
	        System.out.printf("Software Version: %s\n", inverter.Data.SWVersion);
	        System.out.printf("Serial number:    %d\n", inverter.Data.Serial);
		    inverter.GetInverterData(InverterDataType.BatteryChargeStatus);
		    if (inverter.Data.DevClass == InvDeviceClass.BatteryInverter) {
	            System.out.printf("SUSyID: %d - SN: %lu\n", inverter.Data.SUSyID, inverter.Data.Serial);
	            System.out.printf("Batt. Charging Status: %lu%%\n", inverter.Data.BatChaStt);
		    }
	
			inverter.GetInverterData(InverterDataType.BatteryInfo);
		    if (inverter.Data.DevClass == InvDeviceClass.BatteryInverter)
		    {
	            System.out.printf("SUSyID: %d - SN: %lu\n", inverter.Data.SUSyID, inverter.Data.Serial);
	            System.out.printf("Batt. Temperature: %3.1f%sC\n", (float)(inverter.Data.BatTmpVal / 10), misc.SYM_DEGREE); // degree symbol is different on windows/linux
	            System.out.printf("Batt. Voltage    : %3.2fV\n", misc.toVolt(inverter.Data.BatVol));
	            System.out.printf("Batt. Current    : %2.3fA\n", misc.toAmp(inverter.Data.BatAmp));
		    }
	
		    inverter.GetInverterData(InverterDataType.DeviceStatus);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
			System.out.printf("Device Status:      %s\n", TagDefs.GetInstance().getDesc(inverter.Data.DeviceStatus, "?"));
	
			inverter.GetInverterData(InverterDataType.InverterTemperature);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
			//System.out.printf("Device Temperature: %3.1f%sC\n", (float)(inverter.Data.Temperature / 100f), misc.SYM_DEGREE); // degree symbol is different on windows/linux
			System.out.printf("Device Temperature: %3.1f%sC\n", misc.toCelc(inverter.Data.Temperature), misc.SYM_DEGREE); // degree symbol is different on windows/linux
	
			if (inverter.Data.DevClass == InvDeviceClass.SolarInverter) {
				inverter.GetInverterData(InverterDataType.GridRelayStatus);
	            System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
				System.out.printf("GridRelay Status:      %s\n", TagDefs.GetInstance().getDesc(inverter.Data.GridRelayStatus, "?"));
		    }
	
		    inverter.GetInverterData(InverterDataType.MaxACPower);
	        //TODO: REVIEW THIS PART (getMaxACPower & getMaxACPower2 should be 1 function)
	        if (inverter.Data.Pmax1 == 0) {
	        	inverter.GetInverterData(InverterDataType.MaxACPower2);
	        }
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.printf("Pac max phase 1: %dW\n", inverter.Data.Pmax1);
	        System.out.printf("Pac max phase 2: %dW\n", inverter.Data.Pmax2);
	        System.out.printf("Pac max phase 3: %dW\n", inverter.Data.Pmax3);
	
		    inverter.GetInverterData(InverterDataType.EnergyProduction);
		    inverter.GetInverterData(InverterDataType.OperationTime);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.println("Energy Production:");
	        System.out.printf("\tEToday: %.3fkWh\n", misc.tokWh(inverter.Data.EToday));
	        System.out.printf("\tETotal: %.3fkWh\n", misc.tokWh(inverter.Data.ETotal));
	        System.out.printf("\tOperation Time: %.2fh\n", misc.toHour(inverter.Data.OperationTime));
	        System.out.printf("\tFeed-In Time  : %.2fh\n", misc.toHour(inverter.Data.FeedInTime));
	
		    inverter.GetInverterData(InverterDataType.SpotDCPower);
		    inverter.GetInverterData(InverterDataType.SpotDCVoltage);
	
		    //Calculate missing DC Spot Values
		    inverter.CalcMissingSpot();
	
			inverter.Data.calPdcTot = inverter.Data.Pdc1 + inverter.Data.Pdc2;
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.println("DC Spot Data:");
	        System.out.printf("\tString 1 Pdc: %7.3fkW - Udc: %6.2fV - Idc: %6.3fA\n", misc.tokW(inverter.Data.Pdc1), misc.toVolt(inverter.Data.Udc1), misc.toAmp(inverter.Data.Idc1));
	        System.out.printf("\tString 2 Pdc: %7.3fkW - Udc: %6.2fV - Idc: %6.3fA\n", misc.tokW(inverter.Data.Pdc2), misc.toVolt(inverter.Data.Udc2), misc.toAmp(inverter.Data.Idc2));
	
		    inverter.GetInverterData(InverterDataType.SpotACPower);
		    inverter.GetInverterData(InverterDataType.SpotACVoltage);
		    inverter.GetInverterData(InverterDataType.SpotACTotalPower);
	
		    //Calculate missing AC Spot Values
		    inverter.CalcMissingSpot();
	
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.println("AC Spot Data:");
	        System.out.printf("\tPhase 1 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n", misc.tokW(inverter.Data.Pac1), misc.toVolt(inverter.Data.Uac1), misc.toAmp(inverter.Data.Iac1));
	        System.out.printf("\tPhase 2 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n", misc.tokW(inverter.Data.Pac2), misc.toVolt(inverter.Data.Uac2), misc.toAmp(inverter.Data.Iac2));
	        System.out.printf("\tPhase 3 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n", misc.tokW(inverter.Data.Pac3), misc.toVolt(inverter.Data.Uac3), misc.toAmp(inverter.Data.Iac3));
	        System.out.printf("\tTotal Pac   : %7.3fkW\n", misc.tokW(inverter.Data.TotalPac));
		    
		    inverter.GetInverterData(InverterDataType.SpotGridFrequency);
	        System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
	        System.out.printf("Grid Freq. : %.2fHz\n", misc.toHz(inverter.Data.GridFreq));
	
		    if (inverter.Data.DevClass == InvDeviceClass.SolarInverter)
			{
				System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
				if (inverter.Data.InverterDatetime > 0) {
					System.out.printf("Current Inverter Time: %s\n", misc.printDate(inverter.Data.InverterDatetime));
				}
	
				if (inverter.Data.WakeupTime > 0) {
					System.out.printf("Inverter Wake-Up Time: %s\n", misc.printDate(inverter.Data.WakeupTime));
				}
	
				if (inverter.Data.SleepTime > 0) {
					System.out.printf("Inverter Sleep Time  : %s\n", misc.printDate(inverter.Data.SleepTime));
				}
			}
			
			System.out.println("logging off inverter...");
	
			inverter.Logoff();
			
			System.out.println("Shutting down SMA Logger.");
		}
	}
}
