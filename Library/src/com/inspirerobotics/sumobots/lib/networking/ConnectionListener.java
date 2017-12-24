package com.inspirerobotics.sumobots.lib.networking;

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
