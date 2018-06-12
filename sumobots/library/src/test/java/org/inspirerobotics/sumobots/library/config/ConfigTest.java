package org.inspirerobotics.sumobots.library.config;

import me.grison.jtoml.impl.Toml;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

	private static Toml createTestToml() {
		return Toml.parse("bigNum = 1234567\nfoo = \"bar\"");
	}

	@Test
	public void simpleConfigStringTest() {
		Config testConfig = new Config(createTestToml(), null);

		assertEquals("bar", testConfig.getString("foo"));
	}

	@Test
	public void simpleConfigLongTest() {
		Config testConfig = new Config(createTestToml(), null);

		assertEquals(1234567l, testConfig.getLong("bigNum"), 0);
	}

	@Test
	public void nullTomlConfigAlwaysReturnsNullLongTest() {
		Config nullTomlConfig = new Config(null, null);

		Assert.assertNull(nullTomlConfig.getLong("Test"));
	}

	@Test
	public void nullTomlConfigAlwaysReturnsNullString() {
		Config nullTomlConfig = new Config(null, null);

		Assert.assertNull(nullTomlConfig.getString("Test"));
	}

	@Test
	public void nonExistentTomlThrowsWarning() {
		InternalLog.getInstance().clear();

		new Config("Test");

		// Remove the start of the log entry
		String actual = InternalLog.getInstance().getLogLines().get(1);
		actual = actual.substring(actual.indexOf("WARNING:") + 9);

		Assert.assertTrue(true);
	}

}
