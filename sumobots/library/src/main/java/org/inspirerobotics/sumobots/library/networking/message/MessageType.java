package org.inspirerobotics.sumobots.library.networking.message;

public enum MessageType {
	
	PING("PING", "Pings the Client or Server", true), 
	PONG("PONG", "Return form of a Ping message", true),
	LIB_VERSION("LIB_VERSION", "Used to check the library version", true),
	MATCH_STATE_UPDATE("MATCH_STATE_UPDATE", "Used to update the state of a match", false),
	STREAM_TERMINATED("STREAM_TERMINATED", "Used to terminate the stream", true),
	SET_NAME("SET_NAME", "Sets the name of the socket connected", true),
	UPDATE_NTWK_TABLE("UPDATE_NTWK_TABLE", "Updates the Network Table", true),
	UNKNOWN("UNKNOWN", "A Malformed message", true);

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

	public static MessageType fromString(String message){
		for (MessageType m : MessageType.values()) {
			if(m.getName().equals(message)) {
				return m;
			}
		}
		
		return MessageType.UNKNOWN;
	}

	@Override
	public String toString() {
		return name + " " + desc;
	}

	public static boolean isInternalType(MessageType messageType) {
		return messageType.isInternal;
	}
	
}
