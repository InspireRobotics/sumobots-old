package org.inspirerobotics.sumobots.library.networking.connection;

import org.inspirerobotics.sumobots.library.networking.message.Message;

/**
 * The listener for a connection
 * @author Noah
 */
public interface ConnectionListener {
	
	/**
	 * Called when a message is recieved from a connection
	 * 
	 * @param message the message recieved
	 * @param connection the connection the message was recieved from
	 */
	public void receivedMessage(Message message, Connection connection);
	
}
