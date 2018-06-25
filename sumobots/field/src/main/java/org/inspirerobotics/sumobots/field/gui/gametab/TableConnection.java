package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.beans.property.SimpleStringProperty;

public class TableConnection {

	public SimpleStringProperty name;

	public SimpleStringProperty dsIP;

	public SimpleStringProperty dsPing;

	public SimpleStringProperty robotIP;

	public SimpleStringProperty robotPing;

	public SimpleStringProperty timePeriod;

	public SimpleStringProperty disableAction;

	public SimpleStringProperty closeAction;

	public TableConnection(String name, String dsIP, String dsPing, String robotIP, String robotPing, String timePeriod,
			String disableAction, String closeAction) {
		this.name = new SimpleStringProperty(name);
		this.dsIP = new SimpleStringProperty(dsIP);
		this.dsPing = new SimpleStringProperty(dsPing);
		this.robotIP = new SimpleStringProperty(robotIP);
		this.robotPing = new SimpleStringProperty(robotPing);
		this.timePeriod = new SimpleStringProperty(timePeriod);
		this.disableAction = new SimpleStringProperty(disableAction);
		this.closeAction = new SimpleStringProperty(closeAction);
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

	public String getTimePeriod() {
		return timePeriod.get();
	}

	public String getDisableAction() {
		return disableAction.get();
	}

	public String getCloseAction() {
		return closeAction.get();
	}
}
