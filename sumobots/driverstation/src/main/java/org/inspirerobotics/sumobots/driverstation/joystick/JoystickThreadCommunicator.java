package org.inspirerobotics.sumobots.driverstation.joystick;

import java.util.logging.Logger;

import org.inspirerobotics.sumobots.driverstation.DriverStationBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

public class JoystickThreadCommunicator {

	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel;
	private final InputThread thread;
	private final DriverStationBackend driverStationBackend;

	public JoystickThreadCommunicator(DriverStationBackend dsBack) {
		threadChannel = new ThreadChannel();
		thread = new InputThread(threadChannel.createPair());
		thread.start();

		this.driverStationBackend = dsBack;
	}

	public void update() {
		pollMessages();
	}

	private void pollMessages() {
		InterThreadMessage m = null;
		while ((m = threadChannel.poll()) != null) {
			onFrontendMessageReceived(m);
		}
	}

	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		logger.fine("Recieved Message from Input Thread: " + name);

		switch (name) {
			case "joystick_status":
				driverStationBackend.onJoysticksConnected((boolean) m.getData());
				break;
			default:
				logger.warning("Unknown Message Recieved on Backend: " + name);
				break;
		}
	}
}
