package org.inspirerobotics.sumobots.library.concurrent;

import org.junit.Assert;
import org.junit.Test;

import static java.lang.Thread.sleep;

public class ThreadChannelTest {

	@Test
	public void pollTest() {
		ThreadChannel original = new ThreadChannel();
		String foo = "FooBarFizzBuzz";

		original.createPair().add(new InterThreadMessage(foo));

		Assert.assertSame(foo, original.poll().getName());
		Assert.assertNull(original.poll());
	}

	@Test
	public void createPairTest() {
		ThreadChannel original = new ThreadChannel();
		ThreadChannel pair = original.createPair();

		Assert.assertSame(original.getRx(), pair.getTx());
		Assert.assertSame(pair.getRx(), original.getTx());
	}

	@Test
	public void threadChannelTest() {
		ThreadChannel tc = new ThreadChannel();
		TestThread testThread = new TestThread(tc.createPair(), "Foo");
		testThread.start();

		// Give time for the thread to start
		tc.add(new InterThreadMessage("ThreadNameRequest"));
		sleepCatch(20);

		InterThreadMessage m = tc.poll();
		testThread.interrupt();

		Assert.assertNotNull(m);
		Assert.assertEquals(m.getName(), "Foo");
	}

	private void sleepCatch(long milli) {
		try {
			sleep(milli);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
