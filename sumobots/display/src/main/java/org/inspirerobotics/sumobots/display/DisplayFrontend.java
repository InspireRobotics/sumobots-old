package org.inspirerobotics.sumobots.display;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.inspirerobotics.sumobots.display.config.Settings;
import org.inspirerobotics.sumobots.display.gui.SceneManager;
import org.inspirerobotics.sumobots.display.gui.scenes.GameScene;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import java.util.logging.Logger;

public class DisplayFrontend extends Application {

	private static final String threadName = "Display Frontend";
	private final Settings settings = Settings.load();
	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel = new ThreadChannel();
	private final DisplayBackend displayBackend = new DisplayBackend(threadChannel.createPair(),
			(Settings) settings.clone());
	private SceneManager sceneManager;
	private Stage stage;
	private TimePeriod timePeriod = TimePeriod.DISABLED;

	@Override
	public void start(Stage primaryStage) {
		Thread.currentThread().setName(threadName);
		logger.info("Initializing the display...");
		logger.setLevel(settings.logLevel());
		this.displayBackend.start();
		this.stage = primaryStage;
		this.sceneManager = new SceneManager(stage);

		initStage(stage);
		logger.info("Display initialization complete...");

		startUpdateLoop();
	}

	private void sendScenes(String[] sceneNameArray) {
		InterThreadMessage message = new InterThreadMessage("scenes", sceneNameArray);
		threadChannel.add(message);
	}

	private void startUpdateLoop() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				update();
				Platform.runLater(this);
			}
		});
	}

	private void update() {
		handleIncomingMessages();

		if (timePeriod == TimePeriod.GAME) {
			GameScene.updateClock();
		}
	}

	private void handleIncomingMessages() {
		InterThreadMessage message = null;

		while ((message = threadChannel.poll()) != null) {
			logger.finer("Received message from backend: " + formatMessageToString(message));
			onMessageReceived(message);
		}
	}

	private String formatMessageToString(InterThreadMessage message) {
		return message.getName() + (message.getData() == null ? "" : (": " + message.getData()));
	}

	private void onMessageReceived(InterThreadMessage message) {
		if (message.getName().equals("conn_status")) {
			updateConnectionStatus((boolean) message.getData());
		} else if (message.getName().equals("select_scene")) {
			sceneManager.showScene((String) message.getData());
		} else if (message.getName().equals("set_teams")) {
			String[] teams = (String[]) message.getData();
			GameScene.setTeams(teams);
		} else if (message.getName().equals("set_time_period")) {
			timePeriod = TimePeriod.fromString((String) message.getData());

			if (timePeriod == TimePeriod.INIT) {
				GameScene.resetClock();
			} else if (timePeriod == TimePeriod.ESTOPPED) {
				sceneManager.showScene(sceneManager.getEStopScene());
			}

			logger.info("Entering time period: " + timePeriod);
		} else {
			logger.warning("Received unknown message from backend: " + message);
		}
	}

	private void updateConnectionStatus(boolean connected) {
		if (connected) {
			sceneManager.showScene(sceneManager.getLogoScene());
			logger.fine("Showing logo scene");
			sendScenes(sceneManager.getSceneNameArray());
		} else {
			sceneManager.showScene(sceneManager.getNoFieldScene());
		}
	}

	private void initStage(Stage stage) {
		stage.setTitle("FMS Display System: " + Resources.LIBRARY_VERSION);
		stage.setOnCloseRequest(new FrontendShutdownHandler(this));

		stage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
			logger.finer("Key Pressed: " + k.getCode());
			if (k.getCode() == KeyCode.F1) {
				sceneManager.setFullscreen(!sceneManager.isFullscreen());
				logger.fine("Entering Full Screen Mode: " + sceneManager.isFullscreen());
			} else if (k.getCode() == KeyCode.F2) {
				logger.info("Showing debug screen!");
				sceneManager.showScene(sceneManager.getDebugScene());
			}
		});

		stage.setScene(sceneManager.getNoFieldScene());
		stage.show();
	}

	public void shutdown() {
		Platform.exit();
		threadChannel.add(new InterThreadMessage("shutdown"));
	}

	public static void main(String[] args) {
		launch(args);
	}

}

class FrontendShutdownHandler implements EventHandler<WindowEvent> {

	private DisplayFrontend displayFrontend;

	public FrontendShutdownHandler(DisplayFrontend displayFrontend) {
		this.displayFrontend = displayFrontend;
	}

	@Override
	public void handle(WindowEvent event) {
		displayFrontend.shutdown();
	}
}
