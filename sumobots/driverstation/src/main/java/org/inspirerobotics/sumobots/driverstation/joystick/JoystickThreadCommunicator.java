package org.inspirerobotics.sumobots.driverstation.joystick;

import java.util.HashMap;
import java.util.logging.Logger;

import org.inspirerobotics.sumobots.driverstation.DriverStationBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

public class JoystickThreadCommunicator {

	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel;
	private final InputThread thread;
	private final DriverStationBackend driverStationBackend;

	private HashMap<String, Float> inputValues = new HashMap<String, Float>();
	
	private boolean joystickStatus = false;

	public JoystickThreadCommunicator(DriverStationBackend dsBack) {
		threadChannel = new ThreadChannel();
		thread = new InputThread(threadChannel.createPair());
		thread.start();

		this.driverStationBackend = dsBack;
	}

	public void update() {
		pollMessages();
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
				driverStationBackend.updateJoystickValues(inputValues);
				break;
			default:
				logger.warning("Unknown Message Recieved on Backend: " + name);
				break;
		}
	}
}
