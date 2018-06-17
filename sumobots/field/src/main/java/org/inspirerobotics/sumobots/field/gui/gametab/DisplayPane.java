package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class DisplayPane extends AnchorPane {

	@FXML
	private Label connectionLabel;

	public DisplayPane() {
		FXMLFileLoader.load("displayPane.fxml", this);

		setConnectionStatus(false);
	}

	public void setConnectionStatus(boolean connected) {
		if (connected) {
			connectionLabel.setText("Display Connected");
			connectionLabel.setStyle("-fx-background-color:green;");
		} else {
			connectionLabel.setText("Display disconnected");
			connectionLabel.setStyle("-fx-background-color:red;");
		}
	}
}
