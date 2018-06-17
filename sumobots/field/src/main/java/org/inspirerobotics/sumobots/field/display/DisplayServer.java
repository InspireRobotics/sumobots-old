package org.inspirerobotics.sumobots.field.display;

import org.inspirerobotics.sumobots.field.FieldBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.util.logging.Logger;

public class DisplayServer implements ConnectionListener {

	private final Logger log = InternalLog.getLogger();
	private final FieldBackend fieldBackend;
	private final Server server;
	private Connection connection;

	public DisplayServer(FieldBackend fieldBackend) {
		this.fieldBackend = fieldBackend;

		this.server = new Server(this, "", Resources.DISPLAY_PORT);
	}

	@Override
	public void receivedMessage(Message message, Connection connection) {

	}

	public void update() {
		server.update();

		if (connection == null) {
			if (!server.getConnections().isEmpty()) {
				InterThreadMessage m = new InterThreadMessage("display_connection", true);
				fieldBackend.sendMessageToFrontend(m);
				connection = server.getConnections().get(0);
			}
		} else {
			if (server.getConnections().isEmpty()) {
				InterThreadMessage m = new InterThreadMessage("display_connection", false);
				fieldBackend.sendMessageToFrontend(m);
				connection = null;
			}
		}
	}
}
