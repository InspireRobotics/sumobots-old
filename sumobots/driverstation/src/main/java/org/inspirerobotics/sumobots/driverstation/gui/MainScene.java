package org.inspirerobotics.sumobots.driverstation.gui;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.util.logging.Logger;

public class MainScene extends AnchorPane {

	private Logger log = InternalLog.getLogger();

	public MainScene(GuiController controller) {
		FXMLFileLoader.load("root.fxml", controller, this);

		controller.init();
		log.info("Main Scene initialized");
	}

	public Scene toScene() {
		AnchorPane ap = new AnchorPane();

		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);

		ap.getChildren().add(this);
		return new Scene(ap);
	}

}
