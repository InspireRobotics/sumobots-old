package com.inspirerobotics.sumobots.lib.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;

import me.grison.jtoml.impl.Toml;

public class ConfigLoader {
	
	private static final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);
	
	public static Toml loadToml(String name) throws IOException {
		String path = getPath(name);
		
		logger.info("Loading Config File: " + path);
		
		return Toml.parse(new File(path));
	}
	
	private static String getPath(String name) {
		return System.getenv ("userprofile") + "/Desktop/" + name + ".toml";
	}
	
}
