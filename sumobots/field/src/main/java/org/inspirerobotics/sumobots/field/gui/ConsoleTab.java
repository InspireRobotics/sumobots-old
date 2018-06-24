package org.inspirerobotics.sumobots.field.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.util.List;
import java.util.logging.Logger;

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
	public HBox controlBox;

	@FXML
	public Button errorButton, warningButton, infoButton, fineButton, finerButton;

	@FXML
	public Button archiveButton;

	private Logger logger = InternalLog.getLogger();

	public ConsoleTab() {
		FXMLFileLoader.load("log.fxml", this);
	}

	void update() {
		List<String> list = InternalLog.getInstance().getLogLines();
		StringBuilder sb = new StringBuilder();

		levelLabel.setText("Current Level: " + currentLevel.toString());

		String[] levelsToRemove = getLevelsToRemove(currentLevel);

		for (Object obj : list.toArray()) {
			String string = (String) obj;

			if (!containsAny(string, levelsToRemove))
				sb.append(string);
		}

		if (!sb.toString().equals(console.getText())) {
			console.setText(sb.toString());
			console.setScrollTop(Double.MAX_VALUE);
		}

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
		currentLevel = GuiLogLevel.ERROR;
	}

	@FXML
	public void onWarningPressed() {
		currentLevel = GuiLogLevel.WARNING;
	}

	@FXML
	public void onInfoPressed() {
		currentLevel = GuiLogLevel.INFO;
	}

	@FXML
	public void onDebugPressed() {
		currentLevel = GuiLogLevel.DEBUG;
	}

	@FXML
	public void onTracePressed() {
		currentLevel = GuiLogLevel.TRACE;
	}

	@FXML
	public void onArchivePressed() {
		InternalLog.getInstance().clear();
	}

}
