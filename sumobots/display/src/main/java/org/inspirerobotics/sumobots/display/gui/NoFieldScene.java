package org.inspirerobotics.sumobots.display.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class NoFieldScene extends Scene {

	public NoFieldScene() {
		super(generateGroup());
	}

	private static Parent generateGroup() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(new NoFieldPane());
		return anchorPane;
	}
}

class NoFieldPane extends AnchorPane {

	public NoFieldPane() {
		FXMLFileLoader.load("noField.fxml", this);
	}

}