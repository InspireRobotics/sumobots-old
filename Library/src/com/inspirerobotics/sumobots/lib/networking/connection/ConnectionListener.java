package com.inspirerobotics.sumobots.lib.networking.connection;

import com.inspirerobotics.sumobots.lib.networking.message.Message;

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
	public void recievedMessage(Message message, Connection connection);
	
}
