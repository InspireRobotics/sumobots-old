package org.inspirerobotics.sumobots.library.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadChannel {

	private final ConcurrentLinkedQueue<InterThreadMessage> tx;

	private final ConcurrentLinkedQueue<InterThreadMessage> rx ;

	public ThreadChannel() {
		rx = new ConcurrentLinkedQueue<InterThreadMessage>();
		tx = new ConcurrentLinkedQueue<InterThreadMessage>();
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
