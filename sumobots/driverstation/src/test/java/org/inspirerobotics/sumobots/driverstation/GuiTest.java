package org.inspirerobotics.sumobots.driverstation;

import org.inspirerobotics.sumobots.driverstation.gui.MainScene;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;
import org.inspirerobotics.sumobots.library.gui.JavaFXInitTestRule;
import org.junit.Rule;
import org.junit.Test;

public class GuiTest {

	@Rule
	public JavaFXInitTestRule javafxRule = new JavaFXInitTestRule();

	@Test
	public void testRootGroup() {
		new MainScene(null);
	}

	@Test(expected = RuntimeException.class)
	public void testUnexistantFile() {
		FXMLFileLoader.load("foo.fxml", null);
	}

}
