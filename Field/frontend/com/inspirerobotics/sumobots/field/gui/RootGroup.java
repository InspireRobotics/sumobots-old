package com.inspirerobotics.sumobots.field.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.FieldFrontend;
import com.inspirerobotics.sumobots.field.util.InternalLog;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * The root of the GUI This will hold all of the key elements
 * 
 * @author Noah
 *
 *
 */
public class RootGroup extends TabPane {
	
	/**
	 * A Reference to the FieldFrontend
	 */
	private FieldFrontend fieldFrontend;
	
	/**
	 * The Log
	 */
	private Logger log = InternalLog.getLogger();
	
	/**
	 * the Game Tab
	 */
	private GameTab gameTab;
	
	/**
	 * the Console Tab
	 */
	private ConsoleTab consoleTab;
	
	/**
	 * Creates a new root group. This will load 
	 * all of the GUIs tabs and components
	 * @param ff
	 */
	public RootGroup(FieldFrontend ff) {
		this.fieldFrontend = ff;
		
		//Setup the loader to load the GUI
		log.fine("Loading main.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/root.fxml"));
		fxmlLoader.setController(this);
		
		//Actually load the GUI
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to load root.fxml", e);
		}
		
		//Create the tabs
		gameTab = new GameTab(fieldFrontend);
		consoleTab = new ConsoleTab();
	
		//Add of the tabs to the GUI
		addTab(gameTab, "Game");
		addTab(consoleTab, "Console");
		
		
		//Start internal loop
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				internalUpdate();
				Platform.runLater(this);
			}
			
		});
	}
	
	/**
	 * Updates the tab that is currently open
	 */
	protected void internalUpdate() {
		int index = this.getSelectionModel().getSelectedIndex();
		if(index == 0) {
			gameTab.update();
		}else {
			consoleTab.update();
		}
	}

	/**
	 * Adds a tab to the GUI
	 * 
	 * @param pane the pane to add to the tab once its created
	 * @param tabName the name that will show up on the gui
	 */
	public void addTab(AnchorPane pane, String tabName){
		log.fine("Adding tab \"" + tabName + "\" to the GUI");
		Tab tab = new Tab(tabName);
		tab.setClosable(false);
		tab.setContent(pane);
		
		this.getTabs().add(tab);
	}

	/**
	 * @return a scene with this as the root
	 */
	public Scene toScene() {
		return new Scene(this);
	}
	
	public GameTab getGameTab() {
		return gameTab;
	}
	
	public ConsoleTab getConsoleTab() {
		return consoleTab;
	}
	
}
