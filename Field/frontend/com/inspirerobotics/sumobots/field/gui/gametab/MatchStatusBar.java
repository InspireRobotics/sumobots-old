package com.inspirerobotics.sumobots.field.gui.gametab;

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

	/**
	 * The internal match number
	 */
	private int matchNum = 0;

	/**
	 * The internal Time
	 */
	private long matchTime;

	/**
	 * The last known time period
	 */
	private TimePeriod lastKnownTimePeriod;

	public MatchStatusBar() {
		// init the match number box and match time box
		String style = "-fx-background-color:#2a2a2a;-fx-text-fill: white; -fx-font-size: 24;-fx-alignment: center;" + 
				"-fx-border-radius: 0 0 0 0;-fx-background-radius: 0 0 0 0;";
		
		matchNumberBox = new TextField("#250");
		matchNumberBox.setStyle(style);
		matchNumberBox.setMinWidth(cornerBoxSize);
		matchNumberBox.setMaxWidth(cornerBoxSize);
		matchNumberBox.setMinHeight(stdHeight);


		matchTimeBox = new TextField("1:12");
		matchTimeBox.setStyle(style);
		matchTimeBox.setMinWidth(cornerBoxSize);
		matchTimeBox.setMaxWidth(cornerBoxSize);
		matchTimeBox.setMinHeight(stdHeight);

		// init the match state box
		style = "-fx-background-color:green;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;" + 
				"-fx-border-radius: 0 0 0 0;-fx-background-radius: 0 0 0 0;";
		
		matchStateBox = new TextField("Game Active");
		matchStateBox.setMinHeight(stdHeight);
		matchStateBox.setStyle("-fx-background-color:green;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
		
		this.getChildren().addAll(matchNumberBox, matchStateBox, matchTimeBox);
	}

	public void updateStats(TimePeriod timePeriod) {
		// If its null, the backend has yet sent the first match update
		// so we will assume it will be null
		if (timePeriod == null) {
			timePeriod = TimePeriod.DISABLED;
		}

		// Update what state we are in
		updateMatchStateBox(timePeriod);

		// If we are in a new time period, lets update the time and possibly number
		if (timePeriod != lastKnownTimePeriod) {
			enterNewPeriod(timePeriod);
			lastKnownTimePeriod = timePeriod;
		}

		// Update Match Time/Match Number boxes
		matchNumberBox.setText("#" + matchNum);

		String time = "" + (System.currentTimeMillis() - matchTime) / 1000;
		matchTimeBox.setMinWidth(time.length() * 20);
		matchTimeBox.setText(time);

	}

	private void enterNewPeriod(TimePeriod timePeriod) {
		if (timePeriod == TimePeriod.INIT) {
			matchNum++;
		}

		matchTime = System.currentTimeMillis();
	}

	private void updateMatchStateBox(TimePeriod timePeriod) {
		switch (timePeriod) {
		case DISABLED:
			matchStateBox.setText("Disabled");
			matchStateBox.setStyle("-fx-background-color:red;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
			break;
		case GAME:
			matchStateBox.setText("In Game");
			matchStateBox.setStyle("-fx-background-color:green;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
			break;
		case INIT:
			matchStateBox.setText("Initialization");
			matchStateBox.setStyle("-fx-background-color:orange;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
			break;
		case ESTOPPED:
			matchStateBox.setText("E-STOPPED");
			matchStateBox.setStyle("-fx-background-color:black;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
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
