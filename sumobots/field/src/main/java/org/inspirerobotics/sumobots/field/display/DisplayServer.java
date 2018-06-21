package org.inspirerobotics.sumobots.field.display;

import org.inspirerobotics.sumobots.field.FieldBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class DisplayServer extends Server {

	private final Logger log = InternalLog.getLogger();
	private final FieldBackend fieldBackend;
	private Connection connection;

	public DisplayServer(FieldBackend fieldBackend) {
		super(new DisplayListener(fieldBackend), "display_server", Resources.DISPLAY_PORT);
		this.fieldBackend = fieldBackend;
	}

	@Override
	protected void onConnectionCreated(Connection c) {
		if (connection != null) {
			log.severe("Two displays connected at once... Shutting down second connection!");
			c.endConnection();
			return;
		}

		super.onConnectionCreated(c);

		connection = c;
		sendConnectionStatusToFrontend();
	}

	public void update() {
		super.update();

		if (connection == null)
			return;

		if (connection.isClosed()) {
			log.warning("Lost display connection....");
			connection = null;
			sendConnectionStatusToFrontend();
		}
	}

	private void sendConnectionStatusToFrontend() {
		if (connection != null) {
			InterThreadMessage m = new InterThreadMessage("display_connection", true);
			fieldBackend.sendMessageToFrontend(m);
		} else {
			InterThreadMessage m = new InterThreadMessage("display_connection", false);
			fieldBackend.sendMessageToFrontend(m);
		}
	}

	public void selectScene(String data) {
		if (connection != null) {
			Message m = new Message(MessageType.SCENE_UPDATE);
			m.addData("scene", data);
			connection.sendMessage(m);
		}
	}

	public void sendMatchData(List<Connection> connections) {
		log.finer("Sending Match data");
		Message m = new Message(MessageType.MATCH_DATA);

		m.addData("num_of_teams", "" + connections.size());

		for (int i = 0; i < connections.size(); i++) {
			if (isConnectionDisabled(connections.get(i))) {
				m.addData("team" + i, connections.get(i).getConnectionName() + "\teliminated");
			} else {
				m.addData("team" + i, connections.get(i).getConnectionName());
			}
		}

		if (connection != null)
			connection.sendMessage(m);
	}

	private boolean isConnectionDisabled(Connection c) {
		TimePeriod t = TimePeriod.fromString(c.getTable().get("Time Period"));

		if (t == null)
			return false;

		if (t == TimePeriod.DISABLED) {
			return true;
		}
		return false;
	}
}

class DisplayListener implements ConnectionListener {

	private final Logger log = InternalLog.getLogger();
	private final FieldBackend fieldBackend;

	public DisplayListener(FieldBackend fieldBackend) {
		this.fieldBackend = fieldBackend;
	}

	@Override
	public void receivedMessage(Message message, Connection connection) {
		if (message.getType() == MessageType.SCENE_UPDATE) {
			updateScenes(message);
		}
	}

	private void updateScenes(Message m) {
		log.info("Received scenes");
		int amountOfScenes = Integer.parseInt((String) m.getData("amount"));

		String[] scenes = new String[amountOfScenes];

		for (int i = 0; i < scenes.length; i++) {
			scenes[i] = (String) m.getData("scene" + i);
		}

		log.info("Received scene list: " + Arrays.toString(scenes));

		InterThreadMessage interThreadMessage = new InterThreadMessage("scenes", scenes);
		fieldBackend.sendMessageToFrontend(interThreadMessage);
	}
}