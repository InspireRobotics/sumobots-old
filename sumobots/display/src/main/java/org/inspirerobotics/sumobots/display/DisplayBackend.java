package org.inspirerobotics.sumobots.display;

import org.inspirerobotics.sumobots.display.field.Field;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import java.util.logging.Logger;

public class DisplayBackend extends Thread {

	private static final String threadName = "Display Backend";
	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel;
	private final Field field;

	private boolean running;

	public DisplayBackend(ThreadChannel threadChannel) {
		this.threadChannel = threadChannel;
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

			if (field.attemptConnection("localhost")) {
				logger.info("Connected to the field... Starting main loop");
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
		}
	}
}
