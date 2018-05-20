package org.inspirerobotics.sumobots.driverstation.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.driverstation.DriverStationFrontend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class ControlBar extends AnchorPane {

	@FXML
	public ToolBar toolbar;

	@FXML
	public Button initButton;

	@FXML
	public Button startButton;

	@FXML
	public Button disableButton;

	private DriverStationFrontend driverStationFrontend;

	public ControlBar(DriverStationFrontend dsf) {
		this.driverStationFrontend = dsf;

		FXMLFileLoader.load("controlBar.fxml", this, this);

		if (!driverStationFrontend.isNonFieldMode())
			disableTimePeriodButtons();
	}

	private void disableTimePeriodButtons() {
		initButton.setDisable(true);
		startButton.setDisable(true);
		disableButton.setDisable(true);
	}

	@FXML
	public void clearLog() {
		InternalLog.getInstance().clear();
	}

	@FXML
	public void disable() {
		driverStationFrontend.disable();
	}

	@FXML
	public void initGame() {
		driverStationFrontend.attemptInit();
	}

	@FXML
	public void startGame() {
		driverStationFrontend.attemptGame();
	}
}
