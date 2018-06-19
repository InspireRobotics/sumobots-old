package org.inspirerobotics.sumobots.display.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.inspirerobotics.sumobots.display.gui.scenes.DebugScene;
import org.inspirerobotics.sumobots.display.gui.scenes.LogoScene;
import org.inspirerobotics.sumobots.display.gui.scenes.NoFieldScene;

import java.util.HashMap;
import java.util.Set;

public class SceneManager {

	private final HashMap<String, DisplayScene> scenes = new HashMap<>();

	private final Stage stage;
	private final DisplayScene noField;
	private final DisplayScene logoScene;
	private final DisplayScene debugScene;

	private boolean fullscreen = false;

	public SceneManager(Stage s) {
		stage = s;
		noField = new NoFieldScene();
		logoScene = new LogoScene();

		registerScenes(noField, logoScene);

		debugScene = new DebugScene(this);
	}

	private void registerScenes(DisplayScene... scenes) {
		for (DisplayScene scene : scenes) {
			this.scenes.put(scene.getName(), scene);
		}
	}

	public void showScene(String name) {
		showScene(scenes.getOrDefault(name, getNoFieldScene()));
	}

	public void showScene(Scene scene) {
		stage.setScene(scene);
		stage.show();
		stage.setFullScreen(fullscreen);
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

	public DisplayScene getDebugScene() {
		return debugScene;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
		stage.setFullScreen(fullscreen);
	}

	public boolean isFullscreen() {
		return fullscreen;
	}
}
