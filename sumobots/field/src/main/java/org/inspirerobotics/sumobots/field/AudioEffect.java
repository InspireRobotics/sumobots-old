package org.inspirerobotics.sumobots.field;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.inspirerobotics.sumobots.library.Resources;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class AudioEffect{
	
	public static void play(String filename) {
		try {
			Logger.getLogger(Resources.LOGGER_NAME).fine("Playing Sound: " + filename);
			InputStream in = new FileInputStream("assets/sounds/" + filename);
			AudioStream as = new AudioStream(in); 
			AudioPlayer.player.start(as); 
		}catch (IOException e) {
			Logger.getLogger(Resources.LOGGER_NAME).severe(String.format("IO Error \"%s\" while loading sound file: " + filename, e.getMessage()));
		}
		     

		  
	}
	
}
