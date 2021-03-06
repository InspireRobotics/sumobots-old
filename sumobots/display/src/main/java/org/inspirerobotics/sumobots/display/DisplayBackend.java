package org.inspirerobotics.sumobots.display;

import org.inspirerobotics.sumobots.display.config.Settings;
import org.inspirerobotics.sumobots.display.field.Field;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;

import java.util.logging.Logger;

public class DisplayBackend extends Thread {

	private static final String threadName = "Display Backend";
	private final Logger logger = InternalLog.getLogger();
	private final Settings settings;
	private final ThreadChannel threadChannel;
	private final Field field;

	private boolean running;

	public DisplayBackend(ThreadChannel threadChannel, Settings settings) {
		this.threadChannel = threadChannel;
		this.settings = settings;
		this.setName(threadName);
		this.field = new Field(this);
	}

	@Override
	public void run() {
		logger.info("Starting backend thread");
		running = true;

		connectToField();

		while (running) {
			update();
		}
	}

	private void connectToField() {
		while (running) {
			handleIncomingMessages();

			if (field.attemptConnection(settings.fieldIP())) {
				logger.info("Connected to the field... Starting main loop");
				onFieldConnectionAccepted();
				return;
			}

			logger.info("Failed to connect to the field! Sleeping for 3 seconds...");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				logger.info("Display shutdown while sleeping..");
				shutdown();
				return;
			}
		}
	}

	private void onFieldConnectionAccepted() {
		InterThreadMessage m = new InterThreadMessage("conn_status", true);
		sendMessageToFrontend(m);
	}

	private void onFieldConnectionLost() {
		logger.info("Lost field connection");
		InterThreadMessage m = new InterThreadMessage("conn_status", false);
		sendMessageToFrontend(m);
	}

	public void sendMessageToFrontend(InterThreadMessage m) {
		threadChannel.add(m);
	}

	private void shutdown() {
		if (!running)
			logger.severe("The backend thread was shutdown while it wasn't running");

		field.shutdown();
		logger.info("Shutting down backend...");
		running = false;
		return;
	}

	private void update() {
		handleIncomingMessages();

		if (field.connected() == false) {
			onFieldConnectionLost();
			connectToField();
		}

		field.update();
	}

	private void handleIncomingMessages() {
		InterThreadMessage message = null;

		while ((message = threadChannel.poll()) != null && running) {
			logger.fine("Received message from frontend: " + formatMessageToString(message));
			onMessageReceived(message);
		}
	}

	private String formatMessageToString(InterThreadMessage message) {
		return message.getName() + (message.getData() == null ? "" : (": " + message.getData()));
	}

	private void onMessageReceived(InterThreadMessage message) {
		if (message.getName().equals("shutdown")) {
			shutdown();
		} else if (message.getName().equals("scenes")) {
			sendScenes(message);
		}
	}

	private void sendScenes(InterThreadMessage message) {
		String[] scenes = (String[]) message.getData();

		Message m = new Message(MessageType.SCENE_UPDATE);
		m.addData("amount", "" + scenes.length);

		for (int i = 0; i < scenes.length; i++) {
			m.addData("scene" + i, scenes[i]);
		}

		field.send(m);
	}
}
