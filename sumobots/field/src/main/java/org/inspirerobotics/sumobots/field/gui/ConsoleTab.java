package org.inspirerobotics.sumobots.field.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.util.List;

public class ConsoleTab extends AnchorPane {

	public enum GuiLogLevel
	{
		ERROR, WARNING, INFO, DEBUG, TRACE
	}

	public GuiLogLevel currentLevel = GuiLogLevel.DEBUG;

	@FXML
	public Text levelLabel;

	@FXML
	public TextArea console;

	@FXML
	public Button errorButton, warningButton, infoButton, fineButton, finerButton;

	@FXML
	public Button archiveButton;

	private int currentLogLength = 0;

	public ConsoleTab() {
		FXMLFileLoader.load("log.fxml", this);

		setCurrentLevel(GuiLogLevel.DEBUG);
	}

	void update() {
		List<String> list = InternalLog.getInstance().getLogLines();

		if (list.size() != currentLogLength) {
			updateLog(list);
			currentLogLength = list.size();
		}
	}

	private void updateLog(List<String> list) {
		StringBuilder sb = new StringBuilder();

		String[] levelsToRemove = getLevelsToRemove(currentLevel);

		for (Object obj : list.toArray()) {
			String string = (String) obj;

			string = string.replace("org.inspirerobotics.sumobots.", "");

			if (!containsAny(string, levelsToRemove)){
				sb.append(string);
			}
		}

		console.setText(sb.toString());
		console.setScrollTop(Double.MAX_VALUE);
	}

	public static boolean containsAny(String string, String[] levelsToRemove) {
		if (levelsToRemove == null)
			return false;

		for (String s : levelsToRemove) {
			if (string.contains(s))
				return true;
		}
		return false;
	}

	public static String[] getLevelsToRemove(GuiLogLevel currentLevel) {
		// NOTE: We need the colons or "Fine" will pop if a string has "FINER"
		if (currentLevel == GuiLogLevel.ERROR) {
			return new String[] { "WARNING:", "INFO:", "FINE:", "FINER:", "FINEST:" };
		} else if (currentLevel == GuiLogLevel.WARNING) {
			return new String[] { "INFO:", "FINE:", "FINER:", "FINEST:" };
		} else if (currentLevel == GuiLogLevel.INFO) {
			return new String[] { "FINE:", "FINER:", "FINEST:" };
		} else if (currentLevel == GuiLogLevel.DEBUG) {
			return new String[] { "FINER:", "FINEST:" };
		}

		return new String[]{};
	}

	@FXML
	public void onErrorPressed() {
		setCurrentLevel(GuiLogLevel.ERROR);
	}

	@FXML
	public void onWarningPressed() {
		setCurrentLevel(GuiLogLevel.WARNING);
	}

	@FXML
	public void onInfoPressed() {
		setCurrentLevel(GuiLogLevel.INFO);
	}

	@FXML
	public void onDebugPressed() {
		setCurrentLevel(GuiLogLevel.DEBUG);
	}

	@FXML
	public void onTracePressed() {
		setCurrentLevel(GuiLogLevel.TRACE);
	}

	private void setCurrentLevel(GuiLogLevel currentLevel) {
		this.currentLevel = currentLevel;
		levelLabel.setText("Current Level: " + currentLevel.toString());
	}

	@FXML
	public void onArchivePressed() {
		InternalLog.getInstance().clear();
	}

}
