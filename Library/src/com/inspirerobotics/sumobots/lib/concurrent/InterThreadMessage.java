package com.inspirerobotics.sumobots.lib.concurrent;

import java.util.HashMap;

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
	private final HashMap<String, Object> data = new HashMap<String, Object>();

	public InterThreadMessage(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Adds data to the message
	 * @param key the name of data
	 * @param value the value of the data
	 */
	public void addData(String key, Object value){
		data.put(key, value);
	}
	
	/**
	 * Gets data from the message 
	 * @param key the name of the data
	 * @return the data found in the message
	 */
	public Object getData(String key){
		return data.get(key);
	}
	
	public String getName() {
		return name;
	}
	
}
