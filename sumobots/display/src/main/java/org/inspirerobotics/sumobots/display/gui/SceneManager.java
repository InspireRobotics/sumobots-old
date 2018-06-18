package org.inspirerobotics.sumobots.display.gui;

import javafx.scene.Scene;

public class SceneManager {

	private final Scene noField;
	private final Scene logoScene;

	public SceneManager() {
		noField = new NoFieldScene();
		logoScene = new LogoScene();
	}

	public Scene getNoFieldScene() {
		return noField;
	}

	public Scene getLogoScene() {
		return logoScene;
	}
}
