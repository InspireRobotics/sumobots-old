package org.inspirerobotics.sumobots.driverstation.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.inspirerobotics.sumobots.driverstation.DriverStationFrontend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;

import java.util.List;

/**
 * The main controller for the gui
 */
public class GuiController {

	@FXML
	public VBox centralVBox;

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

	@FXML
	public TextField joystickLabel;

	@FXML
	public Label readyLabel;

	private DriverStationFrontend driverStationFrontend;

	private boolean robotReady;

	private boolean fieldReady;

	private boolean joysticksReady;

	public GuiController(DriverStationFrontend dsf) {
		this.driverStationFrontend = dsf;
	}

	public void init() {
		centralVBox.getChildren().add(new ControlBar(driverStationFrontend));

		setFocusTravesable();

		enterNewPeriod(TimePeriod.DISABLED);
		setFieldConnectionStatus(false);
		setRobotConnectionStatus(false);
		setJoystickStatus(false);

		runLoop();

	}

	public boolean isReady() {
		return fieldReady && robotReady && joysticksReady;
	}

	private void runLoop() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (DriverStationFrontend.getSettings().shouldStoreLog())
					checkForLogUpdate();

				updateReadyScreen();

				Platform.runLater(this);
			}
		});
	}

	private void updateReadyScreen() {
		if (isReady()) {
			readyLabel.setText("Ready!");
			readyLabel.setStyle("-fx-background-color:green;-fx-text-fill:white;");
		} else {
			readyLabel.setText("Not Ready!");
			readyLabel.setStyle("-fx-background-color:red;-fx-text-fill:white;");
		}
	}

	private void setFocusTravesable() {
		statusLabel.setFocusTraversable(false);
		nameLabel.setFocusTraversable(false);
		robotLabel.setFocusTraversable(false);
		joystickLabel.setFocusTraversable(false);
	}

	private void checkForLogUpdate() {
		List<String> logLines = InternalLog.getInstance().getLogLines();

		if (logLines.isEmpty()) {
			logConsole.clear();
			return;
		}

		String lastLine = logLines.get(logLines.size() - 1);
		if (logConsole.getText().endsWith(lastLine)) {
			return;
		}

		updateLog(logLines);
	}

	private void updateLog(List<String> logLines) {
		StringBuilder b = new StringBuilder();

		for (Object line : logLines.toArray()) {
			b.append(line);
		}

		logConsole.setText(b.toString());
	}

	public void setName(String newName) {
		nameLabel.setText("Name: " + newName);
	}

	public void setFieldConnectionStatus(boolean connected) {
		if (driverStationFrontend == null) {
			return;
		}

		if (driverStationFrontend.isNonFieldMode()) {
			fieldLabel.setText("Non Field Mode!");
			fieldLabel.setStyle("-fx-background-color:blue");
			fieldReady = true;
		} else if (connected) {
			fieldReady = true;
			fieldLabel.setText("Field: Connected!");
			fieldLabel.setStyle("-fx-background-color:green");
		} else {
			fieldReady = false;
			fieldLabel.setText("Field: Not Connected!");
			fieldLabel.setStyle("-fx-background-color:red");
		}
	}

	public void setRobotConnectionStatus(boolean connected) {
		if (connected) {
			robotReady = true;
			robotLabel.setText("Robot: Connected!");
			robotLabel.setStyle("-fx-background-color:green");
		} else {
			robotReady = false;
			robotLabel.setText("Robot: Not Connected!");
			robotLabel.setStyle("-fx-background-color:red");
		}
	}

	public void setJoystickStatus(boolean connected) {
		if (connected) {
			joysticksReady = true;
			joystickLabel.setText("Joystick: Connected!");
			joystickLabel.setStyle("-fx-background-color:green");
		} else {
			joysticksReady = false;
			joystickLabel.setText("Joystick: Not Connected!");
			joystickLabel.setStyle("-fx-background-color:red");
		}
	}

	public void enterNewPeriod(TimePeriod newPeriod) {
		statusLabel.setText("State: " + newPeriod);

		switch (newPeriod) {
			case DISABLED:
				statusLabel.setStyle("-fx-background-color:red;");
				nameLabel.setStyle("-fx-background-color:red;");
				break;
			case GAME:
				statusLabel.setStyle("-fx-background-color:green;");
				nameLabel.setStyle("-fx-background-color:green;");
				break;
			case INIT:
				statusLabel.setStyle("-fx-background-color:orange;");
				nameLabel.setStyle("-fx-background-color:orange;");
				break;
			case ESTOPPED:
				statusLabel.setStyle("-fx-background-color:black;");
				nameLabel.setStyle("-fx-background-color:black;");

			default:
				break;
		}
	}
}
