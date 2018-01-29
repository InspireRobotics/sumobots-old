package com.inspirerobotics.sumobots.field.gui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.util.InternalLog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ConsoleTab extends AnchorPane {

	/**
	 * A equivalent enum of {@link java.util.logging.Level}. Used internally
	 * because the GUI has different names for each level
	 * 
	 * @author Noah
	 */
	private enum GuiLogLevel {
		ERROR, WARNING, INFO, DEBUG, TRACE
	}

	/**
	 * The console on the control panel
	 */
	public GuiLogLevel currentLevel = GuiLogLevel.DEBUG;

	/**
	 * The console on the control panel
	 */
	@FXML
	public Text levelLabel;

	/**
	 * The console on the control panel
	 */
	@FXML
	public TextArea console;

	/**
	 * The control panel for the tab. At the bottom of the screen
	 */
	@FXML
	public HBox controlBox;

	/**
	 * The buttons for setting the level of the console
	 */
	@FXML
	public Button errorButton, warningButton, infoButton, fineButton, finerButton;

	/**
	 * The buttons for archiving the current log
	 */
	@FXML
	public Button archiveButton;

	/**
	 * The log
	 */
	private Logger logger = InternalLog.getLogger();

	/**
	 * Creates a game tab and starts the game tab loop
	 * 
	 * @param ff
	 *            the varaible to the FieldFrontend. See {@link #fieldFrontend}
	 *            for its purpose
	 */
	public ConsoleTab() {

		// Creates the loader that loads the GUI
		logger.fine("Loading log.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/log.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);

		// Actually try to load the GUI
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to load log.fxml", e);
		}
		
		// Set all of the buttons to red
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
		
		// Update the console
		List<String> list = InternalLog.getInstance().getLogLines();
		StringBuilder sb = new StringBuilder();
	
		// Set the label of the current level
		levelLabel.setText("Current Level: " + currentLevel.toString());
		
		//Lets get the levels we need to remove from the log
		String[] levelsToRemove = getLevelsToRemove();
		
		// toArray() prevents ConcurrentModificationException
		for (Object obj : list.toArray()) {
			String string = (String) obj;
			
			//If the string has any levels we need to remove, don't add the line
			if(!containsAny(string, levelsToRemove))
				sb.append(string);
		}

		if (!sb.toString().equals(console.getText())) {
			console.setText(sb.toString());
			console.setScrollTop(Double.MAX_VALUE);
		}

	}
	
	/**
	 * If a string s has any of the strings in the array
	 * If levelsToRemove is true this will return false.
	 * 
	 * @param string the string to check
	 * @param levelsToRemove strings to check if they are in the string
	 */
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

		// Set the font sizes
		archiveButton.setFont(font);
		errorButton.setFont(font);
		warningButton.setFont(font);
		infoButton.setFont(font);
		fineButton.setFont(font);
		finerButton.setFont(font);

		// Sets the widths of the buttons
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
