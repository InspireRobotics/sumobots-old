package com.inspirerobotics.sumobots.driverstation;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.TimePeriod;
import com.inspirerobotics.sumobots.lib.concurrent.ThreadChannel;
import com.inspirerobotics.sumobots.lib.networking.Connection;
import com.inspirerobotics.sumobots.lib.networking.ConnectionListener;
import com.inspirerobotics.sumobots.lib.networking.Message;
import com.inspirerobotics.sumobots.lib.networking.MessageType;

/**
 * The second most important class for the driver station. Handles everything 
 * that isn't the gui. This is started from the frontend. Roles include: handling communication with 
 * the robot and field and handling the joysticks/driving.
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
	
	public DriverStationBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
		logger.setLevel(Level.ALL);
	}
	
	@Override
	public void run() {
		try {
			Socket socket = new Socket("localhost", Resources.SERVER_PORT);
			conn = new Connection(socket, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Finished initialization! Starting main loop");
		
		runMainLoop();
	}
	
	/**
	 * The Driver Station main loop
	 */
	private void runMainLoop() {
		while(true){
			conn.update();
		}
	}
	
	/**
	 * When a message is recieved from the FMS
	 */
	@Override
	public void recievedMessage(Message message, Connection connection) {
		MessageType type = message.getType();
		
		if(type == MessageType.MATCH_STATE_UPDATE){
			updateMatchStatus(message);
		}
	}
	
	/**
	 * Updates the Driver Station on the current match status. This update would be originated by the FMS
	 * @param message
	 */
	private void updateMatchStatus(Message message) {
		if(message.getData("update_type").equals("match_period")){
			String timePeriod = (String) message.getData("new_period");
			currentPeriod = TimePeriod.fromString(timePeriod);
			
			logger.info("Entering match period: " + currentPeriod.getName());
		}
	}
	
	/**
	 * The connection to the field
	 */
	public Connection getConn() {
		return conn;
	}
	
}
