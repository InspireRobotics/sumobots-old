package org.inspirerobotics.sumobots.display;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
	private Stage stage;

	@Override
	public void start(Stage primaryStage) {
		Thread.currentThread().setName(threadName);
		logger.info("Initializing the display...");
		this.displayBackend.start();
		this.stage = primaryStage;

		initStage(stage);
		logger.info("Display initialization complete...");
	}

	private void initStage(Stage stage) {
		stage.setTitle("FMS Display System: " + Resources.LIBRARY_VERSION);
		stage.setOnCloseRequest(new FrontendShutdownHandler(this));
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
