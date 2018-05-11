package org.inspirerobotics.sumobots.library.networking.tables;

import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

public class NetworkingTableTest {

	@Test
	public void toMessageTest() {
		Message message = generateTestMessage();
		NetworkTable networkTable = generateTestNetworkingTable();

		Assert.assertEquals(message.toJSONString(), networkTable.toMessage().toJSONString());
	}

	@Test
	public void updateFromTest() {
		NetworkTable networkTable = new NetworkTable();
		networkTable.updateFrom(generateTestMessage());

		Assert.assertEquals(generateTestMessage().toJSONString(), networkTable.toMessage().toJSONString());
	}

	private Message generateTestMessage() {
		Message m = new Message(MessageType.UPDATE_NTWK_TABLE);
		m.addData("Foo", "Bar");
		m.addData("Fizz", "Buzz");

		return m;
	}

	private NetworkTable generateTestNetworkingTable() {
		NetworkTable networkTable = new NetworkTable();
		networkTable.put("Foo", "Bar");
		networkTable.put("Fizz", "Buzz");

		return networkTable;
	}

}
