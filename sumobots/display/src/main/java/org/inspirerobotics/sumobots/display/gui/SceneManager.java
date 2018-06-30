package org.inspirerobotics.sumobots.display.gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.inspirerobotics.sumobots.display.gui.scenes.*;

import java.util.HashMap;
import java.util.Set;

public class SceneManager {

	private final HashMap<String, DisplayScene> scenes = new HashMap<>();

	private final Stage stage;
	private final DisplayScene noField;
	private final DisplayScene logoScene;
	private final DisplayScene debugScene;
	private final DisplayScene gameScene;
	private final DisplayScene fieldResetScene;
	private final DisplayScene eStopScene;

	private boolean fullscreen = false;

	public SceneManager(Stage s) {
		stage = s;
		noField = new NoFieldScene();
		logoScene = new LogoScene();
		gameScene = new GameScene();
		fieldResetScene = new FieldResetScene();
		eStopScene = new EStopScene();

		registerScenes(noField, logoScene, gameScene, fieldResetScene, eStopScene);

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

		if (fullscreen) {
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			stage.setWidth(bounds.getWidth());
			stage.setHeight(bounds.getHeight());
		}

		stage.show();
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

	public DisplayScene getFieldResetScene() {
		return fieldResetScene;
	}

	public DisplayScene getGameScene() {
		return gameScene;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;

		if (fullscreen) {
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			stage.setWidth(bounds.getWidth());
			stage.setHeight(bounds.getHeight());
		}
	}

	public DisplayScene getEStopScene() {
		return eStopScene;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}
}
