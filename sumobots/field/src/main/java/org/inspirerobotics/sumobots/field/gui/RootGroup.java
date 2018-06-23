package org.inspirerobotics.sumobots.field.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.field.FieldFrontend;
import org.inspirerobotics.sumobots.field.gui.gametab.GameTab;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.util.logging.Logger;

public class RootGroup extends TabPane {

	private FieldFrontend fieldFrontend;

	private Logger log = InternalLog.getLogger();

	private GameTab gameTab;

	private ConsoleTab consoleTab;

	private AboutTab aboutTab;

	public RootGroup(FieldFrontend ff, boolean startLoop) {
		this.fieldFrontend = ff;

		FXMLFileLoader.load("root.fxml", this, null);

		gameTab = new GameTab(fieldFrontend);
		consoleTab = new ConsoleTab();
		aboutTab = new AboutTab();

		addTab(gameTab, "Game");
		addTab(consoleTab, "Console");
		addTab(aboutTab, "About");

		if (startLoop)
			startUpdateLoop();
	}

	public void startUpdateLoop() {
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
		if (index == 0) {
			gameTab.update();
		} else {
			consoleTab.update();
		}
	}

	public void addTab(AnchorPane pane, String tabName) {
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
