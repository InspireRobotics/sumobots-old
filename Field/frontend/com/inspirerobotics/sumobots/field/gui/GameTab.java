package com.inspirerobotics.sumobots.field.gui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.FieldFrontend;
import com.inspirerobotics.sumobots.field.util.InternalLog;
import com.inspirerobotics.sumobots.lib.networking.connection.Connection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class GameTab extends AnchorPane {

	/**
	 * The FieldFrontened object. This handles all events happening on the Frontend
	 * (and ones being passed to the backend).
	 */
	private FieldFrontend fieldFrontend;

	/**
	 * The pane at the bottom of the screen. Holds the overall match controls (stop,
	 * start), console, and other options
	 */
	@FXML
	public HBox controlPane;

	/**
	 * The Main Border Pane with match status bar, connection table, etc.
	 */
	@FXML
	public BorderPane mainBorderPane;

	/**
	 * The match status bar. Contains match time, match number, and current period
	 */
	public MatchStatusBar matchStatusBar;

	/**
	 * This pane holds only the buttons at the bottom (stop, start, pause, init,
	 * etc..)
	 */
	@FXML
	public HBox controlButtonPane;

	/**
	 * The button used to init the match
	 */
	@FXML
	public Button initMatch;

	/**
	 * The button used to start the match
	 */
	@FXML
	public Button startMatch;

	/**
	 * The button used to stop the match
	 */
	@FXML
	public Button endMatch;

	/**
	 * The console on the control panel
	 */
	@FXML
	public TextArea controlConsole;

	/**
	 * The tab with the current connections
	 */
	@FXML
	public TableView<TableConnection> connTable;

	/**
	 * The log
	 */
	private Logger logger = InternalLog.getLogger();

	/**
	 * The Status Column
	 */
	private TableColumn<?, ?> statusColumn;

	/**
	 * The toolbar with options
	 */
	private ToolBar toolbar;

	/*
	 * Toolbar buttons
	 */
	private Button closeAllTB, emergencyStopTB;

	/**
	 * Creates a game tab and starts the game tab loop
	 * 
	 * @param ff
	 *            the varaible to the FieldFrontend. See {@link #fieldFrontend} for
	 *            its purpose
	 */
	public GameTab(FieldFrontend ff) {
		this.fieldFrontend = ff;

		// Creates the loader that loads the GUI
		logger.fine("Loading gameTab.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/gameTab.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);

		// Actually try to load the GUI
		try {
			fxmlLoader.load();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load gameTab.fxml", e);
		}

		// Temporary Text. Will be removed once logging is set up
		controlConsole.setText("Error: No Robot Detected");

		// initialize the table
		initConnTable();

		// initialize the status bar
		initStatBar();
	}

	/*
	 * Inits the stat bar
	 */
	private void initStatBar() {
		matchStatusBar = new MatchStatusBar();

		initToolbar();
		toolbar = new ToolBar(emergencyStopTB, closeAllTB);

		VBox vbox = new VBox();
		vbox.getChildren().addAll(matchStatusBar, toolbar);

		mainBorderPane.setTop(vbox);
	}

	private void initToolbar() {
		emergencyStopTB = new Button("Emergency Stop");
		emergencyStopTB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fieldFrontend.eStop();
			}
		});

		closeAllTB = new Button("Close All DS Connections");
		closeAllTB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fieldFrontend.closeAll();
			}
		});

	}

	/**
	 * Adds the columns to the table
	 */
	private void initConnTable() {
		addSimpleColumn("Name", 350, "name");
		addSimpleColumn("DS IP", 200, "dsIP");
		addSimpleColumn("DS Ping", 150, "dsPing");
		addSimpleColumn("Robot IP", 200, "robotIP");
		addSimpleColumn("Robot Ping", 150, "robotPing");
		addButtonColumn("Disable", 125, "action", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println(event.hashCode());
			}

		});
		statusColumn = addSimpleColumn("Status", 200, "status");

		TableConnection ts = new TableConnection("DS-1", "196.168.0.5", "20 ms", "196.168.0.3", "35 ms", "Connected!",
				"");
		ObservableList<TableConnection> data = FXCollections.observableArrayList(ts);

		connTable.setItems(data);
	}

	/**
	 * Adds a generic column to the connection table
	 * 
	 * @param name
	 *            the name to be shown to the user
	 * @param minWidth
	 *            the min width of the tab
	 * @param propertyName
	 *            the property name of the column
	 */
	private TableColumn<TableConnection, String> addSimpleColumn(String name, int minWidth, String propertyName) {
		TableColumn<TableConnection, String> col = new TableColumn<TableConnection, String>(name);
		col.setMinWidth(minWidth);
		col.setMaxWidth(minWidth);
		col.setCellValueFactory(new PropertyValueFactory<TableConnection, String>(propertyName));
		connTable.getColumns().add(col);
		return col;
	}

	/**
	 * Adds a column to the connection table with a buttom
	 * 
	 * @param name
	 *            the name to be shown to the user
	 * @param minWidth
	 *            the min width of the tab
	 * @param propertyName
	 *            the property name of the column
	 */
	private TableColumn<TableConnection, String> addButtonColumn(String name, int minWidth, String propertyName,
			EventHandler<ActionEvent> event) {
		TableColumn<TableConnection, String> col = new TableColumn<TableConnection, String>(name);
		col.setMinWidth(minWidth);
		col.setMaxWidth(minWidth);
		col.setCellValueFactory(new PropertyValueFactory<TableConnection, String>(propertyName));

		Callback<TableColumn<TableConnection, String>, TableCell<TableConnection, String>> cellFactory = new Callback<TableColumn<TableConnection, String>, TableCell<TableConnection, String>>() {
			@Override
			public TableCell<TableConnection, String> call(final TableColumn<TableConnection, String> param) {
				final TableCell<TableConnection, String> cell = new TableCell<TableConnection, String>() {

					Button btn = new Button(name);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							if (item.equals("")) {
								setGraphic(null);
								setText(null);
								return;
							}

							btn.setText(item);
							btn.setMaxHeight(10);
							btn.setOnAction(event);
							btn.setMinWidth(minWidth - 8);
							btn.setMaxWidth(minWidth - 8);
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		col.setCellFactory(cellFactory);

		connTable.getColumns().add(col);
		return null;
	}

	/**
	 * Updates certain aspects of the GUI
	 */
	void update() {
		// Update the match status bar
		matchStatusBar.updateGui();
		matchStatusBar.updateStats(fieldFrontend.getTimePeriod());

		// Makes all of the buttons fill the panel height wise
		initMatch.setMinHeight(controlPane.getHeight() - 5);
		startMatch.setMinHeight(controlPane.getHeight() - 5);
		endMatch.setMinHeight(controlPane.getHeight() - 5);

		// Makes it so the console fills all of the width left over
		controlConsole.setMinWidth(this.getWidth() - controlButtonPane.getWidth() - 20);
		controlConsole.setMaxWidth(this.getWidth() - controlButtonPane.getWidth() - 20);

		// Update the status column fill up all available room
		statusColumn.setMinWidth(connTable.getWidth() - 1175);
		statusColumn.setMaxWidth(connTable.getWidth() - 1175);

		// Update the console
		List<String> list = InternalLog.getInstance().getLogLines();
		StringBuilder sb = new StringBuilder();

		// toArray() prevents ConcurrentModificationException
		for (Object obj : list.toArray()) {
			String string = (String) obj;
			if (!string.contains("FINE") && !string.contains("FINER"))
				sb.append(string);
		}

		if (!sb.toString().equals(controlConsole.getText())) {
			controlConsole.setText(sb.toString());
			controlConsole.setScrollTop(Double.MAX_VALUE);
		}

	}

	/**
	 * Sets the connections in the connection table
	 * 
	 * @param conns
	 *            the connections
	 */
	public void setConnections(List<Connection> conns) {
		ObservableList<TableConnection> data = FXCollections.observableArrayList();

		// Add every connection to the table
		for (Connection conn : conns) {
			String name = conn.getConnectionName();
			String dsIP = conn.getSocket().getRemoteSocketAddress().toString();
			String dsPing = conn.getCurrentPing() + " ms";
			String robotIP = "null";
			String robotPing = "null";
			String status = conn.isClosed() ? "Closed!" : "Open!";
			TableConnection tc = new TableConnection(name, dsIP, dsPing, robotIP, robotPing, status, "Disable");
			data.add(tc);
		}

		// If the size is zero, add a blank row to make sure the "no content" notice
		// doesn't show up
		if (data.size() == 0) {
			data.add(new TableConnection("", "", "", "", "", "", ""));
		}

		connTable.setItems(data);
	}

	/*
	 * CONTROLLER METHODS
	 */

	/**
	 * When the Start Button is pressed
	 */
	@FXML
	public void startButtonPressed() {
		fieldFrontend.startMatch();
	}

	/**
	 * When the Init Button is pressed
	 */
	@FXML
	public void initButtonPressed() {
		fieldFrontend.initMatch();
	}

	/**
	 * When the End Button is pressed
	 */
	@FXML
	public void endButtonPressed() {
		fieldFrontend.endMatch();
	}

}
