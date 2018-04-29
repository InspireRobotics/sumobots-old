package org.inspirerobotics.sumobots.library;

import me.grison.jtoml.impl.Toml;
import org.inspirerobotics.sumobots.library.config.Config;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    private static final Config testConfig = new Config(createTestToml());

    private static Toml createTestToml(){
       return Toml.parse("bigNum = 1234567\nfoo = \"bar\"");
    }

    @Test
    public void configStringTest(){
        assertEquals("bar", testConfig.getString("foo"));
    }

    @Test
    public void configLongTest(){
        assertEquals(1234567l, testConfig.getLong("bigNum"), 0);
    }

}
