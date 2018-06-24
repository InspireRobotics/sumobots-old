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
		updateLog();
	}

	private void updateLog() {
		List<String> list = InternalLog.getInstance().getLogLines();
		StringBuilder builder = logToStringBuffer(list);

		if (!builder.toString().equals(controlConsole.getText())) {
			controlConsole.setText(builder.toString());
			controlConsole.setScrollTop(Double.MAX_VALUE);
		}
	}

	private StringBuilder logToStringBuffer(List<String> list) {
		StringBuilder builder = new StringBuilder();

		for (Object obj : list.toArray()) {
			String string = (String) obj;
			if (!string.contains("FINE") && !string.contains("FINER"))
				builder.append(string.replace("org.inspirerobotics.sumobots.", ""));
		}

		return builder;
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
