package com.inspirerobotics.sumobots.field;

import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.util.InternalLog;
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
	 * If the backend should be running
	 */
	private boolean running = true;

	public FieldBackend(ThreadChannel tc) {
		this.setName("Backend Thread");
		this.channel = tc;
	}

	@Override
	public synchronized void start() {
		super.start();
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

			// While there are messages from the frontend, handle them
			InterThreadMessage m = null;
			while ((m = channel.poll()) != null) {
				onFrontendMessageReceived(m);
				
				//This needs to be here to prevent other messages from being
				//proccessed after the app is supposed to be closing
				if(!running)
					break;
			}
		}
		
		server.closeAll(); 
		
		 
		
		log.info("Backend Thread Shutdown Complete!");
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
		log.info("Initializing the Match!");
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod("init"));
	}

	private void endMatch() {
		log.info("Ending the Match!");
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod("end"));
	}

	private void startMatch() {
		log.info("Starting the Match!");
		server.sendAll(ArchetypalMessages.enterNewMatchPeriod("game"));
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
	}

}
