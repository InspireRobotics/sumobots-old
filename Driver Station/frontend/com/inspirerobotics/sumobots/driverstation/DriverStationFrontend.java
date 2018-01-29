package com.inspirerobotics.sumobots.driverstation;

import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;
import com.inspirerobotics.sumobots.lib.concurrent.InterThreadMessage;
import com.inspirerobotics.sumobots.lib.concurrent.ThreadChannel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class for the driver station. Contains the main method. Roles
 * include handling the GUI, starting the backend thread, and handling
 * backend-frontend communication.
 * 
 * @author Noah
 *
 */
public class DriverStationFrontend extends Application {

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

		// Change Thread Name
		Thread.currentThread().setName("Frontend Thread");

		// Create the thread channel
		threadChannel = new ThreadChannel();

		// Create the backend thread
		log.fine("Starting backend thread");
		backend = new DriverStationBackend(threadChannel.createPair());
		backend.start();
		log.fine("Finished starting backend thread");

		// Init the Gui
		initGui();

	}

	private void initGui() {
		stage.setTitle("Driver Station!");
		stage.show();

		// When we close the window, close the app
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				log.info("Application Window has been closed");
				threadChannel.add(new InterThreadMessage("exit_app"));
				log.info("Closing down Frontend Thread...");
				Platform.exit();
			}

		});
	}

	public static void main(String[] args) {
		DriverStationFrontend.launch(args);
	}

}
