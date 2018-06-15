package org.inspirerobotics.sumobots.driverstation.joystick;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.inspirerobotics.sumobots.driverstation.joystick.gamepad.Gamepad;
import org.inspirerobotics.sumobots.driverstation.joystick.gamepad.JoystickListener;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

public class InputThread extends Thread implements JoystickListener {

	private Logger log = InternalLog.getLogger();
	private boolean running;
	private ThreadChannel threadChannel;

	public InputThread(ThreadChannel threadChannel) {
		this.setDaemon(false);

		this.setName("Joystick Thread");
		this.threadChannel = threadChannel;
	}

	public void run() {
		running = true;

		while (running) {
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

		log.info("Joystick thread shutdown");
	}

	private void runMainLoop(Controller controller) {
		log.info("Using Controller: " + controller.getName());

		Gamepad gamepad = new Gamepad(controller, this);

		while (running) {
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

	private void sleepCatchException(long millis) {
		if (!running)
			return;

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			running = false;
			log.warning("Input thread shutdown while sleeping!");
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
			if (controllers[i].getName().contains("F310")) {
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
