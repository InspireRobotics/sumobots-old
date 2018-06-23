package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.display.gui.DisplayScene;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class FieldResetScene extends DisplayScene {

	public FieldResetScene() {
		super(generateGroup(), "Field Reset");
	}

	private static Parent generateGroup() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(new FieldResetPane());
		return anchorPane;
	}
}

class FieldResetPane extends AnchorPane {

	public FieldResetPane() {
		FXMLFileLoader.load("fieldReset.fxml", this);
	}

}
