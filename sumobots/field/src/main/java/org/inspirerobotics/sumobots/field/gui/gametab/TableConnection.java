package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.beans.property.SimpleStringProperty;

public class TableConnection {

	public SimpleStringProperty name;

	public SimpleStringProperty dsIP;

	public SimpleStringProperty dsPing;

	public SimpleStringProperty robotIP;

	public SimpleStringProperty robotPing;

	public SimpleStringProperty status;

	public SimpleStringProperty action;

	public TableConnection(String name, String dsIP, String dsPing, String robotIP, String robotPing, String status,
			String action) {
		this.name = new SimpleStringProperty(name);
		this.dsIP = new SimpleStringProperty(dsIP);
		this.dsPing = new SimpleStringProperty(dsPing);
		this.robotIP = new SimpleStringProperty(robotIP);
		this.robotPing = new SimpleStringProperty(robotPing);
		this.status = new SimpleStringProperty(status);
		this.action = new SimpleStringProperty(action);
	}

	public String getName() {
		return name.get();
	}

	public String getDsIP() {
		return dsIP.get();
	}

	public String getDsPing() {
		return dsPing.get();
	}

	public String getRobotIP() {
		return robotIP.get();
	}

	public String getRobotPing() {
		return robotPing.get();
	}

	public String getStatus() {
		return status.get();
	}

	public String getAction() {
		return action.get();
	}

}
