package org.inspirerobotics.sumobots.field.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class AboutTab extends AnchorPane {

	@FXML
	private Label versionLabel;

	public AboutTab() {
		FXMLFileLoader.load("about.fxml", this);

		versionLabel.setText("Version: " + Resources.LIBRARY_VERSION);
	}

}
