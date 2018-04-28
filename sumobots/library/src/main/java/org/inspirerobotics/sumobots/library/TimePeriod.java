package org.inspirerobotics.sumobots.library;

import java.util.logging.Logger;

/**
 * The time period for the FMS/Robot. Note that the FMS might be in a different time period than a robot. 
 * For instance, if a robot was disabled but the match is still active, the FMS would be in {@link TimePeriod#GAME} whereas
 * the robot would be in {@link TimePeriod#DISABLED}
 * @author Noah
 *
 */
public enum TimePeriod {
	
	/**
	 * When nothing is happening or are in a state of long-term rest
	 */
	DISABLED("disabled"), 
	/**
	 * When we are ready to start but still waiting for the game to start
	 */
	INIT("init"), 
	/**
	 * During the game
	 */
	GAME("game"),
	/*
	 * Emergency Stopped. Used for rare circumstances
	 */
	ESTOPPED("estopped");
	
	private final String name;
	
	private TimePeriod(String name) {
		this.name = name;
	}
	
	/**
	 * The name of the time period
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the TimePeriod with the name passed
	 * @return the time period with the name passed. Will return null if none are found
	 */
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
