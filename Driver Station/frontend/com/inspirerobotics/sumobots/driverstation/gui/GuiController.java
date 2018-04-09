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
	 * The text field that displays the 
	 * status of the DS-Field connection
	 */
	@FXML
	public TextField connectedLabel;
	
	/**
	 * Initializes the GUI controller and elements
	 */
	public void init() {
		statusLabel.setMinHeight(85);
		statusLabel.setMaxHeight(85);
		statusLabel.setFocusTraversable(false);
		nameLabel.setFocusTraversable(false);
		
		enterNewPeriod(TimePeriod.DISABLED);
		setConnectionStatus(false);
	}
	
	/**
	 * Sets the name displayed in the name text field
	 * @see #nameLabel
	 */
	public void setName(String newName) {
		nameLabel.setText("Name: " + newName);
	}
	
	public void setConnectionStatus(boolean connected) {
		if(connected) {
			connectedLabel.setText("Connected!");
			connectedLabel.setStyle("-fx-background-color:green");
		}else {
			connectedLabel.setText("Not Connected!");
			connectedLabel.setStyle("-fx-background-color:red");
		}
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
		case ESTOPPED:
			statusLabel.setStyle("-fx-background-color:black;");

		default:
			break;
		}
	}
	
}
