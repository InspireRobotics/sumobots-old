package org.inspirerobotics.sumobots.driverstation;

import org.inspirerobotics.sumobots.driverstation.config.Settings;
import org.inspirerobotics.sumobots.driverstation.field.Field;
import org.inspirerobotics.sumobots.driverstation.joystick.JoystickThreadCommunicator;
import org.inspirerobotics.sumobots.driverstation.robot.Robot;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

public class DriverStationBackend extends Thread {

	private final Logger logger = InternalLog.getLogger();

	private final JoystickThreadCommunicator joystickThreadCommunicator;

	private final Field field = new Field(this);

	private final Robot robot = new Robot(this);

	private final Settings settings;

	private ThreadChannel channel;

	private boolean running;

	private String name = "";

	private NetworkTable table = new NetworkTable();

	public DriverStationBackend(ThreadChannel tc, Settings settings) {
		this.setName("Backend Thread");
		this.channel = tc;
		this.settings = settings;

		joystickThreadCommunicator = new JoystickThreadCommunicator(this);
	}

	@Override
	public void run() {
		generateDriverStationName();
		field.setDriverStationName(name);

		running = true;
		sendMessageToFrontend(new InterThreadMessage("field_conn_status", false));

		connectToField();

		if (!running) {
			shutdown();
			return;
		}

		runMainLoop();
	}

	private void connectToField() {
		while (running) {
			pollMessages();
			joystickThreadCommunicator.update();

			boolean connectionCreated = field.tryToCreateConnection(getFieldIp(), settings.nonFieldMode());

			if (connectionCreated) {
				break;
			} else {
				logger.info("Failed to connect to the field! Waiting 3 seconds...");
				sleepCatchException(3000);
			}
		}

		if (!running) {
			return;
		}

		onFieldConnectionCreated();
	}

	private void onFieldConnectionCreated() {
		logger.info("Established Field-DS Connection! Starting main loop");
		sendMessageToFrontend(new InterThreadMessage("field_conn_status", true));
	}

	private void sleepCatchException(long time) {
		logger.fine("Sleeping " + time + "ms");

		try {
			sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String getFieldIp() {
		return settings.fieldIP();
	}

	private void generateDriverStationName() {
		String name = settings.dsName("DS-" + new Random().nextInt(10000));

		setDriverStationName(name);
	}

	public void sendMessageToFrontend(InterThreadMessage m) {
		channel.add(m);
	}

	private void runMainLoop() {
		while (running) {
			field.update();
			robot.update();
			joystickThreadCommunicator.update();

			checkConnections();
			updateNetworkTable();
			pollMessages();
		}

		logger.info("Backend Shutdown...");
		shutdown();
	}

	private void checkConnections() {
		if (field.getFieldConnection().isClosed()) {
			onFieldConnectionLost();
			connectToField();
		}

		robot.checkIfStillConnected();

		if (!robot.connected())
			attemptRobotConnection();
	}

	private void attemptRobotConnection() {
		if (robot.inConnectionAttemptTimeout())
			return;

		if (!robot.attemptConnection(settings.robotIP())) {
			logger.info("Failed to connect to the robot! Waiting 3 seconds...");
		}
	}

	private void onFieldConnectionLost() {
		sendMessageToFrontend(new InterThreadMessage("field_conn_status", false));
		sendMessageToFrontend(new InterThreadMessage("robot_conn_status", false));

		logger.info("Lost Connection to Field!");

		if (robot.getRobotConnection() != null)
			robot.getRobotConnection().endConnection();
		field.updateMatchStatus(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void updateNetworkTable() {
		table.put("Logger Level", "" + logger.getLevel());
		table.put("Name", name);
		table.put("Time Period", "" + getTimePeriod());

		robot.addStatsToNetworkTable(table);
		joystickThreadCommunicator.updateNetworkingTable(table);
		field.setNetworkingTable(table);
	}

	private void pollMessages() {
		InterThreadMessage m = null;
		while ((m = channel.poll()) != null) {
			onFrontendMessageReceived(m);

			if (!running)
				return;
		}
	}

	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		logger.fine("Recieved Message from Frontend: " + name);

		switch (name) {
			case "exit_app":
				logger.info("Exiting Backend Thread!");
				shutdown();
				break;
			case "new_state":
				field.updateMatchStatus(ArchetypalMessages.enterNewMatchPeriod((TimePeriod) m.getData()));
			default:
				logger.warning("Unknown Message Recieved on Backend: " + name);
				break;
		}
	}

	public void updateJoystickValues(HashMap<String, Float> inputValues) {
		if (robot.connected()) {
			Message m = new Message(MessageType.JOYSTICK_UPDATE);

			for (Entry<String, Float> entry : inputValues.entrySet()) {
				if (field.getCurrentPeriod() == TimePeriod.GAME) {
					m.addData(entry.getKey(), "" + entry.getValue());
				} else {
					m.addData(entry.getKey(), "" + 0f);
				}
			}

			robot.getRobotConnection().sendMessage(m);
		}
	}

	public void onJoysticksConnected(boolean data) {
		if (data) {
			sendMessageToFrontend(new InterThreadMessage("joystick_status", true));
		} else {
			sendMessageToFrontend(new InterThreadMessage("joystick_status", false));
		}
	}

	public void shutdown() {
		joystickThreadCommunicator.shutdown();
		field.shutdown();
		if (robot.getRobotConnection() != null)
			robot.getRobotConnection().endConnection();
		running = false;
	}

	public void setDriverStationName(String n) {
		sendMessageToFrontend(new InterThreadMessage("new_name", n));
		name = n;
		field.setDriverStationName(name);
	}

	@Deprecated
	public Connection getConn() {
		return field.getFieldConnection();
	}

	public TimePeriod getTimePeriod() {
		return field.getCurrentPeriod();
	}

	public JoystickThreadCommunicator getJoystickThreadCommunicator() {
		return joystickThreadCommunicator;
	}
}
