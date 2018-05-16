package org.inspirerobotics.sumobots.library.gui;

import org.junit.Rule;
import org.junit.Test;

public class FXMLFileLoaderTest {

	@Rule
	public JavaFXInitTestRule javafxRule = new JavaFXInitTestRule();

	@Test
	public void testLoadFile() {
		FXMLFileLoader.load("test.fxml", null);
	}

	@Test(expected = FXMLFileLoadException.class)
	public void testUnexistantFile() {
		FXMLFileLoader.load("foo.fxml", null);
	}

}
