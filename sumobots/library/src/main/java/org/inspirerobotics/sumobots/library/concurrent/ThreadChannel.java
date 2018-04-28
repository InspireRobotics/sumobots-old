package org.inspirerobotics.sumobots.library.concurrent;

import org.inspirerobotics.sumobots.library.Resources;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ThreadChannel {

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	private final ConcurrentLinkedQueue<InterThreadMessage> tx;

	private final ConcurrentLinkedQueue<InterThreadMessage> rx ;

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

	public InterThreadMessage poll(){
		return rx.poll();
	}

	public void add(InterThreadMessage m){
		tx.add(m);
	}

	public ThreadChannel createPair() {
		return new ThreadChannel(rx, tx);
	}
	
	
}
