package org.inspirerobotics.sumobots.library.networking.message;

import org.inspirerobotics.sumobots.library.InternalLog;

import java.util.logging.Logger;

public enum MessageType {

	PING("PING", "Pings the Client or Server", true), 
	PONG("PONG", "Return form of a Ping message", true),
	LIB_VERSION("LIB_VERSION", "Used to check the library version", true),
	MATCH_STATE_UPDATE("MATCH_STATE_UPDATE", "Used to update the state of a match", false),
	STREAM_TERMINATED("STREAM_TERMINATED", "Used to terminate the stream", true),
	SET_NAME("SET_NAME", "Sets the name of the socket connected", true),
	UPDATE_NTWK_TABLE("UPDATE_NTWK_TABLE", "Updates the Network Table", true),
	JOYSTICK_UPDATE("JOY_UPDATE", "Updates a joystick value", false),
	SCENE_UPDATE("SCENE_UPDATE", "Updates the current scene", false),
	MATCH_DATA("MATCH_DATA", "Match Data", false),
	UNKNOWN("UNKNOWN", "A Malformed message", true);

	private static final Logger logger = InternalLog.getLogger();

	private final String name;

	private final String desc;

	private final boolean isInternal; 
	
	MessageType(String name, String desc, boolean isInternal){
		this.name = name;
		this.desc = desc;
		this.isInternal = isInternal;
	}

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return name;
	}

	public static MessageType fromString(String message) throws UnknownMessageTypeException{
		for (MessageType m : MessageType.values()) {
			if(m.getName().equals(message)) {
				return m;
			}
		}

		throw new UnknownMessageTypeException("Unknown Message Type: " + message);
	}

	@Override
	public String toString() {
		return name + " " + desc;
	}

	public static boolean isInternalType(MessageType messageType) {
		return messageType.isInternal;
	}
	
}
