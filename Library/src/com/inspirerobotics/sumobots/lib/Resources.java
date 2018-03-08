package com.inspirerobotics.sumobots.lib;

/**
 * Global Variables for the Standard Sumobot Library
 * 
 * @author Noah
 */
public class Resources {
	
	/**
	 * The Library Version
	 * 
	 * <p><b>1st Number</b>: the major version
	 * <p><b>2nd Number</b>: the minor version
	 * <p><b>3rd Number</b>: the revision number
	 */
	public static final String LIBRARY_VERSION = "0.2.0";
	
	/**
	 * The name of the logger used throughout the library
	 */
	public static final String LOGGER_NAME = "com.inspirerobotics.sumobots";
	
	/*
	 * Server Stuff 
	 */
	
	/**
	 * The port for the server
	 */
	public static final int SERVER_PORT = 4283;
	
	/**
	 * The port for the driver station
	 */
	public static final int DS_PORT = 10489;
	
	/*
	 * Communication Protocal Stuff
	 */
	
	/**End of Transmission*/
	public static final String EOT = String.valueOf((char) 4);
	/**End of Transmission Block*/
	public static final String EOB = String.valueOf((char) 23);
	/**Unit Seperator*/
	public static final String US = String.valueOf((char) 31);
	
	/*
	 * Misc Stuff
	 */
	
	/**
	 * The timeout time for all Sockets
	 */
	public static final int SOCKET_TIMEOUT = 1;
	
}
