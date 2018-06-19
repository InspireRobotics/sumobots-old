package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.display.gui.DisplayScene;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class NoFieldScene extends DisplayScene {

	public NoFieldScene() {
		super(generateGroup(), "No Field Found");
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