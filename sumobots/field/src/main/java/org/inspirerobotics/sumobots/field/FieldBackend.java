package org.inspirerobotics.sumobots.field;

import org.inspirerobotics.sumobots.field.driverstation.DriverStationServer;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.logging.Logger;

public class FieldBackend extends Thread {

	private DriverStationServer server;

	private ThreadChannel channel;

	private final Logger log = InternalLog.getLogger();

	private TimePeriod timePeriod;

	private boolean running = true;

	private NetworkTable internalNetworkTable = new NetworkTable();

	private long loopStartTime = System.currentTimeMillis();
	private long loopTime = 0;
	private int loopCount = 0;

	public FieldBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
	}

	@Override
	public void run() {
		log.fine("Initializating field");
		init();
		log.info("Backend initialization has been completed. Starting main loop");

		while (running) {
			server.update();

			sendConnectionsToFrontend();
			updateLoopTime();
			updateInternalTable(loopTime);

			channel.add(new InterThreadMessage("update_internal_table", this.internalNetworkTable.clone()));

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

		channel.add(m);
	}

	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		log.fine("Recieved Message from Frontend: " + name);

		if (name.endsWith("_match")) {
			onMatchMessageReceived(name.split("_match")[0]);
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

	private void onMatchMessageReceived(String messageName) {
		switch (messageName) {
			case "start":
				startMatch();
				break;
			case "end":
				endMatch();
				break;
			case "init":
				initMatch();
				break;
			case "e-stop":
				eStop();
				break;
			default:
				log.warning("Unknown Match Update Recieved on Backend: " + messageName);
				break;
		}

	}

	private void eStop() {
		updateTimePeriod(TimePeriod.ESTOPPED);
	}

	private void initMatch() {
		if (timePeriod != TimePeriod.DISABLED) {
			log.warning("Match cannot be initialized from a non-disabled state!");
			return;
		}

		updateTimePeriod(TimePeriod.INIT);
	}

	private void endMatch() {
		if (timePeriod == TimePeriod.ESTOPPED) {
			log.warning("Match cannot be ended from e-stop!");
			return;
		}

		updateTimePeriod(TimePeriod.DISABLED);
	}

	private void startMatch() {
		if (timePeriod != TimePeriod.INIT) {
			log.warning("Match cannot be ended from a non-initialized state!");
			return;
		}

		updateTimePeriod(TimePeriod.GAME);
	}

	private void updateTimePeriod(TimePeriod timePeriod) {
		log.info("Entering new period: " + timePeriod);
		this.timePeriod = timePeriod;

		InterThreadMessage m = new InterThreadMessage("time_period_update", timePeriod);
		channel.add(m);

		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(timePeriod));
	}

	private void init() {
		server = new DriverStationServer();

		endMatch();
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

}
