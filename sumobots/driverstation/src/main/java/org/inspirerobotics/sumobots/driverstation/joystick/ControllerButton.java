package org.inspirerobotics.sumobots.driverstation.joystick;

public enum ControllerButton {

	A("Button 0", "A Button"),
	B("Button 1", "B Button"),
	X("Button 2", "X Button"),
	Y("Button 3", "Y Button"),
	LEFT("Button 4", "Left Button"),
	RIGHT("Button 5", "Right Button");

	String id;
	String name;

	ControllerButton(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static ControllerButton fromString(String id) throws UnknownControllerElementException {
		for (ControllerButton button : values()) {
			if(button.getId().equals(id)) {
				return button;
			}
		}

		throw new UnknownControllerElementException("Unknown Controller Button: " + id);
	}

}
