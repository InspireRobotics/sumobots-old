package org.inspirerobotics.sumobots.library;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class InternalLog {

	private static InternalLog INSTANCE;

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	private final ArrayList<String> logLines = new ArrayList<String>();

	private InternalLog() {
		logger.setLevel(Level.FINE);
		
		logger.setUseParentHandlers(false);
		logger.addHandler(new Handler(){
			
			private final MessageFormat messageFormat = new MessageFormat("[{3,date,hh:mm:ss:SS} {2} {6}.{5}()] {1}: {4}\t {7}\n");
			
			@Override
			public void close() throws SecurityException {
				
			}

			@Override
			public void flush() {
				
			}

			@Override
			public void publish(LogRecord record) {
				Object[] arguments = new Object[8];
//		        arguments[0] = record.getLoggerName();
		        arguments[1] = record.getLevel();
		        arguments[2] = Thread.currentThread().getName();
		        arguments[3] = new Date(record.getMillis());
		        arguments[4] = record.getMessage();
		        arguments[5] = record.getSourceMethodName();
		        arguments[6] = record.getSourceClassName();
		        arguments[7] = record.getThrown();
		        if(arguments[7] == null)
		        	arguments[7] = "";
		        
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

	public void clear() {
		logLines.clear();
	}
}