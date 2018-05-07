package org.inspirerobotics.sumobots.driverstation.joystick;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.util.Arrays;

public class Joystick {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));

		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (int i = 0; i < ca.length; i++) {
			/* Get the name of the controller */
			printInfo(ca[i]);

		}

	}

	private static void printInfo(Controller controller) {
		String type = controller.getType().toString();
		String components = Arrays.toString(controller.getComponents());
		String name = controller.getName();

		System.out.printf("%s (%s): %s\n", name, type, components);
	}

}
