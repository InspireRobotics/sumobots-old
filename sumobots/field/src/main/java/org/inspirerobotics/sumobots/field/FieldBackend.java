package org.inspirerobotics.sumobots.field;

import java.util.logging.Logger;

import org.inspirerobotics.sumobots.field.util.InternalLog;

import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

/**
 * This handles all of the backend stuff (basically everything but the GUI).
 * Runs on its own thread
 * 
 * @author Noah
 */
public class FieldBackend extends Thread {

	/**
	 * The Server that the driver stations connect to
	 */
	private Server server;

	/**
	 * The channel for connection to the FrontendThread
	 */
	private ThreadChannel channel;

	/**
	 * The Log
	 */
	private final Logger log = InternalLog.getLogger();

	/**
	 * The current time period
	 */
	private TimePeriod timePeriod;

	/**
	 * If the backend should be running
	 */
	private boolean running = true;
	
	/**
	 * The internal network table
	 */
	private NetworkTable internalNetworkTable = new NetworkTable();
	
	public FieldBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
	}

	/**
	 * The function that is called after the thread is started
	 */
	@Override
	public void run() {
		log.fine("Initializating backend");
		init();
		log.info("Backend initialization has been completed. Starting main loop");

		while (running) {
			// Update the Server
			server.update();

			// update the frontend about the current connections
			sendConnectionsToFrontend();
			
			//Update the frontend network table
			updateInternalTable();
			channel.add(new InterThreadMessage("update_internal_table", this.internalNetworkTable.clone()));

			// While there are messages from the frontend, handle them
			InterThreadMessage m = null;
			while ((m = channel.poll()) != null) {
				onFrontendMessageReceived(m);

				// This needs to be here to prevent other messages from being
				// proccessed after the app is supposed to be closing
				if (!running)
					break;
			}
		}

		server.closeServer();

		log.info("Backend Thread Shutdown Complete!");
	}

	/**
	 * Updates the internal table on the Frontend
	 */
	private void updateInternalTable() {
		internalNetworkTable.put("IP", "" + server.getServerSocket().getLocalSocketAddress());
		internalNetworkTable.put("Port", ""+server.getServerSocket().getLocalPort());
	}

	/**
	 * Sends the frontend a message with the current connections to the server
	 */
	private void sendConnectionsToFrontend() {
		InterThreadMessage m = new InterThreadMessage("conn_update", server.getConnections());

		channel.add(m);
	}

	/**
	 * Handles an incoming message from the frontend thread
	 * 
	 * @param m
	 *            the message recieved
	 */
	private void onFrontendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		log.fine("Recieved Message from Frontend: " + name);

		// Figure out what type of message it is
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
		default: // If it reaches this we don't know what it is so print a
					// warning to the screen
			log.warning("Unknown Message Recieved on Backend: " + name);
			break;
		}
	}
	
	/**
	 * Disables a specific DS
	 */
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

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.ESTOPPED);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.ESTOPPED));
	}

	private void initMatch() {
		// If we are not disabled, we cannot init
		if (timePeriod != TimePeriod.DISABLED) {
			log.warning("Match cannot be initialized from a non-disabled state!");
			return;
		}

		log.info("Initializing the Match!");
		timePeriod = TimePeriod.INIT;

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.INIT);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.INIT));
	}

	private void endMatch() {
		// If we are not disabled, we cannot init
		if (timePeriod == TimePeriod.ESTOPPED) {
			log.warning("Match cannot be ended from e-stop!");
			return;
		}

		log.info("Ending the Match!");
		timePeriod = TimePeriod.DISABLED;

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.DISABLED);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void disableMatch() {
		log.fine("Disabling the match!");
		timePeriod = TimePeriod.DISABLED;

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.DISABLED);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void startMatch() {
		// If we are not in init, we cannot start the game
		if (timePeriod != TimePeriod.INIT) {
			log.warning("Match cannot be ended from a non-initialized state!");
			return;
		}

		log.info("Starting the Match!");
		timePeriod = TimePeriod.GAME;

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update", TimePeriod.GAME);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.GAME));
	}

	/**
	 * Inits the Backend (Server, Logging, etc.)
	 */
	private void init() {
		// Create the server
		server = new Server(new ConnectionListener() {

			@Override
			public void recievedMessage(Message message, Connection connection) {
				log.info("Recieved Message: " + message);
			}

		}, "Field");

		// Set to disabled
		disableMatch();
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

}
