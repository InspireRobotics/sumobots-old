package org.inspirerobotics.sumobots.display.gui;

import javafx.stage.Stage;
import org.inspirerobotics.sumobots.display.gui.scenes.LogoScene;
import org.inspirerobotics.sumobots.display.gui.scenes.NoFieldScene;

import java.util.HashMap;
import java.util.Set;

public class SceneManager {

	private final HashMap<String, DisplayScene> scenes = new HashMap<>();

	private final DisplayScene noField;
	private final DisplayScene logoScene;

	public SceneManager() {
		noField = new NoFieldScene();
		logoScene = new LogoScene();

		registerScenes(noField, logoScene);
	}

	private void registerScenes(DisplayScene... scenes) {
		for (DisplayScene scene : scenes) {
			this.scenes.put(scene.getName(), scene);
		}
	}

	public void showScene(String name, Stage s) {
		s.setScene(scenes.getOrDefault(name, getNoFieldScene()));
	}

	public String[] getSceneNameArray() {
		Set<String> names = scenes.keySet();

		return names.toArray(new String[names.size()]);
	}

	public DisplayScene getNoFieldScene() {
		return noField;
	}

	public DisplayScene getLogoScene() {
		return logoScene;
	}
}
