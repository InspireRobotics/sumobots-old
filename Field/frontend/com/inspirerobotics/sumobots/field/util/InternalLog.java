package com.inspirerobotics.sumobots.field.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;

/**
 * Represents the internal log of the application
 *
 * Levels of Logging:
 * <p>
 * Severe
 * <p>
 * Warning
 * <p>
 * Info
 * <p>
 * Debug (Fine in terms of java.util.logging)
 * <p>
 * Trace (Finer in terms of java.util.logging)
 *
 * @author Noah
 */
public class InternalLog {

	/**
	 * The Instance of the Log (For this thread)
	 */
	private static InternalLog INSTANCE;

	/**
	 * The currents lines in the log
	 */
	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);
	
	/**
	 * This holds all of the lines to the internal log. This only includes logs
	 * with the level info or above
	 */
	private final ArrayList<String> logLines = new ArrayList<String>();

	private InternalLog() {
		logger.setLevel(Level.ALL);
		
		//Set custom logging format
		logger.setUseParentHandlers(false);
		logger.addHandler(new Handler(){
			
			private final MessageFormat messageFormat = new MessageFormat("[{3,date,hh:mm:ss:SS} {2} {6}.{5}()] {1}: {4} \n");
			
			@Override
			public void close() throws SecurityException {
				
			}

			@Override
			public void flush() {
				
			}

			@Override
			public void publish(LogRecord record) {
				Object[] arguments = new Object[7];
//		        arguments[0] = record.getLoggerName();
		        arguments[1] = record.getLevel();
		        arguments[2] = Thread.currentThread().getName();
		        arguments[3] = new Date(record.getMillis());
		        arguments[4] = record.getMessage();
		        arguments[5] = record.getSourceMethodName();
		        arguments[6] = record.getSourceClassName();
		        
		        String format = messageFormat.format(arguments);
		        logLines.add(format);
		        
		        System.out.print(format);
			}
			
		});
	}

	public static Logger getLogger() {
		return getInstance().logger;
	}

	public ArrayList<String> getLogLines() {
		return logLines;
	}
	
	public static InternalLog getInstance() {
		if (INSTANCE == null)
			INSTANCE = new InternalLog();
		return INSTANCE;
	}

	/**
	 * Clears the internal version of the log
	 */
	public void clear() {
		logLines.clear();
	}
}