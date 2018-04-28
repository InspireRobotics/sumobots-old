package org.inspirerobotics.sumobots.library.concurrent;

public class InterThreadMessage {

	private final String name;

	private final Object data;
	
	public InterThreadMessage(String name) {
		super();
		this.name = name;
		this.data = null;
	}
	
	public InterThreadMessage(String name, Object data) {
		super();
		this.name = name;
		this.data = data;
	}

	public Object getData(){
		return data;
	}
	
	public String getName() {
		return name;
	}
	
}
