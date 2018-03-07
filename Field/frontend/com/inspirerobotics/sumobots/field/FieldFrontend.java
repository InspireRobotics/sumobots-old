package com.inspirerobotics.sumobots.field;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.gui.RootGroup;
import com.inspirerobotics.sumobots.field.util.InternalLog;
import com.inspirerobotics.sumobots.lib.TimePeriod;
import com.inspirerobotics.sumobots.lib.concurrent.InterThreadMessage;
import com.inspirerobotics.sumobots.lib.concurrent.ThreadChannel;
import com.inspirerobotics.sumobots.lib.networking.connection.Connection;
import com.inspirerobotics.sumobots.lib.networking.tables.NetworkTable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.audio.AudioPlayer;

/**
 * This is the main class for the Application. This starts the
 * {@link #fieldBackend} thread and then runs the gui
 * 
 * @author Noah
 */
public class FieldFrontend extends Application {

	/**
	 * The FieldBackend Object. This will handle everything that isn't the gui
	 */
	private FieldBackend fieldBackend;

	/**
	 * The channel for connection to the BackendThread
	 */
	private ThreadChannel threadChannel;

	/**
	 * The Stage for the main GUI
	 */
	private Stage stage;

	/**
	 * The root of the GUI
	 */
	private RootGroup root;

	/**
	 * The log
	 */
	private Logger log = InternalLog.getLogger();

	/**
	 * The current time period, as last update by the Field Backend
	 */
	private TimePeriod timePeriod;

	@Override
	public void start(Stage s) throws Exception {
		// Change Thread Name
		Thread.currentThread().setName("Frontend Thread");

		// Create the thread channel
		threadChannel = new ThreadChannel();

		// Create the backend thread
		log.fine("Starting backend thread");
		fieldBackend = new FieldBackend(threadChannel.createPair());
		fieldBackend.start();
		log.fine("Finished starting backend thread");

		// Init the stage(AKA "Window") and then add the GUI elements
		log.fine("Creating the GUI");
		this.stage = s;
		initStage(stage);
		initGUI();
		log.fine("Finished Creating the GUI");

		// Now that the stage is created show it on the screen
		stage.show();

		// We are now done with initialization
		log.info("Frontend initialization complete. Starting main loop");

		// Start the internal loop
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				update();
				Platform.runLater(this);
			}

		});
	}

	protected void update() {
		// While there are messages from the frontend, handle them
		InterThreadMessage m = null;
		while ((m = threadChannel.poll()) != null) {
			onBackendMessageReceived(m);
		}
	}

	private void onBackendMessageReceived(InterThreadMessage m) {
		String name = m.getName();
		log.finer("Recieved Message from Backend: " + name);

		// Figure out what type of message it is
		switch (name) {
		case "conn_update":
			Object data = m.getData();
			if (data instanceof List) {
				@SuppressWarnings("unchecked")
				ArrayList<Connection> conn = (ArrayList<Connection>) data;
				root.getGameTab().setConnections(conn);
			}
			break;
		case "time_period_update":
			timePeriod = (TimePeriod) m.getData();
			log.fine("New Time Period on Frontend: " + timePeriod);
			
			if(timePeriod == TimePeriod.GAME) {
				AudioEffect.play("start.wav");
			}else if(timePeriod == TimePeriod.ESTOPPED) {
				AudioEffect.play("estop.wav");
			}
			
			break;
		case "update_internal_table":
			root.getGameTab().setInternalNetwTable((NetworkTable) m.getData());
			break;
		default: // If it reaches this we don't know what it is so print a warning to the screen
			log.warning("Unknown Message Recieved on Frontend: " + name);
			break;
		}
	}

	/**
	 * Inits the GUI and sets the window to so that GUI
	 */
	private void initGUI() {
		root = new RootGroup(this);
		stage.setScene(root.toScene());
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
			log.finer("Key Pressed: " + k.getCode());
			if(this.timePeriod == TimePeriod.GAME) {
				if(k.getCode() == KeyCode.SPACE) {
					eStop();
		            k.consume();
				}
				
				if(k.getCode() == KeyCode.ENTER) {
					endMatch();
					k.consume();
				}
			}
	    });
	}

	/**
	 * Inits the stage with its default settings and then sets it to visible
	 * 
	 * @param stage
	 */
	private void initStage(Stage stage) {
		stage.setTitle("SumoBots FMS");
		stage.setAlwaysOnTop(false);
		stage.setMinWidth(700);
		stage.setMinHeight(400);

		// When we close the window, close the app
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				log.info("Application Window has been closed");
				threadChannel.add(new InterThreadMessage("exit_app"));
				log.info("Closing down Frontend Thread...");
				stage.hide();
				Platform.exit();
			}

		});
	}

	/**
	 * Stops the application
	 */
	@Override
	public void stop() throws Exception {
		super.stop();

		fieldBackend.join();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * GUI Methods
	 * 
	 * These methods are generally called by the GUI. These will send messages to
	 * the backend
	 */

	public void startMatch() {
		threadChannel.add(new InterThreadMessage("start_match"));
	}

	public void initMatch() {
		threadChannel.add(new InterThreadMessage("init_match"));
	}

	public void endMatch() {
		threadChannel.add(new InterThreadMessage("end_match"));
	}

	public void eStop() {
		threadChannel.add(new InterThreadMessage("e-stop"));
	}

	public void closeAll() {
		threadChannel.add(new InterThreadMessage("close_all"));
	}

	public void disable(String name) {
		threadChannel.add(new InterThreadMessage("disable_ds", name));
	}

	/**
	 * The current time period, as last update by the Field Backend
	 */
	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

}
