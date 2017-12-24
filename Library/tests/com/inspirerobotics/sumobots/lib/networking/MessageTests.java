package com.inspirerobotics.sumobots.lib.networking;

import org.junit.Test;

import com.inspirerobotics.sumobots.lib.Resources;

import static org.junit.Assert.assertEquals;

public class MessageTests {

	@Test
	public void twoDataFormatTest(){
		Message testMessage = new Message(MessageType.PONG);
		testMessage.addData("test", "value");
		testMessage.addData("foo", "bar");
		
		String data = testMessage.getFormatedData();
		String expected = "test" + Resources.US + "value" + Resources.EOB 
				+ "foo" + Resources.US + "bar" + Resources.EOB;
		assertEquals(expected, data);
	}
	
	@Test
	public void noDataFormatTest(){
		Message testMessage = new Message(MessageType.PONG);
	
		assertEquals("", testMessage.getFormatedData());
	}
	
}
