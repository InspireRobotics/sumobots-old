package org.inspirerobotics.sumobots.library.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Alerts {

    public enum ShutdownLevel{ALL, JAVAFX, NONE}

    public static void errorAlert(ShutdownLevel shutdownLevel, String name, String desc, boolean waitForShutdown){
        Alert alert = new Alert(Alert.AlertType.ERROR, desc);
        alert.setTitle(name);
        alert.setHeaderText(name);

        if (waitForShutdown)
            alert.showAndWait();
        else
            alert.show();

        shutdown(shutdownLevel);
    }

    private static void shutdown(ShutdownLevel shutdownLevel) {
        switch (shutdownLevel){
            case ALL:
                System.exit(0);
                break;
            case NONE:
                break;
            case JAVAFX:
                Platform.exit();
                break;
        }
    }
}
