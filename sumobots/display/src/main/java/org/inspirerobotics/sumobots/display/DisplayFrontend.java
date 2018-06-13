package org.inspirerobotics.sumobots.display;

import javafx.application.Application;
import javafx.stage.Stage;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;

import java.util.logging.Logger;

public class DisplayFrontend extends Application {

    private Logger logger = InternalLog.getLogger();
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        logger.info("Initializing the display...");
        this.stage = primaryStage;

        initStage(stage);
        logger.info("Display initialization complete...");
    }

    private void initStage(Stage stage) {
        stage.setTitle("FMS Display System: " + Resources.LIBRARY_VERSION);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
