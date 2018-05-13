package org.inspirerobotics.sumobots.library.concurrent;

public class TestThread extends Thread {

	private ThreadChannel threadChannel;
	private InterThreadMessage message;

	TestThread(ThreadChannel tc, String name) {
		this.threadChannel = tc;

		this.setName(name);
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			InterThreadMessage m = threadChannel.poll();

			if (m == null) {
				sleepNoCatch(1);
				continue;
			}

			parseMessage(m);
		}
	}

	private void parseMessage(InterThreadMessage m) {
		if (m.getName().equals("ThreadNameRequest")) {
			threadChannel.add(new InterThreadMessage(Thread.currentThread().getName()));
		}
	}

	private void sleepNoCatch(long milli) {
		try {
			sleep(milli);
		} catch (InterruptedException e) {
		}
	}
}
