package org.inspirerobotics.sumobots.library.networking.message;

import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

public class ArchetypalMessageTest {

	@Test
	public void archetypalPingTest() {
		Message expected = new Message(MessageType.PING);
		Message actual = ArchetypalMessages.ping();

		Assert.assertEquals(expected.toString(), actual.toString());
	}

	@Test
	public void archetypalPongTest() {
		Message expected = new Message(MessageType.PONG);
		Message actual = ArchetypalMessages.pong();

		Assert.assertEquals(expected.toString(), actual.toString());
	}

	@Test
	public void archetypalTerminatedConnectionTest() {
		Message expected = new Message(MessageType.STREAM_TERMINATED);
		Message actual = ArchetypalMessages.terminatedConnection();

		Assert.assertEquals(expected.toString(), actual.toString());
	}

	@Test
	public void archetypalLibVersionTest() {
		Message expected = new Message(MessageType.LIB_VERSION);
		expected.addData("is_response", "" + true);
		expected.addData("version", Resources.LIBRARY_VERSION);

		Message actual = ArchetypalMessages.libraryVersion(true);

		Assert.assertEquals(expected.toString(), actual.toString());
	}

	@Test
	public void archetypalEnterNewMatchPeriodTest() {
		for (TimePeriod period : TimePeriod.values()) {
			Message actual = ArchetypalMessages.enterNewMatchPeriod(period);
			Message expected = new Message(MessageType.MATCH_STATE_UPDATE);
			expected.addData("new_period", period.getName());

			Assert.assertEquals(expected.toString(), actual.toString());
		}
	}

	@Test
	public void archetypalSetNameTest() {
		Message actual = ArchetypalMessages.setName("Foo");
		Message expected = new Message(MessageType.SET_NAME);
		expected.addData("name", "Foo");

		Assert.assertEquals(expected.toString(), actual.toString());
	}

}
