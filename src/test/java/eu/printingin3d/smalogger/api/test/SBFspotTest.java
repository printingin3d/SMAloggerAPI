package eu.printingin3d.smalogger.api.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.physics.DoubleValue;
import eu.printingin3d.physics.Frequency;
import eu.printingin3d.physics.Power;
import eu.printingin3d.physics.Temperature;
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
import eu.printingin3d.smalogger.api.smajava.SmaLogger;

/**
 * This example basically does the exact same as the original SBFspot program
 * minus the logging stuff Mainly used to test all the functionality of the API.
 *
 */
public class SBFspotTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SBFspotTest.class);

    private static SmaLogger smaLogger;
    private static Inverter inverter;
    
    private TypeLabelResponse savedLabels;

    @BeforeClass
    public static void setupClass() throws IOException {
        LOGGER.info("Initializing SMA Logger");
        smaLogger = new SmaLogger();
        LOGGER.info("SMA Logger succesfully initialized");
        List<Inverter> inverters = smaLogger.detectDevices();

        assertFalse("At least one inverter should be found", inverters.isEmpty());

        inverter = inverters.get(0);
        LOGGER.info("Inverter {} logging on... ", inverter.getIP());
        inverter.logon();
        LOGGER.info("Inverter {} logged on. ", inverter.getIP());
    }

    @AfterClass
    public static void tearDown() throws IOException {
        LOGGER.info("Shutting down SMA Logger.");

        inverter.close();
        smaLogger.close();
    }
    
    private TypeLabelResponse getLabelResponse() throws IOException {
        if (savedLabels == null) {
            savedLabels = inverter.getInverterData(new TypeLabelRequest());
        }
        return savedLabels;
    }
    
    @Test
    public void testInverterFields() {
        assertNotNull(inverter.getSUSyID());
        assertNotNull(inverter.getSerial());
        LOGGER.info("SUSyID: {} - SN: {}", inverter.getSUSyID(), inverter.getSerial());
    }
    
    @Test
    public void testLabels() throws IOException {
        TypeLabelResponse labels = getLabelResponse();
        LOGGER.info("Device Name:  {}", labels.getDeviceName());
        LOGGER.info("Device Class: {}", labels.getDeviceClass());
        LOGGER.info("Device Type:  {}", labels.getDeviceType());
    }
    
    @Test
    public void testSoftwareVersion() throws IOException {
        String version = inverter.getInverterData(new SoftwareVersionRequest());
        assertNotNull(version);
        LOGGER.info("Software Version: {}", version);
    }
    
    @Test
    public void testDeviceStatus() throws IOException {
        String deviceStatus = inverter.getInverterData(new DeviceStatusRequest());
        assertNotNull(deviceStatus);
        LOGGER.info("Device Status: " + deviceStatus);
    }
    
    @Test
    public void testDeviceTemperature() throws IOException {
        Temperature temperature = inverter.getInverterData(new InverterTemperatureRequest());
        assertNotNull(temperature);
        LOGGER.info("Device Temperature: {}", temperature);
    }
    
    @Test
    public void testGridRelayStatus() throws IOException {
        if (getLabelResponse().getDevClass() == InvDeviceClass.SolarInverter) {
            String gridRelayStatus = inverter.getInverterData(new GridRelayStatusRequest());
            assertNotNull(gridRelayStatus);
            LOGGER.info("GridRelay Status: {}", gridRelayStatus);
        }
    }
    
    @Test
    public void testMaxACPower() throws IOException {
        ThreePhaseResponse<Power> maxAcPower = inverter.getInverterData(new MaxACPowerRequest());
        if (maxAcPower.getTotal().isZero()) {
            maxAcPower = inverter.getInverterData(new MaxACPower2Request());
        }
        assertNotNull(maxAcPower);
        LOGGER.info("Pac max {}, {}, {}", maxAcPower.getValue1(), maxAcPower.getValue2(), maxAcPower.getValue3());
    }
    
    @Test
    public void testEnergyProduction() throws IOException {
        EnergyProductionResponse energyProduction = inverter.getInverterData(new EnergyProductionRequest());
        assertNotNull(energyProduction);
        LOGGER.info("Energy production today: {}", energyProduction.getTodayEnergy());
        LOGGER.info("Energy production total: {}", energyProduction.getTotalEnergy());
    }
    
    @Test
    public void testOperationTime() throws IOException {
        OperationTimeResponse operationTime = inverter.getInverterData(new OperationTimeRequest());
        assertNotNull(operationTime);
        LOGGER.info("Operation Time: {}", operationTime.getOperationTime());
        LOGGER.info("Feed-In Time  : {}", operationTime.getFeedInTime());
    }
    
    @Test
    public void testSpotDcPower() throws IOException {
        SpotDcPowerResponse dcPower = inverter.getInverterData(new SpotDcPowerRequest());
        assertNotNull(dcPower);
        LOGGER.info("DC Spot Power: {} + {}", dcPower.getPdc1(), dcPower.getPdc2());
    }
    
    @Test
    public void testSpotDCVoltage() throws IOException {
        SpotDCVoltageResponse dcVoltage = inverter.getInverterData(new SpotDCVoltageRequest());
        assertNotNull(dcVoltage);
        LOGGER.info("DC Spot Voltage/Current: {}/{}, {}/{}", 
                dcVoltage.getVdc1(), dcVoltage.getIdc1(), 
                dcVoltage.getVdc2(), dcVoltage.getIdc2());
    }
    
    @Test
    public void testSpotAcVoltage() throws IOException {
        ACVoltageAmpereResponse voltAmp = inverter.getInverterData(new SpotAcVoltageRequest());
        assertNotNull(voltAmp);
        LOGGER.info("AC Spot Voltage/Current: {}/{}, {}/{}, {}/{}",
                voltAmp.getVoltage().getValue1(), voltAmp.getAmpere().getValue1(),
                voltAmp.getVoltage().getValue2(), voltAmp.getAmpere().getValue2(),
                voltAmp.getVoltage().getValue3(), voltAmp.getAmpere().getValue3());
    }
    
    @Test
    public void testSpotAcPower() throws IOException {
        ThreePhaseResponse<Power> power = inverter.getInverterData(new SpotAcPowerRequest());
        assertNotNull(power);
        LOGGER.info("AC Spot Power: {} + {} + {}", power.getValue1(), power.getValue2(), power.getValue3());
    }
    
    @Test
    public void testSpotAcTotalPower() throws IOException {
        Power totalPower = inverter.getInverterData(new SpotAcTotalPowerRequest());
        assertNotNull(totalPower);
        LOGGER.info("Total AC Power: {}", totalPower);
    }
    
    @Test
    public void testSpotGridFrequency() throws IOException {
        Frequency frequency = inverter.getInverterData(new SpotGridFrequencyRequest());
        assertNotNull(frequency);
        LOGGER.info("Grid Frequency: {}", frequency);
    }
    
    @Test
    public void testDayData() throws IOException {
        Map<LocalDateTime, DoubleSummaryStatistics> dayData = inverter.getInverterData(new DayDataRequest()).stream()
                .collect(Collectors.groupingBy(d -> d.getDt().truncatedTo(ChronoUnit.HOURS),
                        Collectors.summarizingDouble(d -> DoubleValue.readValue(d.getPower()))));
        for (LocalDateTime dt : new TreeSet<>(dayData.keySet())) {
            if (dayData.get(dt).getAverage() > 0.0) {
                LOGGER.info(dt + ": " + new Power(dayData.get(dt).getAverage()));
            }
        }
    }
    
    @Test
    public void testBatteryChargeStatus() throws IOException {
        TypeLabelResponse labels = getLabelResponse();
        if (labels.getDevClass() == InvDeviceClass.BatteryInverter) {
            Integer batteryStatus = inverter.getInverterData(new BatteryChargeStatusRequest());
            LOGGER.info("Batt. Charging Status: {}%", batteryStatus);
        }
    }
    
    @Test
    public void testBatteryInfoStatus() throws IOException {
        TypeLabelResponse labels = getLabelResponse();
        if (labels.getDevClass() == InvDeviceClass.BatteryInverter) {
            BatteryInfoResponse batteryInfo = inverter.getInverterData(new BatteryInfoRequest());
            LOGGER.info("Batt. Temperature: {}", batteryInfo.getBatteryTemperature());
            LOGGER.info("Batt. Voltage    : {}", batteryInfo.getBatteryVoltage());
            LOGGER.info("Batt. Current    : {}", batteryInfo.getBatteryAmpere());
        }
    }
}
