package org.inspirerobotics.sumobots.field.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.field.FieldFrontend;
import org.inspirerobotics.sumobots.field.gui.gametab.GameTab;
import org.inspirerobotics.sumobots.field.util.InternalLog;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RootGroup extends TabPane {

	private FieldFrontend fieldFrontend;

	private Logger log = InternalLog.getLogger();

	private GameTab gameTab;

	private ConsoleTab consoleTab;

	public RootGroup(FieldFrontend ff) {
		this.fieldFrontend = ff;
		
		log.fine("Loading main.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/root.fxml"));
		fxmlLoader.setController(this);
		
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to load root.fxml", e);
		}
		
		gameTab = new GameTab(fieldFrontend);
		consoleTab = new ConsoleTab();
	
		addTab(gameTab, "Game");
		addTab(consoleTab, "Console");
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				internalUpdate();
				Platform.runLater(this);
			}
			
		});
	}

	protected void internalUpdate() {
		int index = this.getSelectionModel().getSelectedIndex();
		if(index == 0) {
			gameTab.update();
		}else {
			consoleTab.update();
		}
	}

	public void addTab(AnchorPane pane, String tabName){
		log.fine("Adding tab \"" + tabName + "\" to the GUI");
		Tab tab = new Tab(tabName);
		tab.setClosable(false);
		tab.setContent(pane);
		
		this.getTabs().add(tab);
	}

	public Scene toScene() {
		AnchorPane p = new AnchorPane();
		
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
		
		p.getChildren().add(this);
		return new Scene(p);
	}
	
	public GameTab getGameTab() {
		return gameTab;
	}
	
	public ConsoleTab getConsoleTab() {
		return consoleTab;
	}
	
}
