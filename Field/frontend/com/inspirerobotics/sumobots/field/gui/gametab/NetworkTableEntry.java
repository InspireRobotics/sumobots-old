package com.inspirerobotics.sumobots.field.gui.gametab;

import javafx.beans.property.SimpleStringProperty;

public class NetworkTableEntry {
	
	public SimpleStringProperty key;
	
	public SimpleStringProperty value;
	
	public NetworkTableEntry(String key, String value) {
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
	}
	
	public String getKey() {
		return key.get();
	}
	
	public String getValue() {
		return value.get();
	}
	
}
