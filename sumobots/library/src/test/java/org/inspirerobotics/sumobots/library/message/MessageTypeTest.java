package org.inspirerobotics.sumobots.library.message;

import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

public class MessageTypeTest {

	@Test
	public void fromStringTest() {
		for (MessageType type : MessageType.values()) {
			Assert.assertEquals(type, MessageType.fromString(type.getName()));
		}
	}

}
