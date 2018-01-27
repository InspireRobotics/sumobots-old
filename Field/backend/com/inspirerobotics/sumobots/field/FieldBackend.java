package com.inspirerobotics.sumobots.field;

import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.util.InternalLog;
import com.inspirerobotics.sumobots.lib.TimePeriod;
import com.inspirerobotics.sumobots.lib.concurrent.InterThreadMessage;
import com.inspirerobotics.sumobots.lib.concurrent.ThreadChannel;
import com.inspirerobotics.sumobots.lib.networking.ArchetypalMessages;
import com.inspirerobotics.sumobots.lib.networking.Connection;
import com.inspirerobotics.sumobots.lib.networking.ConnectionListener;
import com.inspirerobotics.sumobots.lib.networking.Message;
import com.inspirerobotics.sumobots.lib.networking.Server;

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

		server.closeAll();

		log.info("Backend Thread Shutdown Complete!");
	}

	/**
	 * Sends the frontend a message with the current connections to the server
	 */
	private void sendConnectionsToFrontend() {
		InterThreadMessage m = new InterThreadMessage("conn_update");
		m.addData("connections", server.getConnections());

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
			running = false;
			break;
		default: // If it reaches this we don't know what it is so print a
					// warning to the screen
			log.warning("Unknown Message Recieved on Backend: " + name);
			break;
		}
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
		InterThreadMessage m = new InterThreadMessage("time_period_update");
		m.addData("new_period", TimePeriod.INIT);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.INIT));
	}

	private void endMatch() {
		log.info("Ending the Match!");
		timePeriod = TimePeriod.DISABLED;

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update");
		m.addData("new_period", TimePeriod.DISABLED);
		channel.add(m);

		// Lets also tell all of the driver stations to switch the time period
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
	}

	private void disableMatch() {
		log.fine("Disabling the match!");
		timePeriod = TimePeriod.DISABLED;

		// Lets now confirm to the frontend that we are switching time periods
		InterThreadMessage m = new InterThreadMessage("time_period_update");
		m.addData("new_period", TimePeriod.DISABLED);
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
		InterThreadMessage m = new InterThreadMessage("time_period_update");
		m.addData("new_period", TimePeriod.GAME);
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

		});

		// Set to disabled
		disableMatch();
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

}
