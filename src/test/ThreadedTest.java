package test;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inverter.Inverter;
import inverterdata.InverterDataType;
import smajava.SmaLogger;
import smajava.misc;

/**
 * Example of usage of this api while using a thread to keep reading the
 * inverter after login.
 *
 */
public class ThreadedTest implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedTest.class);

	private static final String PASSWORD = "0000"; // Default password
	private static final int RUN_COUNT = 5; // Number of times to read the inverter.
	private static final int WAIT_TIME = 5000; // Wait time between reading the inverter again.
	
	private SmaLogger smaLogger;
	private Inverter inverter;

	public void initialize(String[] args) throws IOException {
		smaLogger = new SmaLogger();

		LOGGER.info("Initializing SMA Logger");
		smaLogger.initialize();
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

		inverter.Logoff();

		LOGGER.info("Shutting down SMA Logger.");
		smaLogger.close();
	}

	@Override
	public void run() {
		try {
			boolean loggedOn = false;
			int counter = RUN_COUNT;
			System.out.println("logging on inverter...");

			System.out.printf("Inverter %s logged on... ", inverter.getIP());
			inverter.logon(PASSWORD);

			inverter.GetInverterData(InverterDataType.SoftwareVersion);
			inverter.GetInverterData(InverterDataType.TypeLabel);
			System.out.printf("SUSyID: %d - SN: %d\n", inverter.Data.SUSyID, inverter.Data.Serial);
			System.out.printf("Device Name:      %s\n", inverter.Data.DeviceName);
			System.out.printf("Device Class:     %s\n", inverter.Data.DeviceClass);
			System.out.printf("Device Type:      %s\n", inverter.Data.DeviceType);
			System.out.printf("Software Version: %s\n", inverter.Data.SWVersion);
			System.out.printf("Serial number:    %d\n", inverter.Data.Serial);
			if (loggedOn) {
				while (counter > 0) {
					inverter.GetInverterData(InverterDataType.EnergyProduction);
					System.out.println("==================================");
					System.out.println("Energy Production:");
					System.out.printf("\tEToday: %.3fkWh\n", misc.tokWh(inverter.Data.EToday));
					System.out.printf("\tETotal: %.3fkWh\n", misc.tokWh(inverter.Data.ETotal));

					inverter.GetInverterData(InverterDataType.SpotACPower);
					inverter.GetInverterData(InverterDataType.SpotACVoltage);
					inverter.GetInverterData(InverterDataType.SpotACTotalPower);

					// Calculate missing AC Spot Values
					inverter.CalcMissingSpot();

					System.out.println("AC Spot Data:");
					System.out.printf("\tPhase 1 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n",
							misc.tokW(inverter.Data.Pac1), misc.toVolt(inverter.Data.Uac1),
							misc.toAmp(inverter.Data.Iac1));
					System.out.printf("\tPhase 2 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n",
							misc.tokW(inverter.Data.Pac2), misc.toVolt(inverter.Data.Uac2),
							misc.toAmp(inverter.Data.Iac2));
					System.out.printf("\tPhase 3 Pac : %7.3fkW - Uac: %6.2fV - Iac: %6.3fA\n",
							misc.tokW(inverter.Data.Pac3), misc.toVolt(inverter.Data.Uac3),
							misc.toAmp(inverter.Data.Iac3));
					System.out.printf("\tTotal Pac   : %7.3fkW\n", misc.tokW(inverter.Data.TotalPac));

					counter--;
					try {
						Thread.sleep(WAIT_TIME);
					} catch (InterruptedException e) {
						counter = 0;
						e.printStackTrace();
					}
				}
			}
			stop();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		ThreadedTest tt = new ThreadedTest();
		tt.initialize(args);
		tt.start();
	}
}
