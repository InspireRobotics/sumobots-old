package com.inspirerobotics.sumobots.lib.networking.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.networking.SocketStream;
import com.inspirerobotics.sumobots.lib.networking.message.ArchetypalMessages;
import com.inspirerobotics.sumobots.lib.networking.message.Message;
import com.inspirerobotics.sumobots.lib.networking.message.MessageType;

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
	
	/**
	 * the current ping
	 */
	private long currentPing;
	
	/**
	 * the name of the opponent connection
	 */
	private String connectionName = "";
	
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
		}
	}
	
	/**
	 * Updates the socket and calls {@link ConnectionListener#recievedMessage(Message, Connection)} if needed
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
		
		if(stream.isClosed()){
			try {
				close();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to close a socket", e);
			}
			return;
		}
			
		
		stream.update();
		
		//While there is message, handle it
		while(hasNextMessage()){
			String nextMessage = getNextMessage();
			Message message = Message.fromString(nextMessage);
			MessageType messageType = message.getType();
			
			//If the message isn't internal send it to the listener
			//else handle it
			if(!MessageType.isInternalType(messageType)){
				listener.recievedMessage(message, this);
			}else{
				handleInternalTypes(message, messageType);
			}
		}
	}
	
	private void handleInternalTypes(Message message, MessageType messageType){
		if(messageType == MessageType.LIB_VERSION){
			String libraryVersion = (String) message.getData("version");
			boolean isResponse = Boolean.valueOf((String) message.getData("is_response"));
	
			if(libraryVersion.equals(Resources.LIBRARY_VERSION)){
				logger.fine("Library Version matches for " + socket.getInetAddress());
				if(isResponse){
				}else{
					sendMessage(ArchetypalMessages.libraryVersion(true));
				}
			}
		}else if(messageType == MessageType.PING){
			sendMessage(ArchetypalMessages.pong());
		}else if(messageType == MessageType.PONG){
			currentPing = System.currentTimeMillis() - lastPingTime;
			logger.fine("Ping: " + currentPing);
		}else if(messageType == MessageType.STREAM_TERMINATED) {
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(messageType == MessageType.SET_NAME) {
			connectionName = (String) message.getData("name");
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
		sendMessage(m.getType(), m.getFormatedData());
	}
	
	/**
	 * Sends a message over the socket with the data attached
	 * @param m
	 */
	public void sendMessage(MessageType m, String data){
		writeRaw(m.getName() + Resources.EOB + data);
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
	
	/**
	 * Creates a connection with the Socket and Listener provided
	 * 
	 * @param socket the socket to handle
	 * @param listener the listener to handle incoming messages
	 */
	public static Connection fromSocket(Socket s, ConnectionListener l){
		return new Connection(s, l);
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
