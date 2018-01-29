package com.inspirerobotics.sumobots.driverstation.gui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MainScene{

	/**
	 * The group for all of the componenets
	 */
	private final Group group = new Group();
	
	private FlowPane pane = new FlowPane();
	
	/**
	 * The name text box
	 */
	private TextField nameTextBox = new TextField();
	
	/**
	 * The set name button
	 */
	private Button setName = new Button("Set Name");
	
	public MainScene() {
		pane.getChildren().addAll(nameTextBox, setName);
		group.getChildren().add(pane);
		
		setName("");
	}
	
	public void setName(String s) {
		nameTextBox.setText(s);
	}
	
	public static MainScene build() {
		return new MainScene();
	}

	public Scene toScene() {
		return new Scene(group);
	}
	
}
