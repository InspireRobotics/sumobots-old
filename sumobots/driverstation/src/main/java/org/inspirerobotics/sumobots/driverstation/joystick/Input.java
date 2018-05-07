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
		while (true) {
			Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
			if (controllers.length == 0) {
				System.out.println("Found no controllers.");
				System.exit(0);
			}

			for (int i = 0; i < controllers.length; i++) {
				controllers[i].poll();

				EventQueue queue = controllers[i].getEventQueue();

				Event event = new Event();

				while (queue.getNextEvent(event)) {
					StringBuffer buffer = new StringBuffer(controllers[i].getName());
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
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
