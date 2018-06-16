package org.inspirerobotics.sumobots.field;

import org.inspirerobotics.sumobots.field.display.DisplayServer;
import org.inspirerobotics.sumobots.field.driverstation.DriverStationServer;
import org.inspirerobotics.sumobots.field.util.MatchController;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.logging.Logger;

public class FieldBackend extends Thread {

	private final MatchController matchController;

	private DriverStationServer server;

	private ThreadChannel channel;

	private final Logger log = InternalLog.getLogger();

	private final DisplayServer displayServer;

	private boolean running = true;

	private NetworkTable internalNetworkTable = new NetworkTable();

	private long loopStartTime = System.currentTimeMillis();
	private long loopTime = 0;
	private int loopCount = 0;

	public FieldBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;

		matchController = new MatchController(this);
		displayServer = new DisplayServer(this);
	}

	@Override
	public void run() {
		log.fine("Initializating field");
		init();
		log.info("Backend initialization has been completed. Starting main loop");

		while (running) {
			server.update();
			displayServer.update();

			sendConnectionsToFrontend();
			updateLoopTime();
			updateInternalTable(loopTime);

			sendMessageToFrontend(new InterThreadMessage("update_internal_table", this.internalNetworkTable.clone()));

			pollFrontendThreadMessages();
		}

		server.closeServer();

		log.info("Backend Thread Shutdown Complete!");
	}

	private void updateLoopTime() {
		loopCount++;

		if (System.currentTimeMillis() - loopStartTime > 1000) {
			loopTime = (System.currentTimeMillis() - loopStartTime) / loopCount;
			loopStartTime = System.currentTimeMillis();
			loopCount = 0;

			if (loopTime > 15) {
				log.warning("High Backend Loop Time: " + loopTime);
			}
		}
	}

	private void pollFrontendThreadMessages() {
		InterThreadMessage m;
		while ((m = channel.poll()) != null) {
			onFrontendMessageReceived(m);

			if (!running)
				break;
		}
	}

	private void updateInternalTable(long loopTime) {
		server.updateNetworkingTable(internalNetworkTable);
		internalNetworkTable.put("Backend Loop Time", loopTime + "ms");
	}

	private void sendConnectionsToFrontend() {
		InterThreadMessage m = new InterThreadMessage("conn_update", server.getConnections());

		sendMessageToFrontend(m);
	}

	public void sendMessageToFrontend(InterThreadMessage threadMessage) {
		channel.add(threadMessage);
	}

	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		log.fine("Recieved Message from Frontend: " + name);

		if (name.equals("period_request")) {
			matchController.attemptStateChange((TimePeriod) m.getData());
			return;
		}

		switch (name) {
			case "exit_app":
				log.info("Exiting Backend Thread!");
				server.closeServer();
				running = false;
				break;
			case "close_all":
				server.removeAll();
				break;
			case "disable_ds":
				server.disableDS((String) m.getData());
				break;
			default:
				log.warning("Unknown Message Recieved on Backend: " + name);
				break;
		}
	}

	private void init() {
		server = new DriverStationServer();

		matchController.attemptStateChange(TimePeriod.DISABLED);
	}

	public DriverStationServer getServer() {
		return server;
	}
}
