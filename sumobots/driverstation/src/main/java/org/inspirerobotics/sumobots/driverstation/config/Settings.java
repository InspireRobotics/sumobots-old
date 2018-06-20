package org.inspirerobotics.sumobots.driverstation.config;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.config.Config;

import java.util.logging.Level;

public class Settings {

	private final Config config;

	private Settings() {
		this(new Config("DriverStation"));

		InternalLog.setShouldStoreLog(shouldStoreLog());
	}

	private Settings(Config c) {
		config = c;
	}

	public boolean nonFieldMode() {
		return Boolean.valueOf(loadDefaultString("nonFieldMode", "false"));
	}

	public Level logLevel() {
		return Level.parse(loadDefaultString("logLevel", "FINE"));
	}

	public boolean shouldStoreLog() {
		return Boolean.valueOf(loadDefaultString("storeLog", "true"));
	}

	public String robotIP() {
		return loadDefaultString("robotIP", "localhost");
	}

	public String fieldIP() {
		return loadDefaultString("fieldIP", "localhost");
	}

	public String dsName(String defaultVal) {
		return loadDefaultString("name", defaultVal);
	}

	private String loadDefaultString(String key, String defaultVal) {
		return config.getString(key) == null ? defaultVal : config.getString(key);
	}

	public Config getConfig() {
		return config;
	}

	@Override
	public Object clone() {
		return new Settings(config);
	}

	public static Settings load() {
		return new Settings();
	}

}
