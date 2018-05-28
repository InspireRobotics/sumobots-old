package org.inspirerobotics.sumobots.field.gui;

import org.junit.Assert;
import org.junit.Test;

public class ConsoleTabTest {

	@Test
	public void containsAnyNullInputTest() {
		Assert.assertFalse(ConsoleTab.containsAny(null, new String[] {}));
	}

	@Test
	public void containsAnyNullLevelToRemoveTest() {
		Assert.assertFalse(ConsoleTab.containsAny("Test", null));
	}

	@Test
	public void containsAnySimpleTest() {
		Assert.assertTrue(ConsoleTab.containsAny("FooBarFizzBuzz", new String[] { "Fizz" }));
	}

	@Test
	public void containsAnyContainsNoneTest() {
		Assert.assertFalse(ConsoleTab.containsAny("FooBarFizzBuzz", new String[] { "Fizzy" }));
	}

	@Test
	public void getLogLevelToRemoveSevereTest() {
		Assert.assertArrayEquals(new String[] { "WARNING:", "INFO:", "FINE:", "FINER:", "FINEST:" },
				ConsoleTab.getLevelsToRemove(ConsoleTab.GuiLogLevel.ERROR));
	}

	@Test
	public void getLogLevelToRemoveWarningTest() {
		Assert.assertArrayEquals(new String[] { "INFO:", "FINE:", "FINER:", "FINEST:" },
				ConsoleTab.getLevelsToRemove(ConsoleTab.GuiLogLevel.WARNING));
	}

	@Test
	public void getLogLevelToRemoveInfoTest() {
		Assert.assertArrayEquals(new String[] { "FINE:", "FINER:", "FINEST:" },
				ConsoleTab.getLevelsToRemove(ConsoleTab.GuiLogLevel.INFO));
	}

	@Test
	public void getLogLevelToRemoveDebugTest() {
		Assert.assertArrayEquals(new String[] { "FINER:", "FINEST:" },
				ConsoleTab.getLevelsToRemove(ConsoleTab.GuiLogLevel.DEBUG));
	}

	@Test
	public void getLogLevelToRemoveTraceTest() {
		Assert.assertArrayEquals(new String[] {}, ConsoleTab.getLevelsToRemove(ConsoleTab.GuiLogLevel.TRACE));
	}

}
