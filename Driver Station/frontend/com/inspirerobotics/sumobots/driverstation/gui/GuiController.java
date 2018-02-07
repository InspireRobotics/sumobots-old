package com.inspirerobotics.sumobots.driverstation.gui;

import com.inspirerobotics.sumobots.lib.TimePeriod;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * The main controller for the gui
 */
public class GuiController{
	
	/**
	 * The text field of the DS name
	 */
	@FXML
	public TextField nameLabel;
	
	/**
	 * The text field that contains the current time period
	 */
	@FXML
	public TextField statusLabel;
	
	/**
	 * Initializes the GUI controller and elements
	 */
	public void init() {
		statusLabel.setMinHeight(85);
		statusLabel.setMaxHeight(85);
		
		enterNewPeriod(TimePeriod.DISABLED);
	}
	
	/**
	 * Sets the name displayed in the name text field
	 * @see #nameLabel
	 */
	public void setName(String newName) {
		nameLabel.setText("Name: " + newName);
	}

	/**
	 * Sets the time period shown by the gui
	 * @see #statusLabel
	 */
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
