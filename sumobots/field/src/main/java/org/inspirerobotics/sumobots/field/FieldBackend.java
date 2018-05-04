package org.inspirerobotics.sumobots.field;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.logging.Logger;

public class FieldBackend extends Thread {

	private Server server;

	private ThreadChannel channel;

	private final Logger log = InternalLog.getLogger();

	private TimePeriod timePeriod;

	private boolean running = true;

	private NetworkTable internalNetworkTable = new NetworkTable();
	
	public FieldBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
	}

	@Override
	public void run() {
		log.fine("Initializating backend");
		init();
		log.info("Backend initialization has been completed. Starting main loop");

		while (running) {
			server.update();

			sendConnectionsToFrontend();
			
			updateInternalTable();
			channel.add(new InterThreadMessage("update_internal_table", this.internalNetworkTable.clone()));

			InterThreadMessage m = null;
			while ((m = channel.poll()) != null) {
				onFrontendMessageReceived(m);

				if (!running)
					break;
			}
		}

		server.closeServer();

		log.info("Backend Thread Shutdown Complete!");
	}

	private void updateInternalTable() {
		internalNetworkTable.put("IP", "" + server.getServerSocket().getLocalSocketAddress());
		internalNetworkTable.put("Port", ""+server.getServerSocket().getLocalPort());
	}

	private void sendConnectionsToFrontend() {
		InterThreadMessage m = new InterThreadMessage("conn_update", server.getConnections());

		channel.add(m);
	}

	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		log.fine("Recieved Message from Frontend: " + name);

		switch (name) {
		case "start_match":
			startMatch();
			break;
		case "end_match":
			endMatch();
			break;
		case "init_match":
			initMatch();
			break;
		case "exit_app":
			log.info("Exiting Backend Thread!");
			server.closeServer();
			running = false;
			break;
		case "close_all":
			server.removeAll();
			break;
		case "e-stop":
			eStop();
			break;
		case "disable_ds":
			disableDS((String) m.getData());
		default:
			log.warning("Unknown Message Recieved on Backend: " + name);
			break;
		}
	}

	private void disableDS(String name) {
		for (Connection c : server.getConnections()) {
			if(c.getConnectionName().equals(name)) {
				c.sendMessage(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
			}
		}
	}

	private void eStop() {
		log.info("EStopping the Match!");
		timePeriod = TimePeriod.ESTOPPED;

		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.ESTOPPED);
		channel.add(m);

		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.ESTOPPED));
	}

	private void initMatch() {
		if (timePeriod != TimePeriod.DISABLED) {
			log.warning("Match cannot be initialized from a non-disabled state!");
			return;
		}

		log.info("Initializing the Match!");
		timePeriod = TimePeriod.INIT;

		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.INIT);
		channel.add(m);

		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.INIT));
	}

	private void endMatch() {
		if (timePeriod == TimePeriod.ESTOPPED) {
			log.warning("Match cannot be ended from e-stop!");
			return;
		}

		log.info("Ending the Match!");
		timePeriod = TimePeriod.DISABLED;

		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.DISABLED);
		channel.add(m);

		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void disableMatch() {
		log.fine("Disabling the match!");
		timePeriod = TimePeriod.DISABLED;

		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.DISABLED);
		channel.add(m);

		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void startMatch() {
		if (timePeriod != TimePeriod.INIT) {
			log.warning("Match cannot be ended from a non-initialized state!");
			return;
		}

		log.info("Starting the Match!");
		timePeriod = TimePeriod.GAME;

		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.GAME);
		channel.add(m);

		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.GAME));
	}

	private void init() {
		server = new Server((message, connection) -> log.info("Recieved Message: " + message), "Field");

		disableMatch();
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

}
