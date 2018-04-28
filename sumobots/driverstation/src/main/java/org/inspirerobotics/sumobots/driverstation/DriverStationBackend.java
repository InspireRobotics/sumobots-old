package org.inspirerobotics.sumobots.driverstation;

import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.config.Config;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DriverStationBackend extends Thread implements ConnectionListener {

	public static final boolean nonFieldMode = false;

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	private Connection conn;

	private Config config;

	private TimePeriod currentPeriod = TimePeriod.DISABLED;

	private ThreadChannel channel;

	private boolean running;

	private String name = "";

	private NetworkTable table = new NetworkTable();

	public DriverStationBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
		logger.setLevel(Level.ALL);
	}

	@Override
	public void run() {
		logger.setLevel(Level.FINE);
		loadConfig();
		
		running = true;
		channel.add(new InterThreadMessage("conn_status", false));
		
		connect();

		if (!running) {
			shutdown();
			return;
		}

		runMainLoop();
	}

	private void loadConfig() {
		config = new Config("DriverStation");
	}
	
	private void connect() {
		while (running) {
			pollMessages();
			
			
			if(nonFieldMode) {
				conn = new EmptyConnection();
				break;
			}
			
			try {
				Socket socket = new Socket(getFieldIp(), Resources.SERVER_PORT);
				conn = new Connection(socket, this);
				conn.setBindedTable(table);
				logger.info("Found connection!");
				break;
			} catch (IOException e) {
				logger.info("Failed to connect! Waiting 3 seconds...");

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		if(!running) {
			return;
		}
		
		generateDriverStationName();
		logger.info("Established Field-DS Connection! Starting main loop");
		channel.add(new InterThreadMessage("conn_status", true));
	}
	
	private String getFieldIp() {
		if(config != null) {
			if(config.getString("fieldIP") != null) {
				return config.getString("fieldIP");
			}
		}
		return "localhost";
	}

	private void generateDriverStationName() {
		if(config != null) {
			if(config.getString("name") != null) {
				setDriverStationName("DS-" + config.getString("name"));
				return;
			}
		}
		setDriverStationName("DS-" + new Random().nextInt(10000));
	}
	
	private void runMainLoop() {

		while (running) {
			conn.update();
			if (conn.isClosed()) {
				channel.add(new InterThreadMessage("conn_status", false));

				logger.info("Lost Connection to Field! Waiting 2.5 seconds before reconnecting...");
				updateMatchStatus(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));

				try {
					Thread.sleep(2500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				connect();
			}

			updateNetworkTable();
			pollMessages();
		}

		logger.info("Backend Shutdown...");
		shutdown();
	}

	private void updateNetworkTable() {
		if (conn != null) {
			if(conn.getSocket() != null) {
				table.put("ping", conn.getCurrentPing() + " ms");
				table.put("connection name", conn.getConnectionName());
				table.put("ip", conn.getSocket().getLocalAddress().toString());
			}
		}
		
		table.put("Logger Level", "" + logger.getLevel());
		table.put("Name", name);
		table.put("Time Period", "" + currentPeriod);

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
			updateMatchStatus(ArchetypalMessages.enterNewMatchPeriod((TimePeriod) m.getData()));
		default:
			logger.warning("Unknown Message Recieved on Backend: " + name);
			break;
		}
	}

	public void shutdown() {
		if (conn != null)
			conn.endConnection();
		running = false;
	}

	@Override
	public void recievedMessage(Message message, Connection connection) {
		MessageType type = message.getType();

		if (type == MessageType.MATCH_STATE_UPDATE) {
			updateMatchStatus(message);
		}
	}

	public void setDriverStationName(String n) {
		channel.add(new InterThreadMessage("new_name", n));
		name = n;
		conn.sendMessage(ArchetypalMessages.setName(name));
	}

	private void updateMatchStatus(Message message) {
		String timePeriod = (String) message.getData("new_period");
		currentPeriod = TimePeriod.fromString(timePeriod);

		channel.add(new InterThreadMessage("new_period", currentPeriod));

		logger.info("Entering match period: " + currentPeriod.getName());
	}

	public Connection getConn() {
		return conn;
	}

}
