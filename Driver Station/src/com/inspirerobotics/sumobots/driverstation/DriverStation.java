package com.inspirerobotics.sumobots.driverstation;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.networking.Connection;
import com.inspirerobotics.sumobots.lib.networking.ConnectionListener;
import com.inspirerobotics.sumobots.lib.networking.Message;
import com.inspirerobotics.sumobots.lib.networking.MessageType;

public class DriverStation implements ConnectionListener {

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);
	private Connection conn;
	
	public DriverStation() {
		logger.setLevel(Level.ALL);
		try {
			Socket socket = new Socket("localhost", Resources.SERVER_PORT);
			conn = new Connection(socket, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Finished initialization! Starting main loop");
		
		runMainLoop();
	}

	private void runMainLoop() {
		while(true){
			conn.update();
		}
	}

	@Override
	public void recievedMessage(Message message, Connection connection) {
		MessageType type = message.getType();
		
		if(type == MessageType.MATCH_STATE_UPDATE){
			updateMatchStatus(message);
		}
	}
	
	private void updateMatchStatus(Message message) {
		if(message.getData("update_type").equals("match_period")){
			String timePeriod = (String) message.getData("new_period");
			if(timePeriod.equals("init")){
				logger.info("Entering new Match Period: Initialization");
			}else if(timePeriod.equals("game")){
				logger.info("Entering new Match Period: Game");
			}else if(timePeriod.equals("end")){
				logger.info("ntering new Match Period: Disable");
			}
		}
	}

	public Connection getConn() {
		return conn;
	}
	
	public static void main(String[] args) {
		new DriverStation();
	}
}
