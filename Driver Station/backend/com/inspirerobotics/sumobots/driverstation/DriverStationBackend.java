package com.inspirerobotics.sumobots.driverstation;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.TimePeriod;
import com.inspirerobotics.sumobots.lib.concurrent.InterThreadMessage;
import com.inspirerobotics.sumobots.lib.concurrent.ThreadChannel;
import com.inspirerobotics.sumobots.lib.networking.connection.Connection;
import com.inspirerobotics.sumobots.lib.networking.connection.ConnectionListener;
import com.inspirerobotics.sumobots.lib.networking.message.ArchetypalMessages;
import com.inspirerobotics.sumobots.lib.networking.message.Message;
import com.inspirerobotics.sumobots.lib.networking.message.MessageType;
import com.inspirerobotics.sumobots.lib.networking.tables.NetworkTable;

/**
 * The second most important class for the driver station. Handles everything
 * that isn't the gui. This is started from the frontend. Roles include:
 * handling communication with the robot and field and handling the
 * joysticks/driving.
 * 
 * @author Noah
 *
 */
public class DriverStationBackend extends Thread implements ConnectionListener {

	/**
	 * The logger for Sumobots
	 */
	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	/**
	 * The connection to the field
	 */
	private Connection conn;

	/**
	 * The current time period on the driver station
	 */
	private TimePeriod currentPeriod = TimePeriod.DISABLED;

	/**
	 * The thread channel for the frontend and backend threads
	 */
	private ThreadChannel channel;

	/**
	 * 
	 * If the application is running
	 */
	private boolean running;

	/**
	 * The name of the current driver station
	 */
	private String name = "";
	
	/*
	 * the DS Network Table
	 */
	private NetworkTable table = new NetworkTable();
	

	public DriverStationBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
		logger.setLevel(Level.ALL);
	}

	/**
	 * Inits the socket, and sets a random name for the driver station
	 */
	@Override
	public void run() {
		running = true;
		channel.add(new InterThreadMessage("conn_status", false));

		connect();

		// If we closed the application while trying to connect,
		// exit and don't run the main loop
		if (!running) {
			shutdown();
			return;
		}

		runMainLoop();
	}

	private void connect() {
		// Connect to the server, wait 3 seconds if we fail and try again
		while (running) {
			pollMessages();
			
			try {
				Socket socket = new Socket("localhost", Resources.SERVER_PORT);
				conn = new Connection(socket, this);
				conn.setBindedTable(table);
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

		// We are now connected, lets start transfering messages...
		setDriverStationName("DS-" + new Random().nextInt(10000));
		logger.info("Established Field-DS Connection! Starting main loop");
		channel.add(new InterThreadMessage("conn_status", true));
	}

	/**
	 * The Driver Station main loop
	 */
	private void runMainLoop() {

		while (running) {
			conn.update();
			if (conn.isClosed()) {
				channel.add(new InterThreadMessage("conn_status", false));
				
				logger.info("Lost Connection to Field! Waiting 2.5 seconds before reconnecting...");
				
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
		if(conn != null) {
			table.put("ping", conn.getCurrentPing() + " ms");
			table.put("connection name", conn.getConnectionName());
		}
		table.put("Name", name);
		table.put("Time Period", ""+currentPeriod);
		
	}

	/**
	 * Checks and handles any messages recieved by the frontend
	 */
	private void pollMessages() {
		// While there are messages from the frontend, handle them
		InterThreadMessage m = null;
		while ((m = channel.poll()) != null) {
			onFrontendMessageReceived(m);

			// This needs to be here to prevent other messages from being
			// proccessed after the app is supposed to be closing
			if (!running)
				return;
		}
	}

	/**
	 * When a message is recieved by the frontend
	 * 
	 * @param m
	 *            the message recieved
	 */
	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		logger.fine("Recieved Message from Frontend: " + name);

		// Figure out what type of message it is
		switch (name) {
		case "exit_app":
			logger.info("Exiting Backend Thread!");
			shutdown();
			break;
		default: // If it reaches this we don't know what it is so print a
					// warning to the screen
			logger.warning("Unknown Message Recieved on Backend: " + name);
			break;
		}
	}

	/*
	 * Shuts down the backend - closes the connections and stops running
	 */
	public void shutdown() {
		if (conn != null)
			conn.endConnection();
		running = false;
	}

	/**
	 * When a message is recieved from the FMS
	 */
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

	/**
	 * Updates the Driver Station on the current match status. This update would be
	 * originated by the FMS
	 * 
	 * @param message
	 */
	private void updateMatchStatus(Message message) {
		String timePeriod = (String) message.getData("new_period");
		currentPeriod = TimePeriod.fromString(timePeriod);

		// Send the new period to the gui
		channel.add(new InterThreadMessage("new_period", currentPeriod));

		logger.info("Entering match period: " + currentPeriod.getName());
	}

	/**
	 * The connection to the field
	 */
	public Connection getConn() {
		return conn;
	}

}
