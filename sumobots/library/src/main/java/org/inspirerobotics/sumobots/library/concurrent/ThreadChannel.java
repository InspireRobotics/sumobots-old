package org.inspirerobotics.sumobots.library.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadChannel {
	
	/**
	 * The transmitter channel
	 */
	private final ConcurrentLinkedQueue<InterThreadMessage> tx;
	
	/**
	 * The receiving channel
	 */
	private final ConcurrentLinkedQueue<InterThreadMessage> rx ;
	
	/**
	 * Creates a new thread channel
	 */
	public ThreadChannel() {
		rx = new ConcurrentLinkedQueue<InterThreadMessage>();
		tx = new ConcurrentLinkedQueue<InterThreadMessage>();
	}
	
	/**
	 * Internal constructor for the {@link #createPair()} method
	 */
	private ThreadChannel(ConcurrentLinkedQueue<InterThreadMessage> tx, ConcurrentLinkedQueue<InterThreadMessage> rx) {
		super();
		this.tx = tx;
		this.rx = rx;
	}
	
	/**
	 * The receiving channel
	 */
	public ConcurrentLinkedQueue<InterThreadMessage> getRx() {
		return rx;
	}
	
	/**
	 * The transmitter channel
	 */
	public ConcurrentLinkedQueue<InterThreadMessage> getTx() {
		return tx;
	}
	
	/**
	 * @see ConcurrentLinkedQueue#poll()
	 */
	public InterThreadMessage poll(){
		return rx.poll();
	}
	
	/**
	 * @see ConcurrentLinkedQueue#add()
	 */
	public void add(InterThreadMessage m){
		tx.add(m);
	}

	/**
	 * Creates the pair for this channel
	 * @return creates a new channel with rx and tx switched
	 */
	public ThreadChannel createPair() {
		return new ThreadChannel(rx, tx);
	}
	
	
}
