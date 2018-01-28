package com.inspirerobotics.sumobots.lib.networking.message;

import com.inspirerobotics.sumobots.lib.Resources;

/**
 * This enum is used for holding the message type
 * 
 * <b>Types:</b>
 * <p><b>Ping</b>: Pings the Client or Server
 * <p><b>Pong</b>: Return form of a Ping message
 * <p><b>Lib. Version</b>: Used to check the library version
 * <p><b>UNKNOWN</b>: A Malformed message
 * @author Noah
 *
 */
public enum MessageType {
	
	PING("PING", "Pings the Client or Server", true), 
	PONG("PONG", "Return form of a Ping message", true),
	LIB_VERSION("LIB_VERSION", "Used to check the library version", true),
	MATCH_STATE_UPDATE("MATCH_STATE_UPDATE", "Used to update the state of a match", false),
	UNKNOWN("UNKNOWN", "A Malformed message", true);
	
	/**
	 * The Name of the message
	 */
	private final String name;
	
	/**
	 * The Description of the message
	 */
	private final String desc;
	
	/**
	 * If the messages type is internal, as in it should not be used outside of this library
	 */
	private final boolean isInternal; 
	
	MessageType(String name, String desc, boolean isInternal){
		this.name = name;
		this.desc = desc;
		this.isInternal = isInternal;
	}
	

	/**
	 * @return The Description of the message
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * 
	 * @return The Name of the message
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a message type from a string
	 * @param name The name of the message
	 * @return
	 */
	public static MessageType fromString(String message){
		//If the message contains data then get rid of it
		if(message.contains(Resources.EOB))
			message = message.subSequence(0, message.indexOf(Resources.EOB)).toString();
		for (MessageType m : MessageType.values()) {
			if(m.getName().equals(message) && !message.equals(UNKNOWN.getName()))
				return m;
		}
		System.out.println("Found Unknown Message: " + message);
		return UNKNOWN;
	}
	
	/**
	 * @return returns a string with the format "name" then a space then the "desc"
	 */
	@Override
	public String toString() {
		return name + " " + desc;
	}

	/**
	 * @param messageType
	 * @return if the messageType is an internal type
	 */
	public static boolean isInternalType(MessageType messageType) {
		return messageType.isInternal;
	}
	
}
