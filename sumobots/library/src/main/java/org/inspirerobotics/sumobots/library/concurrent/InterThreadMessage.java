package org.inspirerobotics.sumobots.library.concurrent;

/**
 * Used by the {@link ThreadChannel} to send messages over threads
 * @author Noah
 *
 */
public class InterThreadMessage {
	
	/**
	 * The Name of the message
	 */
	private final String name;
	
	/**
	 * The data in the message
	 */
	private final Object data;
	
	public InterThreadMessage(String name) {
		super();
		this.name = name;
		this.data = null;
	}
	
	public InterThreadMessage(String name, Object data) {
		super();
		this.name = name;
		this.data = data;
	}
	
	/**
	 * Gets data from the message 
	 * @return the data found in the message
	 */
	public Object getData(){
		return data;
	}
	
	public String getName() {
		return name;
	}
	
}
