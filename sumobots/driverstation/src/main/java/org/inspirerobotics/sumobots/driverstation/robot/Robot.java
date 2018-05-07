package org.inspirerobotics.sumobots.driverstation.robot;

import org.inspirerobotics.sumobots.driverstation.DriverStationBackend;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Robot implements ConnectionListener {

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	private Connection robotConnection;
	private final DriverStationBackend backend;

	public Robot(DriverStationBackend b) {
		backend = b;
	}

	public Connection getRobotConnection() {
		return robotConnection;
	}

	public boolean attemptConnection(String ip) {
		try {
			Socket socket = new Socket(ip, Resources.ROBOT_PORT);
			setRobotConnection(new Connection(socket, this));
			logger.info("Found connection!");
			return true;
		} catch (IOException e) {
			// TODO we should probably throw here and catch lower in the stack
		}
		return false;
	}

	@Override
	public void receivedMessage(Message message, Connection connection) {

	}

	private void setRobotConnection(Connection robotConnection) {
		this.robotConnection = robotConnection;
	}

	public void update() {
		if (robotConnection != null)
			robotConnection.update();
	}
}
