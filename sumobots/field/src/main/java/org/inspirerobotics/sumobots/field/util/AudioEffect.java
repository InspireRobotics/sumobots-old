package org.inspirerobotics.sumobots.field.util;

import org.inspirerobotics.sumobots.library.InternalLog;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class AudioEffect {

	private static final Logger logger = InternalLog.getLogger();

	public static void play(String filename) {
		try {
			logger.fine("Playing Sound: " + filename);

			AudioPlayer.player.start(createAudioStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe(String.format("IO Error \"%s\" while loading sound file: " + filename, e.getMessage()));
		}
	}

	public static AudioStream createAudioStream(String name) throws IOException {
		logger.info("Loading file: sounds/" + name);

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("sounds/" + name);

		if (in == null)
			throw new FileNotFoundException("Couldn't find file: sounds/" + name);

		return new AudioStream(in);
	}

}
