package org.inspirerobotics.sumobots.driverstation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.inspirerobotics.sumobots.driverstation.config.Settings;
import org.inspirerobotics.sumobots.driverstation.gui.GuiController;
import org.inspirerobotics.sumobots.driverstation.gui.MainScene;
import org.inspirerobotics.sumobots.driverstation.joystick.JInputVerifier;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import java.util.logging.Logger;

public class DriverStationFrontend extends Application {

	private static final Settings settings = Settings.load();

	private static final Logger log = InternalLog.getLogger();

	private ThreadChannel threadChannel;

	private final boolean nonFieldMode = settings.nonFieldMode();

	private DriverStationBackend backend;

	private Stage stage;

	private MainScene mainScene;

	private GuiController controller = new GuiController(this);

	@Override
	public void start(Stage s) throws Exception {
		stage = s;
		log.setLevel(settings.logLevel());
		log.info("Starting the driver station!");

		JInputVerifier.checkLibrary();

		Thread.currentThread().setName("Frontend Thread");
		createBackendThread();
		initGui();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				update();
				Platform.runLater(this);
			}

		});
	}

	private void createBackendThread() {
		threadChannel = new ThreadChannel();

		log.fine("Starting field thread");
		backend = new DriverStationBackend(threadChannel.createPair(), (Settings) settings.clone());
		backend.start();
		log.fine("Finished starting field thread");
	}

	protected void update() {
		InterThreadMessage m = null;
		while ((m = threadChannel.poll()) != null) {
			onBackendMessageReceived(m);
		}
	}

	private void onBackendMessageReceived(InterThreadMessage m) {
		String name = m.getName();

		log.finer("Recieved Message from Backend: " + name);

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
			case "field_conn_status":
				controller.setFieldConnectionStatus((boolean) m.getData());
				break;
			case "robot_conn_status":
				controller.setRobotConnectionStatus((boolean) m.getData());
				break;
			case "joystick_status":
				boolean status = (boolean) m.getData();
				controller.setJoystickStatus(status);
				break;
			default:
				log.warning("Unknown Message Recieved on Frontend: " + name);
				break;
		}
	}

	private void initGui() {
		mainScene = new MainScene(controller);

		stage.setScene(mainScene.toScene());
		stage.setTitle("Driver Station!");
		stage.show();

		stage.setOnCloseRequest(event -> {
			log.info("Application Window has been closed");
			threadChannel.add(new InterThreadMessage("exit_app"));
			log.info("Closing down Frontend Thread...");
			Platform.exit();
		});

		addKeyHandlers();
	}

	private void addKeyHandlers() {
		stage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
			log.info("Key Pressed: " + k.getCode());
			if (k.getCode() == KeyCode.DIGIT1 && nonFieldMode) {
				attemptInit();
				return;
			}

			if (k.getCode() == KeyCode.DIGIT2 && nonFieldMode) {
				attemptGame();
				return;
			}

			if (k.getCode() == KeyCode.SPACE) {
				eStop();
				return;
			}

			if (k.getCode() == KeyCode.ENTER) {
				disable();
				return;
			}
		});
	}

	public void attemptInit() {
		threadChannel.add(new InterThreadMessage("new_state", TimePeriod.INIT));
	}

	public void attemptGame() {
		threadChannel.add(new InterThreadMessage("new_state", TimePeriod.GAME));
	}

	public void eStop() {
		threadChannel.add(new InterThreadMessage("new_state", TimePeriod.ESTOPPED));
	}

	public void disable() {
		threadChannel.add(new InterThreadMessage("new_state", TimePeriod.DISABLED));
	}

	public boolean isNonFieldMode() {
		return nonFieldMode;
	}

	public static Settings getSettings() {
		return settings;
	}

	public static void main(String[] args) {
		DriverStationFrontend.launch(args);
	}

}
