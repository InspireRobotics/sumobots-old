package org.inspirerobotics.sumobots.library.networking.message;

import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;

/**
 * This class helps create messages that will often follow the same pattern.
 * For instance all "Library Version" messages are very similar. With this class all
 * one has to do is call the {@link #libraryVersion(boolean)} function and get a simple message
 * 
 * @author Noah
 */
public class ArchetypalMessages {
	
	/**
	 * Creates a "Library Version" message
	 *  
	 * @param isResponse if this response is a response to the server
	 * @return the message created
	 */
	public static final Message libraryVersion(boolean isResponse){
		Message m = new Message(MessageType.LIB_VERSION);
		m.addData("version", Resources.LIBRARY_VERSION);
		m.addData("is_response", ""+isResponse);
		return m;
	}
	
	/**
	 * Creates a Pong message
	 */
	public static final Message pong() {
		Message m = new Message(MessageType.PONG);
		return m;
	}

	/**
	 * Creates a ping message
	 */
	public static final Message ping() {
		Message m = new Message(MessageType.PING);
		return m;
	}
	
	/**
	 * Creates a message for changing the {@link org.inspirerobotics.sumobots.library.TimePeriod}
	 * @param name the name of the new time period.
	 */
	public static final Message enterNewMatchPeriod(TimePeriod t){
		Message m = new Message(MessageType.MATCH_STATE_UPDATE);
		m.addData("new_period", t.getName());
		return m;
	}

	public static Message terminatedConnection() {
		Message m = new Message(MessageType.STREAM_TERMINATED);
		return m;
	}

	public static Message setName(String name) {
		Message m = new Message(MessageType.SET_NAME);
		m.addData("name", name);
		return m;
	}
	
}