package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.inspirerobotics.sumobots.field.FieldFrontend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

import java.util.List;
import java.util.logging.Logger;

public class ControlPane extends AnchorPane {

	private static final Logger logger = InternalLog.getLogger();

	private final FieldFrontend fieldFrontend;

	@FXML
	public HBox controlButtonPane;

	@FXML
	public Button initMatch;

	@FXML
	public Button startMatch;

	@FXML
	public Button endMatch;

	@FXML
	public TextArea controlConsole;

	public ControlPane(FieldFrontend fieldFrontend) {
		this.fieldFrontend = fieldFrontend;

		FXMLFileLoader.load("consolePane.fxml", this);
	}

	void update() {
		initMatch.setMinHeight(this.getHeight() - 10);
		startMatch.setMinHeight(this.getHeight() - 10);
		endMatch.setMinHeight(this.getHeight() - 10);

		controlConsole.setMinWidth(this.getWidth() - controlButtonPane.getWidth() - 20);
		controlConsole.setMaxWidth(this.getWidth() - controlButtonPane.getWidth() - 20);

		List<String> list = InternalLog.getInstance().getLogLines();
		StringBuilder sb = new StringBuilder();

		for (Object obj : list.toArray()) {
			String string = (String) obj;
			if (!string.contains("FINE") && !string.contains("FINER"))
				sb.append(string);
		}

		if (!sb.toString().equals(controlConsole.getText())) {
			controlConsole.setText(sb.toString());
			controlConsole.setScrollTop(Double.MAX_VALUE);
		}
	}

	@FXML
	public void startButtonPressed() {
		fieldFrontend.startMatch();
	}

	@FXML
	public void initButtonPressed() {
		fieldFrontend.initMatch();
	}

	@FXML
	public void endButtonPressed() {
		fieldFrontend.endMatch();
	}
}
