package org.inspirerobotics.sumobots.driverstation;

import org.inspirerobotics.sumobots.driverstation.field.Field;
import org.inspirerobotics.sumobots.driverstation.robot.Robot;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.config.Config;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.Random;
import java.util.logging.Logger;

public class DriverStationBackend extends Thread {

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	private final Field field = new Field(this);

	private final Robot robot = new Robot(this);

	private Config config;

	private ThreadChannel channel;

	private boolean running;

	private String name = "";

	private NetworkTable table = new NetworkTable();

	public DriverStationBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
	}

	@Override
	public void run() {
		logger.setLevel(Settings.LOG_LEVEL);
		loadConfig();

		running = true;
		sendMessageToFrontend(new InterThreadMessage("conn_status", false));

		connectToField();

		if (!running) {
			shutdown();
			return;
		}

		runMainLoop();
	}

	private void loadConfig() {
		config = new Config("DriverStation");
	}

	private void connectToField() {
		while (running) {
			pollMessages();

			boolean connectionCreated = field.tryToCreateConnection(getFieldIp());

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
		generateDriverStationName();
		logger.info("Established Field-DS Connection! Starting main loop");
		sendMessageToFrontend(new InterThreadMessage("conn_status", true));
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
		if (config != null) {
			if (config.getString("fieldIP") != null) {
				return config.getString("fieldIP");
			}
		}
		return "localhost";
	}

	private void generateDriverStationName() {
		if (config != null) {
			if (config.getString("name") != null) {
				setDriverStationName("DS-" + config.getString("name"));
				return;
			}
		}
		setDriverStationName("DS-" + new Random().nextInt(10000));
	}

	public void sendMessageToFrontend(InterThreadMessage m) {
		channel.add(m);
	}

	private void runMainLoop() {
		while (running) {
			field.update();
			robot.update();

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

		if (!robot.connected())
			attemptRobotConnection();
	}

	private void attemptRobotConnection() {
		if (robot.inConnectionAttemptTimeout())
			return;

		if (robot.attemptConnection("localhost")) {

		} else {
			logger.info("Failed to connect to the robot! Waiting 3 seconds...");
		}
	}

	private void onFieldConnectionLost() {
		sendMessageToFrontend(new InterThreadMessage("conn_status", false));

		logger.info("Lost Connection to Field!");
		field.updateMatchStatus(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void updateNetworkTable() {
		table.put("Logger Level", "" + logger.getLevel());
		table.put("Name", name);
		table.put("Time Period", "" + getTimePeriod());

		robot.addStatsToNetworkTable(table);

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

	public void shutdown() {
		field.shutdown();
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

}
