package com.inspirerobotics.sumobots.field.gui;

import com.inspirerobotics.sumobots.lib.TimePeriod;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class MatchStatusBar extends HBox {

	private static final int cornerBoxSize = 80;
	private static final int stdHeight = 60;

	/**
	 * The Current Match Number
	 */
	private TextField matchNumberBox;

	/**
	 * The current state of the match, i.e. game ended, init, in-game
	 */
	private TextField matchStateBox;

	/**
	 * The current time the match has been going
	 */
	private TextField matchTimeBox;

	public MatchStatusBar() {
		// init the match number box
		matchNumberBox = new TextField("#250");
		matchNumberBox
				.setStyle("-fx-background-color:gray;-fx-text-fill: white; -fx-font-size: 24;-fx-alignment: center;");
		matchNumberBox.setMinWidth(cornerBoxSize);
		matchNumberBox.setMaxWidth(cornerBoxSize);
		matchNumberBox.setMinHeight(stdHeight);

		// init the match state box
		matchStateBox = new TextField("Game Active");
		matchStateBox.setMinHeight(stdHeight);
		matchStateBox
				.setStyle("-fx-background-color:green;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");

		matchTimeBox = new TextField("1:12");
		matchTimeBox
				.setStyle("-fx-background-color:gray;-fx-text-fill: white; -fx-font-size: 24;-fx-alignment: center;");
		matchTimeBox.setMinWidth(cornerBoxSize);
		matchTimeBox.setMaxWidth(cornerBoxSize);
		matchTimeBox.setMinHeight(stdHeight);

		this.getChildren().addAll(matchNumberBox, matchStateBox, matchTimeBox);
	}

	public void updateStats(TimePeriod timePeriod) {
		updateMatchStateBox(timePeriod);
	}

	private void updateMatchStateBox(TimePeriod timePeriod) {
		// No time period so lets default to disabled
		if (timePeriod == null) {
			updateMatchStateBox(TimePeriod.DISABLED);
			return;
		}

		switch (timePeriod) {
		case DISABLED:
			matchStateBox.setText("Disabled");
			matchStateBox
					.setStyle("-fx-background-color:red;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
			break;
		case GAME:
			matchStateBox.setText("In Game");
			matchStateBox.setStyle(
					"-fx-background-color:green;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
			break;
		case INIT:
			matchStateBox.setText("Initialization");
			matchStateBox.setStyle(
					"-fx-background-color:orange;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
			break;
		}
	}

	/**
	 * Updates the layout of the gui
	 */
	public void updateGui() {
		matchStateBox.setMinWidth(this.getWidth() - (cornerBoxSize * 2));
	}

}
