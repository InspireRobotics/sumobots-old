package com.inspirerobotics.sumobots.field.gui;

import javafx.beans.property.SimpleStringProperty;

/**
 * A class to store the data for a connection in the Connection Table
 * 
 * Currently a WIP
 * @author Noah
 */
public class TableConnection {
	
	public SimpleStringProperty name;
	
	public SimpleStringProperty dsIP;
	
	public SimpleStringProperty dsPing;
	
	public SimpleStringProperty robotIP;
	
	public SimpleStringProperty robotPing;
	
	public SimpleStringProperty status;
	
	public TableConnection(String name, String dsIP, String dsPing, String robotIP, String robotPing, String status) {
		this.name = new SimpleStringProperty(name);
		this.dsIP = new SimpleStringProperty(dsIP);
		this.dsPing = new SimpleStringProperty(dsPing);
		this.robotIP = new SimpleStringProperty(robotIP);
		this.robotPing = new SimpleStringProperty(robotPing);
		this.status = new SimpleStringProperty(status);
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
	
}