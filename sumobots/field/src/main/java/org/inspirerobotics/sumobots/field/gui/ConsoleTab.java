package org.inspirerobotics.sumobots.field.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.inspirerobotics.sumobots.library.InternalLog;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleTab extends AnchorPane {

	private enum GuiLogLevel {
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

		logger.fine("Loading log.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/log.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to load log.fxml", e);
		}
		
		archiveButton.setStyle("-fx-background-color:red");
		errorButton.setStyle("-fx-background-color:red");
		warningButton.setStyle("-fx-background-color:red");
		infoButton.setStyle("-fx-background-color:red");
		fineButton.setStyle("-fx-background-color:red");
		finerButton.setStyle("-fx-background-color:red");
	}

	/**
	 * Updates certain aspects of the GUI
	 */
	void update() {
		updateButtons();
		
		List<String> list = InternalLog.getInstance().getLogLines();
		StringBuilder sb = new StringBuilder();
	
		levelLabel.setText("Current Level: " + currentLevel.toString());
		
		String[] levelsToRemove = getLevelsToRemove();
		
		for (Object obj : list.toArray()) {
			String string = (String) obj;
			
			if(!containsAny(string, levelsToRemove))
				sb.append(string);
		}

		if (!sb.toString().equals(console.getText())) {
			console.setText(sb.toString());
			console.setScrollTop(Double.MAX_VALUE);
		}

	}

	private boolean containsAny(String string, String[] levelsToRemove) {
		if(levelsToRemove == null)
			return false;
		
		for (String s : levelsToRemove) {
			if(string.contains(s))
				return true;
		}
		return false;
	}

	private String[] getLevelsToRemove() {
		//NOTE: We need the colons or "Fine" will pop if a string has "FINER"
		if(currentLevel == GuiLogLevel.ERROR){
			return new String[]{"WARNING:", "INFO:", "FINE:", "FINER:"};
		}else if(currentLevel == GuiLogLevel.WARNING){
			return new String[]{"INFO:", "FINE:", "FINER:"};
		}else if(currentLevel == GuiLogLevel.INFO){
			return new String[]{"FINE:", "FINER:"};
		}else if(currentLevel == GuiLogLevel.DEBUG){
			return new String[]{"FINER:"};
		}
		return null;
	}

	private void updateButtons() {
		double buttonLength = ((controlBox.getWidth() - 20) / 7) - 6;
		double fontSize = buttonLength / 6 > 20 ? 20 : buttonLength / 6;
		Font font = Font.font(fontSize);

		archiveButton.setFont(font);
		errorButton.setFont(font);
		warningButton.setFont(font);
		infoButton.setFont(font);
		fineButton.setFont(font);
		finerButton.setFont(font);

		archiveButton.setMinWidth(buttonLength * 2);
		errorButton.setMinWidth(buttonLength);
		warningButton.setMinWidth(buttonLength);
		infoButton.setMinWidth(buttonLength);
		fineButton.setMinWidth(buttonLength);
		finerButton.setMinWidth(buttonLength);
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
		logger.warning("Log Archiving is currently unimplemented. All this currently does is clear the log");
	}

}
