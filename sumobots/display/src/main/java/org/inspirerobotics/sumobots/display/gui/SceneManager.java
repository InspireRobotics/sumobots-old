package org.inspirerobotics.sumobots.display.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

	private final Scene noField;
	private final Scene logoScene;

	public SceneManager() {
		noField = new NoFieldScene();
		logoScene = new LogoScene();
	}

	public void showScene(String name, Stage s) {
		if (name.equals("Logo")) {
			s.setScene(getLogoScene());
		} else if (name.equals("No Field Found")) {
			s.setScene(getNoFieldScene());
		}
	}

	public String[] getSceneNameArray() {
		return new String[] { "Logo", "No Field Found" };
	}

	public Scene getNoFieldScene() {
		return noField;
	}

	public Scene getLogoScene() {
		return logoScene;
	}
}
