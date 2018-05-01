package org.inspirerobotics.sumobots.library;

import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArchetypalMessageTest {

    @Test
    public void libraryVersionTest(){
        Message m = ArchetypalMessages.libraryVersion(false);

        assertEquals(Boolean.parseBoolean((String) m.getData("is_response")), false);
        assertEquals(m.getData("version"), Resources.LIBRARY_VERSION);
    }

    @Test
    public void enterNewMatchPeriodTest(){
        for (TimePeriod t : TimePeriod.values()){
            Message m = ArchetypalMessages.enterNewMatchPeriod(t);

            assertEquals(m.getData("new_period"), t.getName());
        }
    }

    @Test
    public void setNameTest(){
        Message m = ArchetypalMessages.setName("FooBar");

        assertEquals(m.getData("name"), "FooBar");
    }

}
