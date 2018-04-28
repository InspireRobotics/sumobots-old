package org.inspirerobotics.sumobots.library.networking.message;

import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;

public class ArchetypalMessages {

	public static final Message libraryVersion(boolean isResponse){
		Message m = new Message(MessageType.LIB_VERSION);
		m.addData("version", Resources.LIBRARY_VERSION);
		m.addData("is_response", ""+isResponse);
		return m;
	}

	public static final Message pong() {
		Message m = new Message(MessageType.PONG);
		return m;
	}

	public static final Message ping() {
		Message m = new Message(MessageType.PING);
		return m;
	}

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
