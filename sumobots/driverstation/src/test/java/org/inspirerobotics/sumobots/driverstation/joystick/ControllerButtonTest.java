package org.inspirerobotics.sumobots.driverstation.joystick;

import org.junit.Assert;
import org.junit.Test;

public class ControllerButtonTest {

	@Test
	public void fromStringTest() throws UnknownControllerElementException {
		for (ControllerButton type : ControllerButton.values()) {
			Assert.assertEquals(type, ControllerButton.fromString(type.getId()));
		}
	}

	@Test(expected = UnknownControllerElementException.class)
	public void fromStringUnknownTest() throws UnknownControllerElementException {
		ControllerButton.fromString("Foo");
	}

}
