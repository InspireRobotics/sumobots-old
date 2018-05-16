package org.inspirerobotics.sumobots.library.networking.message;

import org.junit.Assert;
import org.junit.Test;

public class MessageTypeTest {

	@Test
	public void fromStringTest() throws UnknownMessageTypeException {
		for (MessageType type : MessageType.values()) {
			Assert.assertEquals(type, MessageType.fromString(type.getName()));
		}
	}

	@Test(expected = UnknownMessageTypeException.class)
	public void fromStringUnknownTest() throws UnknownMessageTypeException {
		MessageType.fromString("foo");
	}

}
