package org.inspirerobotics.sumobots.library.networking;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.gui.Alerts;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private final Logger log = InternalLog.getLogger();

	private final ServerSocket serverSocket;

	private final List<Connection> connections = new ArrayList<Connection>();

	private final ConnectionListener cl;

	private final String name;

	public Server(ConnectionListener cl, String name, int port) {
		serverSocket = createServerSocket(port);
		log.info("Started Server on " + serverSocket.getInetAddress() + " on port " + serverSocket.getLocalPort());
		this.cl = cl;
		this.name = name;
	}

	public void sendAll(Message m) {
		for (Connection connection : connections) {
			connection.sendMessage(m);
		}
	}

	public void update() {
		acceptConnections();

		ArrayList<Connection> connectionsToClose = new ArrayList<Connection>();

		for (Connection conn : connections) {
			conn.update();

			if (conn.isClosed())
				connectionsToClose.add(conn);
		}

		for (Connection c : connectionsToClose) {
			connections.remove(c);
		}
	}

	private void acceptConnections() {
		try {
			Socket s;
			while ((s = serverSocket.accept()) != null) {
				Connection c = Connection.fromSocket(s, cl);

				// immediately send a request to verify the library version
				connections.add(c);
				log.info("Found Connection: " + s.getInetAddress() + "\t" + s.getLocalAddress());
				onConnectionCreated(c);
			}
		} catch (SocketTimeoutException e) {
			// If there is a timeout, then no connections are waiting to join...
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to Accept Connection:" + e.getMessage());
		}

	}

	protected void onConnectionCreated(Connection c) {
		c.sendMessage(ArchetypalMessages.libraryVersion(false));
		c.sendMessage(ArchetypalMessages.setName(name));
	}

	private ServerSocket createServerSocket(int port) {
		try {
			ServerSocket socket = new ServerSocket(port, 20, InetAddress.getByName("0.0.0.0"));
			socket.setSoTimeout(Resources.SOCKET_TIMEOUT);
			return socket;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to create ServerSocket", e);
			Alerts.exceptionAlert(Alerts.ShutdownLevel.ALL, e);
		}
		return null;
	}

	public void closeServer() {
		try {
			for (Connection socket : connections) {
				socket.endConnection();
			}
			serverSocket.close();
		} catch (IOException e) {
			log.warning("Failed to stop the Server!");
		}
	}

	public void removeAll() {
		for (Connection socket : connections) {
			socket.endConnection();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		for (Connection socket : connections) {
			socket.close();
		}

		serverSocket.close();
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public List<Connection> getConnections() {
		return connections;
	}

}
