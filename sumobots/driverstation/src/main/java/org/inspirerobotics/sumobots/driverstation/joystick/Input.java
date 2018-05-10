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

		EventQueue queue = controller.getEventQueue();

		Event event = new Event();

		log.info("Using Controller: " + controller.getName());

		while (true) {
			controller.poll();

			while (queue.getNextEvent(event)) {
				StringBuffer buffer = new StringBuffer(controller.getName());
				buffer.append(" at ");
				buffer.append(event.getNanos()).append(", ");
				Component comp = event.getComponent();
				buffer.append(comp.getName()).append(" changed to ");
				float value = event.getValue();
				if (comp.isAnalog()) {
					buffer.append(value);
				} else {
					if (value == 1.0f) {
						buffer.append("On");
					} else {
						buffer.append("Off");
					}
				}
				log.finest(buffer.toString());
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
