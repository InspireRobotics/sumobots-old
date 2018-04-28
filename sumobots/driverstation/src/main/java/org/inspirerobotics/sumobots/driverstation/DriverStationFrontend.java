package org.inspirerobotics.sumobots.driverstation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.inspirerobotics.sumobots.driverstation.gui.GuiController;
import org.inspirerobotics.sumobots.driverstation.gui.MainScene;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	
	/**
	 * The main scene
	 */
	private MainScene mainScene;
	
	/**
	 *  The GUIs controller
	 */
	private GuiController controller = new GuiController();
	
	private static final boolean nonFieldMode = DriverStationBackend.nonFieldMode;

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

		// Start the internal loop
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				update();
				Platform.runLater(this);
			}

		});
	}

	/**
	 * Updates the frontend and checks for messages from the backend
	 */
	protected void update() {
		log.setLevel(Level.ALL);
		// While there are messages from the frontend, handle them
		InterThreadMessage m = null;
		while ((m = threadChannel.poll()) != null) {
			onBackendMessageReceived(m);
		}
	}

	private void onBackendMessageReceived(InterThreadMessage m) {
		String name = m.getName();
		
		log.finer("Recieved Message from Backend: " + name);

		//Figure out what type of message it is
		switch (name) {
		case "new_name":
			String newName = (String) m.getData();
			controller.setName(newName);
			break;
		case "new_period":
			TimePeriod newPeriod = (TimePeriod) m.getData();
			stage.setTitle("DS: " + newPeriod.toString());
			controller.enterNewPeriod(newPeriod);
			break;
		case "conn_status":
			controller.setConnectionStatus((boolean) m.getData());
			break;
		default: //If it reaches this we don't know what it is so print a warning to the screen
			log.warning("Unknown Message Recieved on Frontend: " + name);
			break;
		}
	}
	
	/**
	 * Inits the GUI, and creates the onCloseRequest handler
	 */
	private void initGui() {
		// Create the gui
		mainScene = new MainScene(controller);

		// Setup the stage
		stage.setScene(mainScene.toScene());
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
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
			log.info("Key Pressed: " + k.getCode());
			if(k.getCode() == KeyCode.DIGIT1 && nonFieldMode) {
				threadChannel.add(new InterThreadMessage("new_state", TimePeriod.INIT));
				return;
			}
			
			if(k.getCode() == KeyCode.DIGIT2 && nonFieldMode) {
				threadChannel.add(new InterThreadMessage("new_state", TimePeriod.GAME));
				return;
			}
			
			if(k.getCode() == KeyCode.SPACE && nonFieldMode) {
				threadChannel.add(new InterThreadMessage("new_state", TimePeriod.ESTOPPED));
				return;
			}
			
			if(k.getCode() == KeyCode.ENTER && nonFieldMode) {
				threadChannel.add(new InterThreadMessage("new_state", TimePeriod.DISABLED));
				return;
			}
	    });

	}

	public static void main(String[] args) {
		DriverStationFrontend.launch(args);
	}

}