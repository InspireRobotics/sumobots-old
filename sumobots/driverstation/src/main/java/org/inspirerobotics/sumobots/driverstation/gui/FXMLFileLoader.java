package org.inspirerobotics.sumobots.driverstation.gui;

import javafx.fxml.FXMLLoader;
import org.inspirerobotics.sumobots.library.Resources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLFileLoader {

	private static final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	public static void load(String name, Object controller) {
		load(name, controller, controller);
	}

	public static void load(String name, Object controller, Object root) {
		logger.fine("Loading " + name);
		FXMLLoader fxmlLoader = new FXMLLoader(FXMLFileLoader.class.getClass().getResource("/fxml/" + name));
		fxmlLoader.setController(controller);
		fxmlLoader.setRoot(root);

		if (fxmlLoader.getLocation() == null)
			throw new RuntimeException("Failed to load file: " + name);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to load " + name, e);
		}

		logger.fine("Loaded " + name);
	}

}
