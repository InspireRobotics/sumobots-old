package org.inspirerobotics.sumobots.driverstation.joystick;

import java.util.HashMap;
import java.util.logging.Logger;

import org.inspirerobotics.sumobots.library.InternalLog;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class Gamepad {

	private final HashMap<String, Float> inputValues = new HashMap<String, Float>();
	
	private final JoystickListener listener;
	private final Logger log = InternalLog.getLogger();
	private final Controller controller;
	private final EventQueue eventQueue;
	private final Event event;

	Gamepad(Controller controller, JoystickListener l) {
		this.controller = controller;
		this.listener = l;

		eventQueue = controller.getEventQueue();
		event = new Event();
	}

	boolean poll() {
		return controller.poll();
	}

	void update() {
		while (eventQueue.getNextEvent(event)) {
			handleEvent();
		}
	}
	
	private void handleEvent() {
		printEventInfo();
		updateValueMap();
		listener.onValueUpdated(this);
	}

	private void updateValueMap() {
		String name = getFormattedName(event.getComponent());
		inputValues.put(name, event.getValue());
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
	
	public HashMap<String, Float> getInputValues() {
		return inputValues;
	}

}
