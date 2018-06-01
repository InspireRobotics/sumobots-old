package org.inspirerobotics.sumobots.library.gui;

import org.junit.Test;

import static org.inspirerobotics.sumobots.library.gui.Alerts.traceToString;

public class AlertTraceToStringTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullExceptionTraceToStringFailsTest(){
        traceToString(null);
    }

}
