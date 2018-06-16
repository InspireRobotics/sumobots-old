package org.inspirerobotics.sumobots.display.gui;

import javafx.scene.Scene;

public class SceneManager {

	private final Scene noField;

	public SceneManager() {
		noField = new NoFieldScene();
	}

	public Scene getNoFieldScene() {
		return noField;
	}
}
