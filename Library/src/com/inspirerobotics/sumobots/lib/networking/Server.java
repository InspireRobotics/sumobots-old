package com.inspirerobotics.sumobots.lib.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.networking.connection.Connection;
import com.inspirerobotics.sumobots.lib.networking.connection.ConnectionListener;
import com.inspirerobotics.sumobots.lib.networking.message.ArchetypalMessages;
import com.inspirerobotics.sumobots.lib.networking.message.Message;

/**
 * This class handles the server socket/server stuff.
 * 
 * @author Noah
 *
 */
public class Server {

	/**
	 * The Log
	 */
	private final Logger log = Logger.getLogger(Resources.LOGGER_NAME);

	/**
	 * The server socket that handles incoming requests
	 */
	private final ServerSocket serverSocket;
	/**
	 * The list of the connections currently open
	 */
	private final List<Connection> connections = new ArrayList<Connection>();
	/**
	 * The ConnectionListener for everything connected to the server
	 */
	private final ConnectionListener cl;

	/**
	 * The name to set the connection
	 */
	private final String name;

	/**
	 * Creates a new server
	 * 
	 * @param cl
	 *            The
	 *            {@link com.inspirerobotics.sumobots.lib.networking.connection.ConnectionListener}
	 *            for everything connected to the server
	 */
	public Server(ConnectionListener cl, String name) {
		serverSocket = createServerSocket();
		log.info("Started Server on " + serverSocket.getInetAddress() + " on port " + serverSocket.getLocalPort());
		this.cl = cl;
		this.name = name;
	}

	/**
	 * Sends a message to every connection on the server
	 * 
	 * @param m
	 *            the message to send
	 */
	public void sendAll(Message m) {
		for (Connection connection : connections) {
			connection.sendMessage(m);
		}
	}

	/**
	 * Updates every connection on the server
	 */
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

	/**
	 * Accepts all new connections and immediately sends a library version request
	 * to confirm that both ends are using the same version of the software
	 */
	private void acceptConnections() {
		try {
			Socket s;
			while ((s = serverSocket.accept()) != null) {
				Connection c = Connection.fromSocket(s, cl);

				// imediately send a request to verify the library version
				c.sendMessage(ArchetypalMessages.libraryVersion(false));
				c.sendMessage(ArchetypalMessages.setName(name));
				connections.add(c);
				log.info("Found Connection: " + s.getInetAddress() + "\t" + s.getLocalAddress());
			}
		} catch (SocketTimeoutException e) {
			// If there is a timeout, then no connections are waiting to join...
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to Accept Connection:" + e.getMessage());
		}

	}

	/**
	 * @return a server socket with settings from the{@link Resources} file
	 */
	private ServerSocket createServerSocket() {
		try {
			ServerSocket socket = new ServerSocket(Resources.SERVER_PORT);
			socket.setSoTimeout(Resources.SOCKET_TIMEOUT);
			return socket;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to create ServerSocket", e);
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
