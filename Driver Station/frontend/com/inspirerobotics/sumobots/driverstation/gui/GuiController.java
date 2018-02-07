package com.inspirerobotics.sumobots.driverstation.gui;

import com.inspirerobotics.sumobots.lib.TimePeriod;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class GuiController{
	
	@FXML
	public TextField nameLabel;
	
	@FXML
	public TextField statusLabel;
	
	public void init() {
		statusLabel.setMinHeight(85);
		statusLabel.setMaxHeight(85);
		
		enterNewPeriod(TimePeriod.DISABLED);
	}

	public void setName(String newName) {
		nameLabel.setText("Name: " + newName);
	}

	public void enterNewPeriod(TimePeriod newPeriod) {
		statusLabel.setText("State: " + newPeriod);
		
		switch(newPeriod) {
		case DISABLED:
			statusLabel.setStyle("-fx-background-color:red;");
			break;
		case GAME:
			statusLabel.setStyle("-fx-background-color:green;");
			break;
		case INIT:
			statusLabel.setStyle("-fx-background-color:orange;");
			break;
		default:
			break;
		}
	}
	
}
