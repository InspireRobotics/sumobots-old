package org.inspirerobotics.sumobots.library.networking.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.networking.SocketStream;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

public class Connection {
	
	/**
	 * The actual socket connection
	 */
	private Socket socket;
	
	/**
	 * The streams for the Sockets
	 */
	private SocketStream stream;
	
	/**
	 * The connection listener attached to this connection
	 */
	private final ConnectionListener listener;
	
	/**
	 * The logger
	 */
	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);
	
	/**
	 * If the socket has been closed
	 */
	private boolean closed = false;
	
	/**
	 * The time since the last ping was sent
	 */
	private long lastPingTime = 0;
	
	/*
	 * The time since the network table updates were sent
	 */
	private long lastNetworkTime = 0;
	
	/**
	 * the current ping
	 */
	private long currentPing;
	
	/**
	 * the name of the opponent connection
	 */
	private String connectionName = "";
	
	/*
	 * The network table
	 */
	private NetworkTable table = new NetworkTable();
	
	/*
	 * The binded network table
	 */
	private NetworkTable bindedTable;
	
	/**
	 * Creates a connection with the Socket and Listener provided
	 * 
	 * @param socket the socket to handle
	 * @param listener the listener to handle incoming messages
	 */
	public Connection(Socket socket, ConnectionListener listener) {
		this.socket = socket;
		this.listener = listener;
		
		try {
			this.socket.setSoTimeout(Resources.SOCKET_TIMEOUT);
			this.stream = new SocketStream(socket);
		}catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to create connection from socket", e);
		}catch(NullPointerException e) {
			
		}
	}
	
	/**
	 * Updates the socket and calls {@link ConnectionListener#receivedMessage(Message, Connection)} if needed
	 */
	public void update() {
		if(closed){
			logger.warning("WARNING! Closed Socket is remaining open!");
			return;
		}
		
		if(System.currentTimeMillis() - lastPingTime > 1000){
			lastPingTime = System.currentTimeMillis();
			sendMessage(ArchetypalMessages.ping());
		}
		
		if(System.currentTimeMillis() - lastNetworkTime > 250) {
			if(bindedTable != null)
				bindedTable.sendUpdates(this);
			
			lastNetworkTime = System.currentTimeMillis();
		}
		
		if(stream.isClosed()){
			try {
				close();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to close a socket", e);
			}
			return;
		}
			
		
		stream.update();
		
		while(hasNextMessage()){
			String nextMessage = getNextMessage();
			Message message = Message.fromString(nextMessage);
			MessageType messageType = message.getType();

			if(!MessageType.isInternalType(messageType)){
				listener.receivedMessage(message, this);
			}else{
				handleInternalTypes(message, messageType);
			}
		}
	}
	
	private void handleInternalTypes(Message message, MessageType messageType){
		if(messageType == MessageType.LIB_VERSION){
			String libraryVersion = (String) message.getData("version");
			boolean isResponse = Boolean.valueOf((String) message.getData("is_response"));
	
			if (libraryVersion.equals(Resources.LIBRARY_VERSION)) {
				String logMessage = String.format("Library Version(%s) matches for %s",
						Resources.LIBRARY_VERSION, socket.getInetAddress().toString());
				
				logger.fine(logMessage);
				if (!isResponse) {
					sendMessage(ArchetypalMessages.libraryVersion(true));
				}
			} else {
				String errorMessage = String.format("Library versions don't match! This Version: %s, Connection Version: %s",
						Resources.LIBRARY_VERSION, libraryVersion);

				logger.severe(errorMessage);

				if (!isResponse) {
					sendMessage(ArchetypalMessages.libraryVersion(true));
				}
				
				try {
					this.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(messageType == MessageType.PING){
			sendMessage(ArchetypalMessages.pong());
		}else if(messageType == MessageType.PONG){
			currentPing = System.currentTimeMillis() - lastPingTime;
			
			if(currentPing > 25)
				logger.info("High Ping on connection, " + connectionName);
		}else if(messageType == MessageType.STREAM_TERMINATED) {
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(messageType == MessageType.SET_NAME) {
			connectionName = (String) message.getData("name");
		}else if(messageType == MessageType.UPDATE_NTWK_TABLE) {
			table.updateFrom(message);
		}
	}
	
	/**
	 * Sends a message over the socket. 
	 * This function automatically appends the EOT char.
	 * @param string
	 */
	public void writeRaw(String string){
		stream.write(string + Resources.EOT);
	}
	
	/**
	 * Sends a message over the socket with the data attached
	 * @param m
	 */
	public void sendMessage(Message m){
		writeRaw(m.toJSONString());

	}
	
	private String getNextMessage(){
		return stream.getNextMessage();
	}
	
	private boolean hasNextMessage(){
		return stream.hasNextMessage();
	}
	
	@Override
	protected void finalize() throws Throwable {
		stream.close();
		socket.close();
	}

	/**
	 * Peacefully ends the stream. Used when nothing went wrong but we need to end the connection
	 */
	public void endConnection() {
		sendMessage(ArchetypalMessages.terminatedConnection());
		
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the socket and streams
	 * @throws IOException 
	 */
	public void close() throws IOException{
		stream.close();
		socket.close();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Connection other = (Connection) obj;
		if (connectionName == null) {
			if (other.connectionName != null)
				return false;
		} else if (!connectionName.equals(other.connectionName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getConnectionName() == null ? this.getConnectionName():"Unnamed Connection";
	}
	
	/**
	 * Creates a connection with the Socket and Listener provided
	 * 
	 * @param socket the socket to handle
	 * @param listener the listener to handle incoming messages
	 */
	public static Connection fromSocket(Socket s, ConnectionListener l){
		return new Connection(s, l);
	}
	
	public void setBindedTable(NetworkTable bindedTable) {
		this.bindedTable = bindedTable;
	}
	
	public NetworkTable getTable() {
		return table;
	}

	public Socket getSocket() {
		return socket;
	}
	
	public boolean isClosed() {
		return closed || stream.isClosed();
	}
	
	public long getCurrentPing() {
		return currentPing;
	}
	
	public String getConnectionName() {
		return connectionName;
	}
	
}
