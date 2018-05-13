package org.inspirerobotics.sumobots.library;

import org.junit.Assert;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class InternalLogTest {

    @Test
    public void getLoggerNotNullTest(){
        InternalLog.getInstance().reset();

        Assert.assertNotNull(InternalLog.getLogger());
    }

    @Test
    public void logLinesSimpleTest(){
        Logger logger = InternalLog.getLogger();
        logger.info("Test");

        Assert.assertTrue("Logger was empty after a log was made!", !InternalLog.getInstance().getLogLines().isEmpty());
    }


    @Test
    public void logLinesClearTest(){
        Logger logger = InternalLog.getLogger();
        logger.info("Test");
        InternalLog.getInstance().clear();

        Assert.assertTrue("After a clear the internal log still wasn't empty!!", InternalLog.getInstance().getLogLines().isEmpty());
    }

    @Test
    public void getInstanceNotNullTest(){
        InternalLog.getInstance().reset();

        Assert.assertNotNull(InternalLog.getInstance());
    }

    @Test
    public void loggerResetTest(){
        InternalLog.getInstance().reset();

        Assert.assertNull(InternalLog.getInstanceWithoutNullCheck());
        Assert.assertTrue("After reset the internal log wasn't empty!!", InternalLog.getInstance().getLogLines().isEmpty());
    }

    @Test
    public void logFormatTest(){
        LogRecord log = new LogRecord(Level.INFO, "Foo");
        log.setLoggerName(Resources.LOGGER_NAME);
        log.setSourceClassName("Class");
        log.setSourceMethodName("Method");

        String formattedString = InternalLog.format(log);
        String expected = createExpectedLog();

        Assert.assertEquals(expected, formattedString);
    }

    private String createExpectedLog() {
        MessageFormat messageFormat = new MessageFormat(
                "[{3,date,hh:mm:ss:SS} {2} {6}.{5}()] {1}: {4}\t {7}\n");

        Object[] arguments = new Object[8];
        arguments[1] = "INFO";
        arguments[2] = Thread.currentThread().getName();
        arguments[3] = new Date(System.currentTimeMillis());
        arguments[4] = "Foo";
        arguments[5] = "Method";
        arguments[6] = "Class";
        arguments[7] = "";

        return messageFormat.format(arguments);
    }

}
