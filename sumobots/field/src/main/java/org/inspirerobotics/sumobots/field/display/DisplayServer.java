package org.inspirerobotics.sumobots.field.display;

import org.inspirerobotics.sumobots.field.FieldBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.util.logging.Logger;

public class DisplayServer implements ConnectionListener {

	private final Logger log = InternalLog.getLogger();
	private final FieldBackend fieldBackend;
	private final Server server;

	public DisplayServer(FieldBackend fieldBackend) {
		this.fieldBackend = fieldBackend;

		this.server = new Server(this, "", Resources.DISPLAY_PORT);
	}

	@Override
	public void receivedMessage(Message message, Connection connection) {

	}

	public void update() {
		server.update();
	}
}
