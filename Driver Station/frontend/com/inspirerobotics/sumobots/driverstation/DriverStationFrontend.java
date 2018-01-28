package com.inspirerobotics.sumobots.driverstation;

import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.concurrent.ThreadChannel;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class for the driver station. Contains the main method.
 * Roles include handling the GUI, starting the backend thread, and handling backend-frontend communication.
 * @author Noah
 *
 */
public class DriverStationFrontend extends Application{

	/**
	 * the log 
	 */
	private Logger log = Logger.getLogger(Resources.LOGGER_NAME);
	
	/**
	 * The thread channel between frontend and backend threads
	 */
	private ThreadChannel threadChannel;
	
	/**
	 * The driver station backend. Handles everything but the GUI
	 */
	private DriverStationBackend backend;
	
	/**
	 * The stage for the GUIs
	 */
	private Stage stage;
	
	@Override
	public void start(Stage s) throws Exception {
		stage = s;
		
		//Change Thread Name
		Thread.currentThread().setName("Frontend Thread");
		
		// Create the thread channel
		threadChannel = new ThreadChannel();
		
		//Create the backend thread
		log.fine("Starting backend thread");
		backend = new DriverStationBackend(threadChannel.createPair());
		backend.start();
		log.fine("Finished starting backend thread");
		
		//Start the gui
		stage.setTitle("Driver Station!");
		stage.show();
	}
	
	public static void main(String[] args) {
		DriverStationFrontend.launch(args);
	}

}
