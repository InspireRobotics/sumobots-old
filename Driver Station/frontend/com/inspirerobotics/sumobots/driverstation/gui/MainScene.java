package com.inspirerobotics.sumobots.driverstation.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class MainScene extends AnchorPane{
	
	private static final String FXML_PATH = "/fxml/root.fxml";
	
	private Logger log = Logger.getLogger(Resources.LOGGER_NAME);
	
	public MainScene(GuiController controller) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
		fxmlLoader.setController(controller);
		fxmlLoader.setRoot(this);
		
		//Actually load the GUI
		try {
			fxmlLoader.load();
			controller.init();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to load root.fxml", e);
		}
	}
	

	public Scene toScene() {
		Group g = new Group();
		g.getChildren().add(this);
		return new Scene(g);
	}
	
}
