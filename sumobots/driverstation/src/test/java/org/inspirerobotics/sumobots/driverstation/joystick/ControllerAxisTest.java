package org.inspirerobotics.sumobots.driverstation.joystick;

import org.junit.Assert;
import org.junit.Test;

public class ControllerAxisTest {

	@Test
	public void fromStringTest() throws UnknownControllerElementException {
		for (ControllerAxis type : ControllerAxis.values()) {
			Assert.assertEquals(type, ControllerAxis.fromString(type.getId()));
		}
	}

	@Test(expected = UnknownControllerElementException.class)
	public void fromStringUnknownTest() throws UnknownControllerElementException {
		ControllerAxis.fromString("Foo");
	}

}
