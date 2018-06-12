package org.inspirerobotics.sumobots.library.networking.connection;

import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkingTableTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InternalMessageHandlingTest {

	private TestConnection testConnection;

	@Before
	public void setupTest() {
		testConnection = new TestConnection();
	}

	@Test
	public void pingTest() throws InterruptedException {
		testConnection.ping();

		Thread.sleep(26);

		testConnection.onMessageReceived(ArchetypalMessages.pong());

		Assert.assertTrue("Incorrect Ping (" + testConnection.getCurrentPing() + ") should be greater than 25",
				testConnection.getCurrentPing() > 25);
	}

	@Test
	public void pingResponseTest() {
		testConnection.handleTestMessage(ArchetypalMessages.ping());

		Assert.assertEquals(MessageType.PONG, testConnection.getLastSentMessage().getType());
	}

	@Test
	public void setNameTest() {
		testConnection.handleTestMessage(ArchetypalMessages.setName("Foo"));

		Assert.assertEquals("Foo", testConnection.getConnectionName());
	}

	@Test
	public void libraryVersionNonResponseTest() {
		testConnection.handleTestMessage(ArchetypalMessages.libraryVersion(false));
		Message m = testConnection.getLastSentMessage();

		Assert.assertEquals(m.getType(), MessageType.LIB_VERSION);
		Assert.assertEquals("true", m.getData("is_response"));
	}

	@Test
	public void libraryVersionResponseTest() {
		testConnection.handleTestMessage(ArchetypalMessages.libraryVersion(true));
		Message m = testConnection.getLastSentMessage();

		Assert.assertNull(m);
	}

	@Test
	public void sendNetworkTableUpdateTest() {
		testConnection.setBindedTable(NetworkingTableTest.generateTestNetworkingTable());
		testConnection.sendNetworkTableUpdates();
		Message m = testConnection.getLastSentMessage();

		Assert.assertEquals(MessageType.UPDATE_NTWK_TABLE, m.getType());
		Assert.assertEquals("Bar", m.getData("Foo"));
		Assert.assertEquals("Buzz", m.getData("Fizz"));
	}

	@Test
	public void networkTableUpdateReceivedTest() {
		testConnection.handleTestMessage(NetworkingTableTest.generateTestMessage());

		Assert.assertEquals("Bar", testConnection.getTable().get("Foo"));
		Assert.assertEquals("Buzz", testConnection.getTable().get("Fizz"));
	}

}
