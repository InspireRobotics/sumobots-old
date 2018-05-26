package org.inspirerobotics.sumobots.driverstation.joystick;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import org.inspirerobotics.sumobots.driverstation.joystick.gamepad.Gamepad;
import org.inspirerobotics.sumobots.driverstation.joystick.gamepad.JoystickListener;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class InputThread extends Thread implements JoystickListener {

	private Logger log = InternalLog.getLogger();

	private ThreadChannel threadChannel;

	public InputThread(ThreadChannel threadChannel) {
		this.setDaemon(true);

		this.setName("Joystick Thread");
		this.threadChannel = threadChannel;
	}

	public void run() {
		while (true) {
			Controller controller = getController();

			if (controller == null) {
				log.info("No Controller Found!");
				sleepCatchException(3000);
			} else {
				threadChannel.add(new InterThreadMessage("joystick_status", true));
				runMainLoop(controller);
				threadChannel.add(new InterThreadMessage("joystick_status", false));
			}
		}
	}

	private void runMainLoop(Controller controller) {
		log.info("Using Controller: " + controller.getName());

		Gamepad gamepad = new Gamepad(controller, this);

		while (true) {
			if (!gamepad.poll()) {
				log.warning("Lost the controller!");
				threadChannel.add(new InterThreadMessage("input_values", null));
				return;
			}

			gamepad.update();

			sleepCatchException(10);
		}
	}

	@Override
	public void onValueUpdated(Gamepad p) {
		threadChannel.add(new InterThreadMessage("input_values", p.getInputValues()));
	}

	private static void sleepCatchException(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Controller getController() {
		ControllerEnvironment environment = getControllerEnvironment();

		Controller[] controllers = environment.getControllers();
		if (controllers.length == 0) {
			System.out.println("Found no controllers.");
			System.exit(0);
		}

		Controller controller = null;

		for (int i = 0; i < controllers.length; i++) {
			if (controllers[i].getName().equals("Controller (Gamepad F310)")) {
				controller = controllers[i];
				break;
			}
		}

		return controller;
	}

	private ControllerEnvironment getControllerEnvironment() {
		try {
			return rescan();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ControllerEnvironment rescan() throws Exception {
		Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>) Class
				.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

		constructor.setAccessible(true);

		return constructor.newInstance();
	}
}
