package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.scene.layout.AnchorPane;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;

public class DisplayPane extends AnchorPane {

    public DisplayPane() {
        FXMLFileLoader.load("displayPane.fxml", this);
    }
}
