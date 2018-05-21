package org.inspirerobotics.sumobots.library.networking.message;

import com.google.gson.JsonSyntaxException;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {

	@Test(expected = JsonSyntaxException.class)
	public void invalidJSONTest() {
		Message.fromString("GG");
	}

	@Test
	public void parseJSONWithNoMessageTypeTest() {
		Assert.assertEquals(Message.fromString("{}").getType(), MessageType.UNKNOWN);
	}

	@Test
	public void messageTypeFormatTest() {
		Message m = new Message(MessageType.PING);

		Assert.assertEquals("{\"message_type\":\"PING\"}", m.toJSONString());
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
