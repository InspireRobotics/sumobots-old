package com.inspirerobotics.sumobots.lib.config;

import java.io.IOException;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;

import me.grison.jtoml.impl.Toml;

public class Config{
	
	private static final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);
	
	private Toml toml;
	private final String name;
	
	public Config(String name) {
		this.name = name;
		
		try {
			toml = ConfigLoader.loadToml(name);
		}catch(IOException e){
			logger.warning("Couldn't find config file: " + name);
		}
		
	}
	
	public String getString(String key) {
		if(toml == null)
			return null;
		return toml.getString(key);
	}
	
	public Long getLong(String key) {
		if(toml == null)
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
