package org.inspirerobotics.sumobots.driverstation.joystick;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.Alerts;

import java.util.logging.Logger;

public class JInputVerifier {

	private static final Logger log = InternalLog.getLogger();

	public static boolean libraryInstalled() {
		try {
			if ("x86".equals(System.getProperty("os.arch"))) {
				System.loadLibrary("jinput-dx8");
			} else {
				System.loadLibrary("jinput-dx8_64");
			}
		} catch (UnsatisfiedLinkError e) {
			return false;
		}

		return true;
	}

	public static void checkLibrary() {
		if (libraryInstalled()) {
			log.info("JInput library found!");
			return;
		}

		Alerts.errorAlert(Alerts.ShutdownLevel.ALL, "Joystick Error", "Failed to load JInput", true);
	}
}
