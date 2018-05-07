package org.inspirerobotics.sumobots.field.util;

import org.inspirerobotics.sumobots.library.Resources;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class AudioEffect {

	public static void play(String filename) {
		try {
			Logger.getLogger(Resources.LOGGER_NAME).fine("Playing Sound: " + filename);

			AudioPlayer.player.start(createAudioStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(Resources.LOGGER_NAME)
					.severe(String.format("IO Error \"%s\" while loading sound file: " + filename, e.getMessage()));
		}
	}

	public static AudioStream createAudioStream(String name) throws IOException {
		Logger.getLogger(Resources.LOGGER_NAME).info("Loading file: " + "sounds/" + name);

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("sounds/" + name);

		return new AudioStream(in);
	}

}
