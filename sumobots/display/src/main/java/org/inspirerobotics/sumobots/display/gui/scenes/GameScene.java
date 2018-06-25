package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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

	private static int clockTime = -1;
	private static long lastClockUpdate;

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
		if (lastClockUpdate + 1000 < System.currentTimeMillis()) {

			if (clockTime > 0)
				clockTime--;

			gamePane.setTime(clockTime);
			lastClockUpdate = System.currentTimeMillis();
		}
	}

	public static void resetClock() {
		clockTime = Resources.MATCH_LENGTH_SECONDS;
		lastClockUpdate = System.currentTimeMillis() - 250;// -250 counteracts delay of the field message getting here
		gamePane.setTime(clockTime);
	}
}

class GamePane extends AnchorPane {

	@FXML
	private GridPane gridPane;

	@FXML
	private Label timeLabel;

	GamePane() {
		FXMLFileLoader.load("game.fxml", this);

		addTeamPanes();
	}

	private void addTeamPanes() {
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				gridPane.add(new TeamPane(), x, y);
			}
		}
	}

	public void setTeams(String[] teams) {
		for (Node node : gridPane.getChildren()) {
			TeamPane t = (TeamPane) node;
			t.setText("No Team!");
		}

		InternalLog.getLogger().finer("Teams: " + Arrays.toString(teams));

		for (int i = 0; i < teams.length; i++) {
			getTeamPaneAt(i < 2 ? 0 : 1, i % 2).setText(teams[i]);
		}
	}

	public TeamPane getTeamPaneAt(int row, int column) {
		ObservableList<Node> children = gridPane.getChildren();

		for (Node node : children.toArray(new Node[children.size()])) {
			if (gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
				return (TeamPane) node;
			}
		}

		return null;
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

	TeamPane() {
		label = new Label("No Team!");
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
