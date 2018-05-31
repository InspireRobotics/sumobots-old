package org.inspirerobotics.sumobots.library.gui;

import javafx.application.Platform;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Alerts {

    public enum ShutdownLevel{ALL, JAVAFX, NONE}

    public static void errorAlert(ShutdownLevel shutdownLevel, String name, String desc){
        JOptionPane.showMessageDialog(null,
                name + ": " + desc,
                name,
                JOptionPane.ERROR_MESSAGE);

        shutdown(shutdownLevel);
    }

    public static void exceptionAlert(ShutdownLevel shutdownLevel, Exception e){
        errorAlert(shutdownLevel, e.getMessage(), traceToString(e));
    }

    /**
     * TODO Test This
     */
    private static String traceToString(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
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
