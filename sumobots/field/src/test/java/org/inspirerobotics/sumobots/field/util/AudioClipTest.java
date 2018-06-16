package org.inspirerobotics.sumobots.field.util;

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

	@Test
	public void testDisableRobotSound() throws IOException {
		AudioEffect.createAudioStream("disable_robot.wav");
	}

	@Test(expected = FileNotFoundException.class)
	public void fakeSoundTest() throws IOException {
		AudioEffect.createAudioStream("foo.wav");
	}

}
