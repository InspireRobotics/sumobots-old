package org.inspirerobotics.sumobots.field;

import org.inspirerobotics.sumobots.field.util.AudioEffect;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioClipTest {

	@Test
	public void testEstopSound() throws IOException {
		AudioEffect.createAudioStream("estop.wav");
	}

	@Test
	public void testStartSound() throws IOException {
		AudioEffect.createAudioStream("start.wav");
	}

	@Test(expected = FileNotFoundException.class)
	public void fakeSoundTest() throws IOException {
		AudioEffect.createAudioStream("foo.wav");
	}

}
