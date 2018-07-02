package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.inspirerobotics.sumobots.display.gui.DisplayScene;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class GameScene extends DisplayScene {

	private static final GamePane gamePane = new GamePane();

	private static long startTime = 0;
	private static long clockTime = -1;

	public GameScene() {
		super(generateGroup(), "Game");
	}

	private static Parent generateGroup() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(gamePane);
		return anchorPane;
	}

	public static void setTeams(String[] teams) {
		gamePane.setTeams(teams);
	}

	public static void updateClock() {
		long matchTimeCountUpwards = (Math.round((double) (System.currentTimeMillis() - startTime) / 1000.0));
		clockTime = (Resources.MATCH_LENGTH_SECONDS) - matchTimeCountUpwards;

		gamePane.setTime((int) clockTime);
	}

	public static void resetClock() {
		startTime = System.currentTimeMillis();
		gamePane.setTime(Resources.MATCH_LENGTH_SECONDS);
	}
}

class GamePane extends AnchorPane {

	@FXML
	public VBox teamVBox;

	@FXML
	public Label timeLabel;

	GamePane() {
		FXMLFileLoader.load("game.fxml", this);
		setTeams(new String[0]);
	}

	public void setTeams(String[] teams) {
		InternalLog.getLogger().finer("Teams: " + Arrays.toString(teams));
		teamVBox.getChildren().clear();

		if (teams.length == 0) {
			teamVBox.getChildren().add(new TeamPane("No Teams Playing!"));
			return;
		}

		for (int i = 0; i < teams.length; i++) {
			teamVBox.getChildren().add(new TeamPane(teams[i]));
		}
	}

	public void setTime(int time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("m:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		Date date = new Date(time * 1000);
		timeLabel.setText(dateFormat.format(date));
	}
}

class TeamPane extends AnchorPane {

	private Label label;

	TeamPane(String title) {
		VBox.setVgrow(this, Priority.ALWAYS);
		label = new Label("");
		setText(title);
		label.setId("teamLabel");
		label.setAlignment(Pos.CENTER);

		this.getChildren().add(label);

		AnchorPane.setTopAnchor(label, 0.0);
		AnchorPane.setLeftAnchor(label, 0.0);
		AnchorPane.setBottomAnchor(label, 0.0);
		AnchorPane.setRightAnchor(label, 0.0);
	}

	void setText(String s) {
		StringBuilder style = new StringBuilder();

		style.append(applyStyleFromName(s));

		if (s.contains("eliminated")) {
			label.setText("ELIMINATED");
			style.append("-fx-text-fill:yellow;");
		} else {
			label.setText(s);
		}

		label.setStyle(style.toString());
	}

	private String applyStyleFromName(String s) {
		s = s.toLowerCase();

		if (s.contains("red")) {
			return "-fx-background-color:red;";
		} else if (s.contains("purple")) {
			return "-fx-background-color:purple;";
		} else if (s.contains("green")) {
			return "-fx-background-color:green;";
		} else if (s.contains("orange")) {
			return "-fx-background-color:orange;";
		} else if (s.contains("blue")) {
			return "-fx-background-color:#1621c1;";
		}
		return "";
	}

	public Label getLabel() {
		return label;
	}
}
