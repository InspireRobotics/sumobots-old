package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.display.gui.DisplayScene;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class EStopScene extends DisplayScene {

	public EStopScene() {
		super(generateGroup(), "EStop");
	}

	private static Parent generateGroup() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(new EStopPane());
		return anchorPane;
	}

}

class EStopPane extends AnchorPane {

	EStopPane() {
		FXMLFileLoader.load("estop.fxml", this);
	}

}
