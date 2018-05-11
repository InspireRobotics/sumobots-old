package org.inspirerobotics.sumobots.field;

import org.inspirerobotics.sumobots.field.gui.FXMLFileLoader;
import org.inspirerobotics.sumobots.field.gui.RootGroup;
import org.junit.Rule;
import org.junit.Test;

public class GuiTest {

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Test
    public void testRootGroup(){
        new RootGroup(null, false);
    }

    @Test(expected = RuntimeException.class)
    public void testUnexistantFile(){
        FXMLFileLoader.load("foo.fxml", null);
    }

}
