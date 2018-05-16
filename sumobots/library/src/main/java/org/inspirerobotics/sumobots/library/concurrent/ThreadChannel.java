package org.inspirerobotics.sumobots.library.concurrent;

import org.inspirerobotics.sumobots.library.InternalLog;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ThreadChannel {

	private final Logger logger = InternalLog.getLogger();

	private final ConcurrentLinkedQueue<InterThreadMessage> tx;

	private final ConcurrentLinkedQueue<InterThreadMessage> rx;

	public ThreadChannel() {
		rx = new ConcurrentLinkedQueue<InterThreadMessage>();
		tx = new ConcurrentLinkedQueue<InterThreadMessage>();

		logger.fine("Created new ThreadChannel on thread: " + Thread.currentThread().getName());
	}

	private ThreadChannel(ConcurrentLinkedQueue<InterThreadMessage> tx, ConcurrentLinkedQueue<InterThreadMessage> rx) {
		super();
		this.tx = tx;
		this.rx = rx;
	}

	public ConcurrentLinkedQueue<InterThreadMessage> getRx() {
		return rx;
	}

	public ConcurrentLinkedQueue<InterThreadMessage> getTx() {
		return tx;
	}

	public InterThreadMessage poll() {
		InterThreadMessage message = rx.poll();

		if (message != null)
			logger.finer("Received inter-thread message " + message.getName());

		return message;
	}

	public void add(InterThreadMessage m) {
		tx.add(m);
	}

	public ThreadChannel createPair() {
		return new ThreadChannel(rx, tx);
	}

}
