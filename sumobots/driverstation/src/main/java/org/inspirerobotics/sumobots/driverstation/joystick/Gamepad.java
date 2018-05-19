package org.inspirerobotics.sumobots.driverstation.joystick;

import java.util.logging.Logger;

import org.inspirerobotics.sumobots.library.InternalLog;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class Gamepad {

	private final Logger log = InternalLog.getLogger();
	private final Controller controller;
	private final EventQueue eventQueue;
	private final Event event;

	Gamepad(Controller controller) {
		this.controller = controller;

		eventQueue = controller.getEventQueue();
		event = new Event();
	}

	boolean poll() {
		return controller.poll();
	}

	void update() {
		while (eventQueue.getNextEvent(event)) {
			printEventInfo();
		}
	}

	private void printEventInfo() {
		StringBuffer buffer = new StringBuffer(controller.getName());
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

}
