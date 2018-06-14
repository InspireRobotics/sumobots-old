package org.inspirerobotics.sumobots.display;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import java.util.logging.Logger;

public class DisplayBackend extends Thread {

	private static final String threadName = "Display Backend";
	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel;

	private boolean running;

	public DisplayBackend(ThreadChannel threadChannel) {
		this.threadChannel = threadChannel;
		this.setName(threadName);
	}

	@Override
	public void run() {
		logger.info("Starting backend thread");
		running = true;

		while (running) {
			update();
		}
	}

	private void shutdown() {
		if (!running)
			logger.severe("The backend thread was shutdown while it was running");

		logger.info("Shutting down backend...");
		running = false;
		return;
	}

	private void update() {
		handleIncomingMessages();
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
