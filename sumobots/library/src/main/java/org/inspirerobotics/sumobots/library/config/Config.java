package org.inspirerobotics.sumobots.library.config;

import me.grison.jtoml.impl.Toml;
import org.inspirerobotics.sumobots.library.InternalLog;

import java.io.IOException;
import java.util.logging.Logger;

public class Config {

	private static final Logger logger = InternalLog.getLogger();

	private Toml toml;
	private final String name;

	public Config(String name) {
		this.name = name;

		try {
			toml = ConfigLoader.loadToml(name);
		} catch (IOException e) {
			logger.warning("Failed to load config file: " + name + ". Error: " + e.getMessage());
		}

	}

	public Config(Toml t, String name) {
		this.toml = t;
		this.name = name;
	}

	public String getString(String key) {
		if (toml == null)
			return null;
		return toml.getString(key);
	}

	public Long getLong(String key) {
		if (toml == null)
			return null;
		return toml.getLong(key);
	}

	public String getName() {
		return name;
	}

	public Toml getToml() {
		return toml;
	}

}
