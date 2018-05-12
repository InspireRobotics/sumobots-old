package org.inspirerobotics.sumobots.library.message;

import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {

	@Test
	public void messageTypeFormatTest() {
		for (MessageType type : MessageType.values()) {
			Message original = new Message(type);
			Message parsed = Message.fromString(original.toJSONString());

			Assert.assertEquals(original.getType(), parsed.getType());
		}
	}

	@Test
	public void messageDataTest() {
		Message original = new Message(MessageType.LIB_VERSION);

		original.addData("Foo", "Bar");
		original.addData("Pi", "3.14");
		original.addData("" + Integer.MAX_VALUE, "" + Long.MAX_VALUE);

		Message parsed = Message.fromString(original.toJSONString());

		Assert.assertEquals(original.getData("Foo"), parsed.getData("Foo"));
		Assert.assertEquals(original.getData("Pi"), parsed.getData("Pi"));
		Assert.assertEquals(original.getData("" + Integer.MAX_VALUE), parsed.getData("" + Integer.MAX_VALUE));
		Assert.assertNull(original.getData("NullValue"));
	}

	@Test
	public void toStringTest() {
		Message message = new Message(MessageType.LIB_VERSION);
		message.addData("Foo", "Bar");

		Assert.assertEquals(MessageType.LIB_VERSION.getName() + " Data: " + message.getDataSet(), message.toString());
	}
}
