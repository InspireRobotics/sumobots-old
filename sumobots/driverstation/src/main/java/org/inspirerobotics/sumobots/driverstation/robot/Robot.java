package org.inspirerobotics.sumobots.driverstation.robot;

import org.inspirerobotics.sumobots.driverstation.DriverStationBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class Robot implements ConnectionListener {

	private final Logger logger = InternalLog.getLogger();

	private Connection robotConnection;
	private final DriverStationBackend backend;
	private long lastConnectionAttempt;
	private boolean previouslyConnected;

	public Robot(DriverStationBackend b) {
		backend = b;
	}

	public Connection getRobotConnection() {
		return robotConnection;
	}

	public boolean attemptConnection(String ip) {
		if (inConnectionAttemptTimeout()) {
			logger.warning("Cannot connect to robot during timeout");
			return false;
		}

		logger.fine("Attempting Robot Connection...");

		try {
			Socket socket = createSocket(ip);

			if (!socket.isConnected())
				throw new IOException();

			onConnectionMade(socket);
			return true;
		} catch (IOException e) {
			logger.fine("E: " + e);
			lastConnectionAttempt = System.currentTimeMillis();
		}
		return false;
	}

	public void addStatsToNetworkTable(NetworkTable table) {
		if (!connected()) {
			table.put("robot_connected", "false");
			return;
		}

		table.put("robot_connected", "false");
		table.put("robot_ip", robotConnection.getSocket().getInetAddress().toString());
		table.put("robot_ping", robotConnection.getCurrentPing() + " ms");
		table.put("robot_name", robotConnection.getConnectionName());
	}

	public void checkIfStillConnected() {
		if (previouslyConnected && !connected()) {
			previouslyConnected = false;
			backend.sendMessageToFrontend(new InterThreadMessage("robot_conn_status", false));
		}
	}

	public boolean connected() {
		if (getRobotConnection() == null) {
			return false;
		} else if (getRobotConnection().isClosed()) {
			return false;
		}
		return true;
	}

	private void onConnectionMade(Socket s) {
		setRobotConnection(new Connection(s, this));
		logger.info("Found connection!");
		lastConnectionAttempt = 0;
		previouslyConnected = true;
		backend.sendMessageToFrontend(new InterThreadMessage("robot_conn_status", true));
	}

	private Socket createSocket(String ip) throws IOException {
		Socket socket = new Socket();
		socket.setSoTimeout(Resources.SOCKET_TIMEOUT);
		socket.connect(new InetSocketAddress(ip, Resources.ROBOT_PORT));

		return socket;
	}

	public boolean inConnectionAttemptTimeout() {
		return lastConnectionAttempt + 3000 > System.currentTimeMillis();
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
