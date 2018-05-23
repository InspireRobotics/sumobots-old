package org.inspirerobotics.sumobots.library.config;

import me.grison.jtoml.impl.Toml;
import org.inspirerobotics.sumobots.library.InternalLog;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ConfigLoader {

	private static final Logger logger = InternalLog.getLogger();

	public static Toml loadToml(String name) throws IOException {
		String path = getPath(name);

		logger.info("Loading Config File: " + path);

		return loadTomlFile(new File(path));
	}

	static Toml loadTomlFile(File file) throws IOException {
		return Toml.parse(file);
	}

	public static String getPath(String name) {
		return getPath(name, System.getenv("userprofile") + "/Desktop/");
	}

	public static String getPath(String name, String rootDirectory) {
		return rootDirectory + name + ".toml";
	}
}
