package org.inspirerobotics.sumobots.display.config;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.config.Config;

import java.util.logging.Level;

public class Settings {

	private final Config config;

	private Settings() {
		this(new Config("Display"));

		InternalLog.setShouldStoreLog(shouldStoreLog());
	}

	private Settings(Config c) {
		config = c;
	}

	private String loadDefaultString(String key, String defaultVal) {
		return config.getString(key) == null ? defaultVal : config.getString(key);
	}

	public Level logLevel() {
		return Level.parse(loadDefaultString("logLevel", "FINE"));
	}

	public boolean shouldStoreLog() {
		return Boolean.valueOf(loadDefaultString("storeLog", "false"));
	}

	public String fieldIP() {
		return loadDefaultString("fieldIP", "localhost");
	}

	@Override
	public Object clone() {
		return new Settings(config);
	}

	public static Settings load() {
		return new Settings();
	}

}
