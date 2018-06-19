package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.inspirerobotics.sumobots.field.FieldFrontend;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.util.ArrayList;
import java.util.List;

public class DisplayPane extends AnchorPane {

	@FXML
	private Label connectionLabel;

	@FXML
	private VBox sceneBox;

	private List<RadioButton> buttons = new ArrayList<>();

	private FieldFrontend fieldFrontend;

	public DisplayPane(FieldFrontend fieldFrontend) {
		FXMLFileLoader.load("displayPane.fxml", this);

		setConnectionStatus(false);

		this.fieldFrontend = fieldFrontend;
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

	public void setScenes(String[] scenes) {
		sceneBox.getChildren().clear();
		buttons.clear();

		for (String scene : scenes) {
			RadioButton button = new RadioButton(scene);
			button.setOnAction(event -> {
				for (RadioButton b : buttons) {
					b.setSelected(false);
				}

				fieldFrontend.sendMessageToBackend(new InterThreadMessage("select_scene", scene));
				button.setSelected(true);
			});

			buttons.add(button);
			sceneBox.getChildren().add(button);
		}

		if (!buttons.isEmpty())
			buttons.get(0).getOnAction().handle(null);
	}
}
