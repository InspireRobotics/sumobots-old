package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.inspirerobotics.sumobots.display.gui.DisplayScene;
import org.inspirerobotics.sumobots.display.gui.SceneManager;

public class DebugScene extends DisplayScene {

	public DebugScene(SceneManager sceneManager) {
		super(generateGroup(sceneManager), "Debug Scene");
	}

	private static Parent generateGroup(SceneManager sceneManager) {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(new DebugPane(sceneManager));
		return anchorPane;
	}
}

class DebugPane extends VBox {

	private final SceneManager sceneManager;

	public DebugPane(SceneManager sceneManager) {
		this.sceneManager = sceneManager;

		this.setSpacing(10);
		this.setMinSize(800, 600);

		addTitle();
		addSceneButtons(sceneManager.getSceneNameArray());
	}

	private void addTitle() {
		Label label = new Label("Debug Screen");
		label.setStyle("-fx-font-size:20px");

		this.getChildren().add(label);
	}

	public void addSceneButtons(String[] scenes) {
		for (String scene : scenes) {
			Button button = new Button(scene);
			button.setOnAction(event -> {
				sceneManager.showScene(button.getText());
			});

			this.getChildren().add(button);
		}
	}

}
