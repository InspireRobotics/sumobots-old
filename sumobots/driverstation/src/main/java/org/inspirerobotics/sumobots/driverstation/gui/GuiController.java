package org.inspirerobotics.sumobots.driverstation.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.inspirerobotics.sumobots.driverstation.Settings;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;

import java.util.List;

/**
 * The main controller for the gui
 */
public class GuiController {

	@FXML
	public TextArea logConsole;

	@FXML
	public TextField nameLabel;

	@FXML
	public TextField statusLabel;

	@FXML
	public TextField fieldLabel;

	@FXML
	public TextField robotLabel;

	public void init() {
		statusLabel.setMinHeight(85);
		statusLabel.setMaxHeight(85);
		statusLabel.setFocusTraversable(false);
		nameLabel.setFocusTraversable(false);
		robotLabel.setFocusTraversable(false);

		enterNewPeriod(TimePeriod.DISABLED);
		setFieldConnectionStatus(false);
		setRobotConnectionStatus(false);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateLog();

				Platform.runLater(this);
			}
		});
	}

	private void updateLog() {
		List<String> logLines = InternalLog.getInstance().getLogLines();

		StringBuilder b = new StringBuilder();

		for (Object line : logLines.toArray()) {
			b.append(line);
		}

		// Probably bad
		if (!b.toString().equals(logConsole.getText()))
			logConsole.setText(b.toString());
	}

	public void setName(String newName) {
		nameLabel.setText("Name: " + newName);
	}

	public void setFieldConnectionStatus(boolean connected) {
		if (Settings.nonFieldMode) {
			fieldLabel.setText("Non Field Mode!");
			fieldLabel.setStyle("-fx-background-color:blue");
		} else if (connected) {
			fieldLabel.setText("Field: Connected!");
			fieldLabel.setStyle("-fx-background-color:green");
		} else {
			fieldLabel.setText("Field: Not Connected!");
			fieldLabel.setStyle("-fx-background-color:red");
		}
	}

	public void setRobotConnectionStatus(boolean connected) {
		if (connected) {
			robotLabel.setText("Robot: Connected!");
			robotLabel.setStyle("-fx-background-color:green");
		} else {
			robotLabel.setText("Robot: Not Connected!");
			robotLabel.setStyle("-fx-background-color:red");
		}
	}

	public void enterNewPeriod(TimePeriod newPeriod) {
		statusLabel.setText("State: " + newPeriod);

		switch (newPeriod) {
			case DISABLED:
				statusLabel.setStyle("-fx-background-color:red;");
				break;
			case GAME:
				statusLabel.setStyle("-fx-background-color:green;");
				break;
			case INIT:
				statusLabel.setStyle("-fx-background-color:orange;");
				break;
			case ESTOPPED:
				statusLabel.setStyle("-fx-background-color:black;");

			default:
				break;
		}
	}

}
