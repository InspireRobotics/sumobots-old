package org.inspirerobotics.sumobots.driverstation.joystick;

import net.java.games.input.*;
import org.inspirerobotics.sumobots.library.InternalLog;

import java.util.logging.Logger;

public class Input extends Thread {

	private Logger log = InternalLog.getLogger();

	public Input() {
		this.setDaemon(true);
	}

	public void run() {
		Controller controller = getController();

		if (controller == null) {
			log.info("No Controller Found!");
			return;
		}

		EventQueue queue = controller.getEventQueue();
		Event event = new Event();
		log.info("Using Controller: " + controller.getName());

		while (true) {
			controller.poll();

			while (queue.getNextEvent(event)) {
				printEventInfo(event, controller);
			}

			sleepCatchException(10);
		}
	}

	private void printEventInfo(Event event, Controller c) {
		StringBuffer buffer = new StringBuffer(c.getName());
		buffer.append(": the ");
		Component comp = event.getComponent();
		buffer.append(getFormattedName(comp)).append(" changed to ");
		float value = event.getValue();

		addChangedValue(buffer, comp.isAnalog(), value);

		log.finest(buffer.toString());
	}

	private String getFormattedName(Component c) {
		if (c.getName().startsWith("Button")) {
			return formatButtonName(c.getName());
		} else {
			return formatAxisName(c.getName());
		}
	}

	private String formatButtonName(String id) {
		try {
			return ControllerButton.fromString(id).getName();
		} catch (UnknownControllerElementException e) {
			return id;
		}
	}

	private String formatAxisName(String id) {
		try {
			return ControllerAxis.fromString(id).getName();
		} catch (UnknownControllerElementException e) {
			return id;
		}
	}

	private void addChangedValue(StringBuffer buffer, boolean isAnalog, float value) {
		if (isAnalog) {
			buffer.append(value);
		} else {
			if (value == 1.0f) {
				buffer.append("On");
			} else {
				buffer.append("Off");
			}
		}
	}

	private static void sleepCatchException(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Controller getController() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		if (controllers.length == 0) {
			System.out.println("Found no controllers.");
			System.exit(0);
		}

		Controller controller = null;

		for (int i = 0; i < controllers.length; i++) {
			log.fine("Found Controller: " + controllers[i]);
			if (controllers[i].getName().equals("Controller (Gamepad F310)")) {
				controller = controllers[i];
				break;
			}
		}

		return controller;
	}
}
