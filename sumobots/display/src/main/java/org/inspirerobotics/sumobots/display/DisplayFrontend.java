package org.inspirerobotics.sumobots.display;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.inspirerobotics.sumobots.display.gui.SceneManager;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import java.util.logging.Logger;

public class DisplayFrontend extends Application {

	private static final String threadName = "Display Frontend";
	private final Logger logger = InternalLog.getLogger();
	private final ThreadChannel threadChannel = new ThreadChannel();
	private final DisplayBackend displayBackend = new DisplayBackend(threadChannel.createPair());
	private SceneManager sceneManager;
	private Stage stage;

	@Override
	public void start(Stage primaryStage) {
		Thread.currentThread().setName(threadName);
		logger.info("Initializing the display...");
		this.displayBackend.start();
		this.stage = primaryStage;
		this.sceneManager = new SceneManager();

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
	}

	private void handleIncomingMessages() {
		InterThreadMessage message = null;

		while ((message = threadChannel.poll()) != null) {
			logger.fine("Received message from backend: " + formatMessageToString(message));
			onMessageReceived(message);
		}
	}

	private String formatMessageToString(InterThreadMessage message) {
		return message.getName() + (message.getData() == null ? "" : (": " + message.getData()));
	}

	private void onMessageReceived(InterThreadMessage message) {
		if (message.getName().equals("conn_status")) {
			boolean connected = (boolean) message.getData();

			if (connected) {
				stage.setScene(sceneManager.getLogoScene());
				stage.show();
				logger.fine("Showing logo scene");
				sendScenes(sceneManager.getSceneNameArray());
			} else {
				stage.setScene(sceneManager.getNoFieldScene());
			}
		} else if (message.getName().equals("select_scene")) {
			sceneManager.showScene((String) message.getData(), stage);
		}
	}

	private void initStage(Stage stage) {
		stage.setTitle("FMS Display System: " + Resources.LIBRARY_VERSION);
		stage.setOnCloseRequest(new FrontendShutdownHandler(this));
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
