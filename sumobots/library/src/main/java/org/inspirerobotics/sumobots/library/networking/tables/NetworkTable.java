package org.inspirerobotics.sumobots.library.networking.tables;

import java.util.HashMap;
import java.util.Set;

import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;

@SuppressWarnings("serial")
public class NetworkTable extends HashMap<String, String> {

	public void sendUpdates(Connection c) {
		if (!this.isEmpty())
			c.sendMessage(toMessage());
	}

	public Message toMessage() {
		Message m = new Message(MessageType.UPDATE_NTWK_TABLE);

		Set<Entry<String, String>> entries = this.entrySet();

		for (Entry<String, String> entry : entries) {
			m.addData(entry.getKey(), entry.getValue());
		}

		this.clear();

		return m;
	}

	public void updateFrom(Message message) {
		Set<Entry<String, String>> entries = message.getDataSet().entrySet();

		for (Entry<String, String> entry : entries) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

}
