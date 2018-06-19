package org.inspirerobotics.sumobots.display.gui.scenes;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.display.gui.DisplayScene;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class LogoScene extends DisplayScene {

	public LogoScene() {
		super(generateGroup(), "Logo");
	}

	private static Parent generateGroup() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(new LogoPane());
		return anchorPane;
	}
}

class LogoPane extends AnchorPane {

	private static final String basePath = "imgs/logo";

	private final Image small, large;

	@FXML
	private ImageView imageView;

	public LogoPane() {
		FXMLFileLoader.load("logo.fxml", this);

		small = new Image(basePath + "Small.png");
		large = new Image(basePath + "Large.png");
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);

		if (width > 1000 && height > 1000) {
			imageView.setImage(large);
			imageView.setFitHeight(large.getHeight());
			imageView.setFitWidth(large.getWidth());
		} else {
			imageView.setImage(small);
			imageView.setFitHeight(small.getHeight());
			imageView.setFitWidth(small.getWidth());
		}
	}
}
