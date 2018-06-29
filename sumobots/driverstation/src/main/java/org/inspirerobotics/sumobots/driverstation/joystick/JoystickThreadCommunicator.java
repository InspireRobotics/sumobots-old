package org.inspirerobotics.sumobots.driverstation.joystick;

import org.inspirerobotics.sumobots.driverstation.DriverStationBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.HashMap;
import java.util.logging.Logger;

public class JoystickThreadCommunicator {

	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel;
	private final InputThread thread;
	private final DriverStationBackend driverStationBackend;

	private long nextUpdateTime;

	private HashMap<String, Float> inputValues = new HashMap<String, Float>();

	private boolean joystickStatus = false;

	public JoystickThreadCommunicator(DriverStationBackend dsBack) {
		threadChannel = new ThreadChannel();
		thread = new InputThread(threadChannel.createPair());
		thread.start();

		this.driverStationBackend = dsBack;
	}

	public void shutdown() {
		logger.fine("Interrupting joystick thread.");
		thread.interrupt();
	}

	public void update() {
		if (nextUpdateTime < System.currentTimeMillis()) {
			nextUpdateTime = System.currentTimeMillis() + 100;

			pollMessages();
			sendValuesToRobot();
		}
	}

	public void sendValuesToRobot() {
		driverStationBackend.updateJoystickValues(inputValues);
	}

	public void updateNetworkingTable(NetworkTable networkTable) {
		if (joystickStatus == false) {
			networkTable.put("Joysticks", "Not Connected");
			return;
		}

		networkTable.put("Joysticks", "Connected");
	}

	private void pollMessages() {
		InterThreadMessage m = null;
		while ((m = threadChannel.poll()) != null) {
			onFrontendMessageReceived(m);
		}
	}

	@SuppressWarnings("unchecked")
	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		switch (name) {
			case "joystick_status":
				joystickStatus = (boolean) m.getData();

				driverStationBackend.onJoysticksConnected(joystickStatus);

				break;
			case "input_values":
				inputValues = (HashMap<String, Float>) m.getData();
				break;
			default:
				logger.warning("Unknown Message Recieved on Backend: " + name);
				break;
		}
	}
}
