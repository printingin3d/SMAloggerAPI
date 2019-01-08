package eu.printingin3d.smalogger.api.test;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.Inverter;
import eu.printingin3d.smalogger.api.requestvisitor.EnergyProductionRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SoftwareVersionRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotAcPowerRequest;
import eu.printingin3d.smalogger.api.requestvisitor.SpotAcVoltageRequest;
import eu.printingin3d.smalogger.api.requestvisitor.TypeLabelRequest;
import eu.printingin3d.smalogger.api.response.ACVoltageAmpereResponse;
import eu.printingin3d.smalogger.api.response.EnergyProductionResponse;
import eu.printingin3d.smalogger.api.response.ThreePhaseResponse;
import eu.printingin3d.smalogger.api.response.TypeLabelResponse;
import eu.printingin3d.smalogger.api.smajava.Misc;
import eu.printingin3d.smalogger.api.smajava.SmaLogger;

/**
 * Example of usage of this api while using a thread to keep reading the
 * inverter after login.
 *
 */
public class ThreadedTest implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedTest.class);

    private static final int RUN_COUNT = 5; // Number of times to read the inverter.
    private static final int WAIT_TIME = 5000; // Wait time between reading the inverter again.

    private SmaLogger smaLogger;
    private Inverter inverter;

    public void initialize(String[] args) throws IOException {
        LOGGER.info("Initializing SMA Logger");
        smaLogger = new SmaLogger();
        LOGGER.info("SMA Logger succesfully initialized");
    }

    public void start() {
        // Manual creation
        inverter = smaLogger.createInverter("192.168.0.12");
        Thread t = new Thread(this);
        t.start();
    }

    public void stop() throws IOException {
        LOGGER.info("logging off inverter...");

        inverter.close();

        LOGGER.info("Shutting down SMA Logger.");
        smaLogger.close();
    }

    @Override
    public void run() {
        try {
            int counter = RUN_COUNT;
            System.out.println("logging on inverter...");

            System.out.printf("Inverter %s logged on... ", inverter.getIP());
            inverter.logon();

            TypeLabelResponse labels = inverter.getInverterData(new TypeLabelRequest());
            System.out.printf("SUSyID: %d - SN: %d\n", inverter.getSUSyID(), inverter.getSerial());
            System.out.printf("Device Name:      %s\n", labels.getDeviceName());
            System.out.printf("Device Class:     %s\n", labels.getDeviceClass());
            System.out.printf("Device Type:      %s\n", labels.getDeviceType());
            System.out.printf("Software Version: %s\n", inverter.getInverterData(new SoftwareVersionRequest()));
            System.out.printf("Serial number:    %d\n", inverter.getSerial());
            while (counter > 0) {
                EnergyProductionResponse energyProduction = inverter.getInverterData(new EnergyProductionRequest());
                System.out.println("==================================");
                System.out.println("Energy Production:");
                System.out.printf("\tEToday: %.3fkWh\n", Misc.tokWh(energyProduction.getTodayEnergy()));
                System.out.printf("\tETotal: %.3fkWh\n", Misc.tokWh(energyProduction.getTotalEnergy()));

                ThreePhaseResponse<Integer> power = inverter.getInverterData(new SpotAcPowerRequest());
                ACVoltageAmpereResponse voltAmp = inverter.getInverterData(new SpotAcVoltageRequest());

                System.out.println("AC Spot Data:");
                System.out.printf("\tPhase 1 Pac : %7dW - Uac: %6.2fV - Iac: %6.3fA\n", power.getValue1(),
                        voltAmp.getVoltage().getValue1(), voltAmp.getAmpere().getValue1());
                System.out.printf("\tPhase 2 Pac : %7dW - Uac: %6.2fV - Iac: %6.3fA\n", power.getValue2(),
                        voltAmp.getVoltage().getValue2(), voltAmp.getAmpere().getValue2());
                System.out.printf("\tPhase 3 Pac : %7dW - Uac: %6.2fV - Iac: %6.3fA\n", power.getValue3(),
                        voltAmp.getVoltage().getValue3(), voltAmp.getAmpere().getValue3());
                System.out.printf("\tTotal Pac   : %7dW\n", power.getTotal());

                counter--;
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    counter = 0;
                    e.printStackTrace();
                }
            }
            stop();
        } catch (IOException e1) {
            LOGGER.error("Exception: ", e1);
        }
    }

    public static void main(String[] args) throws IOException {
        ThreadedTest tt = new ThreadedTest();
        tt.initialize(args);
        tt.start();
    }
}
