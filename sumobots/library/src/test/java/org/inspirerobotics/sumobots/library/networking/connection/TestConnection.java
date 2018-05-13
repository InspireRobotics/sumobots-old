package org.inspirerobotics.sumobots.library.networking.connection;

import org.inspirerobotics.sumobots.library.networking.TestSocketStream;
import org.inspirerobotics.sumobots.library.networking.message.Message;

public class TestConnection extends Connection {

	private Message lastSentMessage;

	public TestConnection(ConnectionListener cl) {
		super(new TestSocketStream(), cl);
	}

	public void handleTestMessage(Message m) {
		this.onMessageReceived(m);
	}

	@Override
	public void sendMessage(Message messeage) {
		lastSentMessage = messeage;
	}

	@Override
	public void writeRaw(String string) {

	}

	public Message getLastSentMessage() {
		return lastSentMessage;
	}
}
