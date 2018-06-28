package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;

public class MatchStatusBar extends HBox {

	private static final int cornerBoxSize = 80;
	private static final int stdHeight = 60;

	private TextField matchNumberBox;

	private TextField matchStateBox;

	private TextField matchTimeBox;

	private int matchNum = 0;

	private long matchTime = 0;
	private long matchStartTime = 0;

	private TimePeriod lastKnownTimePeriod;

	public MatchStatusBar() {
		String style = "-fx-background-color:#2a2a2a;-fx-text-fill: white; -fx-font-size: 24;-fx-alignment: center;"
				+ "-fx-border-radius: 0 0 0 0;-fx-background-radius: 0 0 0 0;";

		matchNumberBox = new TextField("#250");
		matchNumberBox.setStyle(style);
		matchNumberBox.setMinWidth(cornerBoxSize);
		matchNumberBox.setMaxWidth(cornerBoxSize);
		matchNumberBox.setMinHeight(stdHeight);

		matchTimeBox = new TextField("0");
		matchTimeBox.setStyle(style);
		matchTimeBox.setMinWidth(cornerBoxSize);
		matchTimeBox.setMaxWidth(cornerBoxSize);
		matchTimeBox.setMinHeight(stdHeight);

		matchStateBox = new TextField("Game Active");
		matchStateBox.setMinHeight(stdHeight);
		matchStateBox
				.setStyle("-fx-background-color:green;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");

		this.getChildren().addAll(matchNumberBox, matchStateBox, matchTimeBox);
	}

	public void updateStats(TimePeriod timePeriod) {
		if (timePeriod == null) {
			timePeriod = TimePeriod.DISABLED;
		}

		updateMatchStateBox(timePeriod);

		if (timePeriod != lastKnownTimePeriod) {
			enterNewPeriod(timePeriod);
			lastKnownTimePeriod = timePeriod;
		}

		matchNumberBox.setText("#" + matchNum);

		setMatchTime();

	}

	private void setMatchTime() {
		if (lastKnownTimePeriod == TimePeriod.GAME) {
			long matchTimeCountUpwards = (Math.round((double) (System.currentTimeMillis() - matchStartTime) / 1000.0));
			matchTime = (Resources.MATCH_LENGTH_SECONDS) - matchTimeCountUpwards;
		}

		if (lastKnownTimePeriod == TimePeriod.INIT) {
			matchTimeBox.setText("" + Resources.MATCH_LENGTH_SECONDS);
		} else {
			matchTimeBox.setText("" + matchTime);
		}
	}

	private void enterNewPeriod(TimePeriod timePeriod) {
		if (timePeriod == TimePeriod.INIT) {
			matchNum++;
			matchTime = Resources.MATCH_LENGTH_SECONDS;
		} else if (timePeriod == TimePeriod.GAME) {
			matchStartTime = System.currentTimeMillis();
		}
	}

	private void updateMatchStateBox(TimePeriod timePeriod) {
		switch (timePeriod) {
			case DISABLED:
				matchStateBox.setText("Disabled");
				matchStateBox.setStyle(
						"-fx-background-color:red;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
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
			case ESTOPPED:
				matchStateBox.setText("E-STOPPED");
				matchStateBox.setStyle(
						"-fx-background-color:black;-fx-text-fill: white;-fx-font-size: 28;-fx-alignment: center;");
				break;
		}
	}

	public void updateGui() {
		matchStateBox.setMinWidth(this.getWidth() - (cornerBoxSize * 2));
	}

}
