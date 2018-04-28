package org.inspirerobotics.sumobots.library;

import java.util.logging.Logger;

public enum TimePeriod {

	DISABLED("disabled"), 

	INIT("init"), 

	GAME("game"),

	ESTOPPED("estopped");
	
	private final String name;
	
	private TimePeriod(String name) {
		this.name = name;
	}
	

	public String getName() {
		return name;
	}

	public static TimePeriod fromString(String s){
		for (TimePeriod p : values()) {
			if(p.getName().equals(s)){
				return p;
			}
		}
		Logger.getLogger(Resources.LOGGER_NAME).warning("Unkown Time Period Found: " + s);
		return null;
	}
	
}
