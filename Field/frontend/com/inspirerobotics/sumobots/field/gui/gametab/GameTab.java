package com.inspirerobotics.sumobots.field.gui.gametab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.field.FieldFrontend;
import com.inspirerobotics.sumobots.field.util.InternalLog;
import com.inspirerobotics.sumobots.lib.networking.connection.Connection;
import com.inspirerobotics.sumobots.lib.networking.tables.NetworkTable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
	 * The table to view NetworkTables
	 */
	@FXML
	public TableView<NetworkTableEntry> netwTable;

	/**
	 * The log
	 */
	private Logger logger = InternalLog.getLogger();

	/**
	 * The toolbar with options
	 */
	private ToolBar toolbar;

	/*
	 * Toolbar buttons
	 */
	private Button closeAllTB, emergencyStopTB;

	/**
	 * The button that selects the netwTable
	 */
	@FXML
	private ChoiceBox<String> netwTableSelector;
	
	/**
	 * The internal network table
	 */
	@FXML
	private NetworkTable internalNetwTable;

	/*
	 * The connections in the Network Table Selector
	 */
	private ArrayList<Connection> connsInTable = new ArrayList<Connection>();

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
		initTables();

		// initialize the status bar
		initStatBar();
		
		//Add internal network table
		netwTableSelector.getItems().add("Internal Table");
		netwTableSelector.getSelectionModel().select(0);
	}

	/*
	 * Inits the stat bar
	 */
	private void initStatBar() {
		// Create the bar that displays match number, time period, and time
		matchStatusBar = new MatchStatusBar();

		// Initialize the toolbar
		initToolbar();
		toolbar = new ToolBar(emergencyStopTB, closeAllTB);
		toolbar.setStyle("-fx-background-color:#2a2a2a");

		// Create the layout
		VBox vbox = new VBox();
		vbox.getChildren().addAll(matchStatusBar, toolbar);

		mainBorderPane.setTop(vbox);
	}

	/**
	 * Creates the toolbar that has options
	 */
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
	 * Adds the columns to the tables
	 */
	private void initTables() {
		// Network Table
		addSimpleNetworkColumn("Key", 200, "key");
		addSimpleNetworkColumn("Value", 500, "value");

		// Conn Table
		addSimpleConnColumn("Name", 350, "name");
		addSimpleConnColumn("DS IP", 200, "dsIP");
		addSimpleConnColumn("DS Ping", 150, "dsPing");
		addSimpleConnColumn("Robot IP", 200, "robotIP");
		addSimpleConnColumn("Robot Ping", 150, "robotPing");
		addButtonColumn("Disable", 125, "action", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String name = (String) event.getSource();
				logger.info("Disabling " + name);
				fieldFrontend.disable(name);
			}

		});
		addSimpleConnColumn("Status", 200, "status");
		ObservableList<TableConnection> data = FXCollections.observableArrayList();

		connTable.setItems(data);
	}

	/**
	 * Adds a generic column to the network table
	 * 
	 * @param name
	 *            the name to be shown to the user
	 * @param minWidth
	 *            the min width of the tab
	 * @param propertyName
	 *            the property name of the column
	 */
	private TableColumn<NetworkTableEntry, String> addSimpleNetworkColumn(String name, int minWidth, String propertyName) {
		TableColumn<NetworkTableEntry, String> col = new TableColumn<NetworkTableEntry, String>(name);
		col.setCellValueFactory(new PropertyValueFactory<NetworkTableEntry, String>(propertyName));

		netwTable.getColumns().add(col);
		return col;
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
	private TableColumn<TableConnection, String> addSimpleConnColumn(String name, int minWidth, String propertyName) {
		TableColumn<TableConnection, String> col = new TableColumn<TableConnection, String>(name);
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
	private TableColumn<TableConnection, String> addButtonColumn(String name, int minWidth, String propertyName, EventHandler<ActionEvent> handler) {
		TableColumn<TableConnection, String> col = new TableColumn<TableConnection, String>(name);
		col.setCellValueFactory(new PropertyValueFactory<TableConnection, String>(propertyName));
		col.setMaxWidth(minWidth);
		col.setMinWidth(minWidth);

		// Create the cell factory for the buttons
		Callback<TableColumn<TableConnection, String>, TableCell<TableConnection, String>> cellFactory = new Callback<TableColumn<TableConnection, String>, TableCell<TableConnection, String>>() {
			@Override
			public TableCell<TableConnection, String> call(final TableColumn<TableConnection, String> param) {
				final TableCell<TableConnection, String> cell = new TableCell<TableConnection, String>() {

					Button btn = new Button(name);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);

						// If its empty, don't show anything
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							// If the button is blank, dont show anything
							if (item.equals("")) {
								setGraphic(null);
								setText(null);
								return;
							}

							// Show the button
							btn.setText(item.split(";")[1]);
							btn.setMaxHeight(10);
							btn.setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent e) {
									handler.handle(new ActionEvent(item.split(";")[0], ActionEvent.NULL_SOURCE_TARGET));
								}
								
							});
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
	public void update() {
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
	public void setConnections(ArrayList<Connection> conns) {
		updateNetworkTableSelector(conns);
		updateNetworkTable(conns);
		updateConnectionTable(conns);
	}
	
	/**
	 * Update the network table
	 */
	private void updateNetworkTable(ArrayList<Connection> conns) {
		// Update Connection Table
		ObservableList<NetworkTableEntry> data = FXCollections.observableArrayList();
		
		NetworkTable table = null;
		String netwTableName = netwTableSelector.getSelectionModel().getSelectedItem();
		
		for (Connection c : conns) {
			if(c.getConnectionName().equals(netwTableName)) {
				table = c.getTable();
				break;
			}
		}
		if(netwTableName != null) {
			if(netwTableName.equals("Internal Table")) {
				table = internalNetwTable;
			}
		}
		
		if(table != null) {
			for (Entry<String, String> e : table.entrySet()) {
				data.add(new NetworkTableEntry(e.getKey(), e.getValue()));
			}
		}

		// If the size is zero, add a blank row to make sure the "no content" notice
		// doesn't show up
		if (data.size() == 0) {
			data.add(new NetworkTableEntry("", ""));
		}

		netwTable.setItems(data);
	}

	@SuppressWarnings("unchecked")
	private void updateNetworkTableSelector(ArrayList<Connection> conns) {
		// Update Network Table Selector
		ArrayList<Connection> connsListed = (ArrayList<Connection>) this.connsInTable.clone();
		ArrayList<Connection> connsIter = (ArrayList<Connection>) conns.clone();

		// Add in all unlisted conns, and remove the ones
		// That are already there. Now the only elements left in
		// conns listed are those that are still listed
		// but not in the list passed
		for (Iterator<Connection> iterator = connsIter.iterator(); iterator.hasNext();) {
			Connection connection = (Connection) iterator.next();

			boolean alreadyListed = false;

			// Check if the Connection was already listed in the selector
			for (Connection c : connsListed) {
				if (connection.getConnectionName().equals(c.getConnectionName())) {
					alreadyListed = true;
					break;
				}
			}

			// If its not listed, add it the to the selector
			if (!alreadyListed) {
				// If the name is null or empty, don't add it yet
				if (connection.getConnectionName() != null) {
					if (!connection.getConnectionName().equals("")) {
						netwTableSelector.getItems().add(connection.getConnectionName());
						connsInTable.add(connection);
						logger.fine("Found Network Table: " + connection.getConnectionName());
					}
				}
			} else { // If its already there, remove it from the list but don't add it
				connsListed.remove(connection);
			}
		}

		// Remove all of the things left, because they are no longer connected
		for (Connection connection : connsListed) {
			if(netwTableSelector.getItems().contains(connection.getConnectionName()))
				connsInTable.remove(netwTableSelector.getItems().indexOf(connection.getConnectionName()));
			boolean removed = netwTableSelector.getItems().remove(connection.getConnectionName());
			logger.fine("Lost Network Table: " + connection.getConnectionName());
			logger.fine("Network Table removed: " + removed);
		}

	}

	/**
	 * Adds the data to the connection table
	 */
	private void updateConnectionTable(ArrayList<Connection> conns) {
		// Update Connection Table
		ObservableList<TableConnection> data = FXCollections.observableArrayList();

		// Add every connection to the table
		for (Connection conn : conns) {
			String name = conn.getConnectionName();
			String dsIP = conn.getSocket().getRemoteSocketAddress().toString();
			String dsPing = conn.getCurrentPing() + " ms";
			String robotIP = "null";
			String robotPing = "null";
			String status = conn.isClosed() ? "Closed!" : "Open!";
			TableConnection tc = new TableConnection(name, dsIP, dsPing, robotIP, robotPing, status, name+";Disable");
			data.add(tc);
		}

		// If the size is zero, add a blank row to make sure the "no content" notice
		// doesn't show up
		if (data.size() == 0) {
			data.add(new TableConnection("", "", "", "", "", "", ""));
		}

		connTable.setItems(data);
	}
	
	public void setInternalNetwTable(NetworkTable internalNetwTable) {
		this.internalNetwTable = internalNetwTable;
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
