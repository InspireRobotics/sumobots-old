package org.inspirerobotics.sumobots.display.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class DisplayScene extends Scene {

	private final String name;

	protected DisplayScene(Parent root, String name) {
		super(root);

		this.name = name;
	}

	public void onDisplayed() {
	}

	public String getName() {
		return name;
	}
}
