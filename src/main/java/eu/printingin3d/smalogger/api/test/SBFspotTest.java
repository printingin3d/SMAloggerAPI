package eu.printingin3d.smalogger.api.test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.InvDeviceClass;
import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.requestvisitor.BatteryChargeStatusRequest;
import eu.printingin3d.smalogger.api.requestvisitor.BatteryInfoRequest;
import eu.printingin3d.smalogger.api.requestvisitor.DayDataRequest;
import eu.printingin3d.smalogger.api.requestvisitor.DeviceStatusRequest;
import eu.printingin3d.smalogger.api.requestvisitor.EnergyProductionRequest;
import eu.printingin3d.smalogger.api.requestvisitor.GridRelayStatusRequest;
import eu.printingin3d.smalogger.api.requestvisitor.InverterTemperatureRequest;
import eu.printingin3d.smalogger.api.requestvisitor.MaxACPower2Request;
import eu.printingin3d.smalogger.api.requestvisitor.MaxACPowerRequest;
import eu.printingin3d.smalogger.api.requestvisitor.OperationTimeRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SoftwareVersionRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotAcPowerRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotAcTotalPowerRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotAcVoltageRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotDCVoltageRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotDcPowerRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotGridFrequencyRequest;
import eu.printingin3d.smalogger.api.requestvisitor.TypeLabelRequest;
import eu.printingin3d.smalogger.api.response.ACVoltageAmpereResponse;
import eu.printingin3d.smalogger.api.response.BatteryInfoResponse;
import eu.printingin3d.smalogger.api.response.EnergyProductionResponse;
import eu.printingin3d.smalogger.api.response.OperationTimeResponse;
import eu.printingin3d.smalogger.api.response.SpotDCVoltageResponse;
import eu.printingin3d.smalogger.api.response.SpotDcPowerResponse;
import eu.printingin3d.smalogger.api.response.ThreePhaseResponse;
import eu.printingin3d.smalogger.api.response.TypeLabelResponse;
import eu.printingin3d.smalogger.api.smajava.Misc;
import eu.printingin3d.smalogger.api.smajava.SmaLogger;

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
			LOGGER.info("Initializing SMA Logger");
			smaLogger.initialize();
			LOGGER.info("SMA Logger succesfully initialized");
			
			//Manual creation
			try (Inverter inverter = smaLogger.createInverter("192.168.0.12")) {
				LOGGER.info("Inverter {} logging on... ", inverter.getIP());
				inverter.logon(PASSWORD);
				LOGGER.info("Inverter {} logged on. ", inverter.getIP());
				
				System.out.println("Getting some data...");
				TypeLabelResponse labels = inverter.getInverterData(new TypeLabelRequest());
		        System.out.printf("SUSyID: %d - SN: %d\n", inverter.getSUSyID(), inverter.getSerial());
		        System.out.printf("Device Name:      %s\n", labels.getDeviceName());
		        System.out.printf("Device Class:     %s\n", labels.getDeviceClass());
		        System.out.printf("Device Type:      %s\n", labels.getDeviceType());
		        System.out.printf("Software Version: %s\n", inverter.getInverterData(new SoftwareVersionRequest()));
		        System.out.printf("Serial number:    %d\n", inverter.getSerial());
			    if (labels.getDevClass() == InvDeviceClass.BatteryInverter) {
			    	Integer batteryStatus = inverter.getInverterData(new BatteryChargeStatusRequest());
		            System.out.printf("Batt. Charging Status: %lu%%\n", batteryStatus);
	
		            BatteryInfoResponse batteryInfo = inverter.getInverterData(new BatteryInfoRequest());
		            System.out.printf("Batt. Temperature: %3.1f%sC\n", batteryInfo.getBatteryTemperature(), Misc.SYM_DEGREE); // degree symbol is different on windows/linux
		            System.out.printf("Batt. Voltage    : %3.2fV\n", batteryInfo.getBatteryVoltage());
		            System.out.printf("Batt. Current    : %2.3fA\n", batteryInfo.getBatteryAmpere());
			    }
		
			    String deviceStatus = inverter.getInverterData(new DeviceStatusRequest());
		        System.out.printf("Device Status:      %s\n", deviceStatus);
		
		        Double temperature = inverter.getInverterData(new InverterTemperatureRequest());
		        System.out.printf("Device Temperature: %3.1f%sC\n", temperature, Misc.SYM_DEGREE); // degree symbol is different on windows/linux
		
				if (labels.getDevClass() == InvDeviceClass.SolarInverter) {
					System.out.printf("GridRelay Status:      %s\n", inverter.getInverterData(new GridRelayStatusRequest()));
			    }
		
				ThreePhaseResponse<Integer> maxAcPower = inverter.getInverterData(new MaxACPowerRequest());
				if (maxAcPower.getTotal() == 0) {
					maxAcPower = inverter.getInverterData(new MaxACPower2Request());
				}
		        System.out.printf("Pac max phase 1: %dW\n", maxAcPower.getValue1());
		        System.out.printf("Pac max phase 2: %dW\n", maxAcPower.getValue2());
		        System.out.printf("Pac max phase 3: %dW\n", maxAcPower.getValue3());
				
		        EnergyProductionResponse energyProduction = inverter.getInverterData(new EnergyProductionRequest());
		        OperationTimeResponse operationTime = inverter.getInverterData(new OperationTimeRequest());
		        
		        System.out.println("Energy Production:");
		        System.out.printf("\tEToday: %.3fkWh\n", Misc.tokWh(energyProduction.getTodayEnergy()));
		        System.out.printf("\tETotal: %.3fkWh\n", Misc.tokWh(energyProduction.getTotalEnergy()));
		        System.out.printf("\tOperation Time: %.2fh\n", Misc.toHour(operationTime.getOperationTime()));
		        System.out.printf("\tFeed-In Time  : %.2fh\n", Misc.toHour(operationTime.getFeedInTime()));
		
		        SpotDcPowerResponse dcPower = inverter.getInverterData(new SpotDcPowerRequest());
		        SpotDCVoltageResponse dcVoltage = inverter.getInverterData(new SpotDCVoltageRequest());
		        System.out.println("DC Spot Data:");
		        System.out.printf("\tString 1 Pdc: %7.3fkW - Udc: %6.2fV - Idc: %6.3fA\n", Misc.tokW(dcPower.getPdc1()), dcVoltage.getVdc1(), dcVoltage.getIdc1());
		        System.out.printf("\tString 2 Pdc: %7.3fkW - Udc: %6.2fV - Idc: %6.3fA\n", Misc.tokW(dcPower.getPdc2()), dcVoltage.getVdc2(), dcVoltage.getIdc2());
		        
		        ThreePhaseResponse<Integer> power = inverter.getInverterData(new SpotAcPowerRequest());
		        ACVoltageAmpereResponse voltAmp = inverter.getInverterData(new SpotAcVoltageRequest());
		        Integer totalPower = inverter.getInverterData(new SpotAcTotalPowerRequest());
		        
		        System.out.println("AC Spot Data:");
		        System.out.printf("\tPhase 1 Pac : %7dW - Uac: %6.2fV - Iac: %6.3fA\n", power.getValue1(), voltAmp.getVoltage().getValue1(), voltAmp.getAmpere().getValue1());
		        System.out.printf("\tPhase 2 Pac : %7dW - Uac: %6.2fV - Iac: %6.3fA\n", power.getValue2(), voltAmp.getVoltage().getValue2(), voltAmp.getAmpere().getValue2());
		        System.out.printf("\tPhase 3 Pac : %7dW - Uac: %6.2fV - Iac: %6.3fA\n", power.getValue3(), voltAmp.getVoltage().getValue3(), voltAmp.getAmpere().getValue3());
		        System.out.printf("\tTotal Pac   : %7.3fkW\n", Misc.tokW(totalPower.intValue()));
	
		        SpotGridFrequencyRequest freqReq = new SpotGridFrequencyRequest();
				Double frequency = inverter.getInverterData(freqReq);
		        System.out.printf("Grid Freq. : %.2fHz\n", frequency);
		
			    if (labels.getDevClass() == InvDeviceClass.SolarInverter) {
					System.out.printf("Current Inverter Time: %s\n", Misc.printDate(freqReq.getDatetime()));
				}
			    
			    Map<LocalDateTime, DoubleSummaryStatistics> dayData = inverter.getInverterData(new DayDataRequest()).stream().collect(
			    		Collectors.groupingBy(d -> d.getDt().truncatedTo(ChronoUnit.HOURS), 
			    				Collectors.summarizingDouble(d -> d.getPower())));
			    for (LocalDateTime dt : new TreeSet<>(dayData.keySet())) {
			    	System.out.println(dt + ": "+dayData.get(dt).getAverage());
			    }
				
				System.out.println("Shutting down SMA Logger.");
			}
		}
	}
}
