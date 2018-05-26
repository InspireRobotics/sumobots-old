package org.inspirerobotics.sumobots.driverstation.joystick.gamepad;

import org.inspirerobotics.sumobots.driverstation.joystick.UnknownControllerElementException;

public enum ControllerAxis {

    LEFT_X("X Axis", "Left X Axis"),
    LEFT_Y("Y Axis", "Left Y Axis"),
    RIGHT_X("X Rotation", "Right X Axis"),
    RIGHT_Y("Y Rotation", "Right Y Axis");

    String id;
    String name;

    ControllerAxis(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static ControllerAxis fromString(String id) throws UnknownControllerElementException {
        for (ControllerAxis axis : values()) {
            if(axis.getId().equals(id)) {
                return axis;
            }
        }

        throw new UnknownControllerElementException("Unknown Controller Axis: " + id);
    }
}
