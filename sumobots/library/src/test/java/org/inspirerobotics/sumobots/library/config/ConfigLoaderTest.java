package org.inspirerobotics.sumobots.library.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigLoaderTest {

	@Test
	public void getConfigPathDefaultDirectory() {
		String expected = System.getenv("userprofile") + "/Desktop/Test.toml";

		Assert.assertEquals(expected, ConfigLoader.getPath("Test"));
	}

	@Test
	public void getConfigPathNonDefaultDirectory() {
		String expected = "Root/Test.toml";

		Assert.assertEquals(expected, ConfigLoader.getPath("Test", "Root/"));
	}

}
