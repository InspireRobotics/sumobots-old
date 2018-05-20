package org.inspirerobotics.sumobots.field.gui;

import org.inspirerobotics.sumobots.field.gui.RootGroup;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoadException;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;
import org.inspirerobotics.sumobots.library.gui.JavaFXInitTestRule;
import org.junit.Rule;
import org.junit.Test;

public class GuiTest {

	@Rule
	public JavaFXInitTestRule javafxRule = new JavaFXInitTestRule();

	@Test
	public void testRootGroup() {
		new RootGroup(null, false);
	}

	@Test(expected = FXMLFileLoadException.class)
	public void testUnexistantFile() {
		FXMLFileLoader.load("foo.fxml", null);
	}

}
