package org.inspirerobotics.sumobots.library.gui;

import javafx.fxml.FXMLLoader;
import org.inspirerobotics.sumobots.library.InternalLog;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLFileLoader {

	private static final Logger logger = InternalLog.getLogger();

	public static void load(String name, Object controller) {
		load(name, controller, controller);
	}

	public static void load(String name, Object controller, Object root) {
		logger.fine("Loading " + name);
		FXMLLoader fxmlLoader = new FXMLLoader(FXMLFileLoader.class.getClass().getResource("/fxml/" + name));
		fxmlLoader.setController(controller);
		fxmlLoader.setRoot(root);

		if (fxmlLoader.getLocation() == null){
			RuntimeException e = new FXMLFileLoadException("Failed to find file " + name);

			Alerts.exceptionAlert(Alerts.ShutdownLevel.JAVAFX, e);
			throw e;
		}


		try {
			fxmlLoader.load();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to load " + name, e);
			Alerts.exceptionAlert(Alerts.ShutdownLevel.JAVAFX, e);
		}

		logger.fine("Loaded " + name);
	}
}
