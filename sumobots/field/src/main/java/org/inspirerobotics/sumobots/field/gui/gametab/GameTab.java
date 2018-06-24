package org.inspirerobotics.sumobots.field.gui.gametab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.inspirerobotics.sumobots.field.FieldFrontend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.gui.FXMLFileLoader;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class GameTab extends AnchorPane {

	private final FieldFrontend fieldFrontend;

	private ControlPane controlPane;

	private DisplayPane displayPane;

	@FXML
	public BorderPane centralBorderPane;

	@FXML
	public BorderPane mainBorderPane;

	@FXML
	private SplitPane centralSplitPane;

	public MatchStatusBar matchStatusBar;

	@FXML
	public TableView<TableConnection> connTable;

	@FXML
	public TableView<NetworkTableEntry> netwTable;

	private Logger logger = InternalLog.getLogger();

	private ToolBar toolbar;

	private Button emergencyStopTB;

	private Button clearLogButton;

	@FXML
	private ChoiceBox<String> netwTableSelector;

	@FXML
	private NetworkTable internalNetwTable;

	private ArrayList<Connection> connsInTable = new ArrayList<Connection>();

	public GameTab(FieldFrontend ff) {
		this.fieldFrontend = ff;

		FXMLFileLoader.load("gameTab.fxml", this);

		controlPane = new ControlPane(fieldFrontend);
		mainBorderPane.setBottom(controlPane);

		initTables();
		initStatBar();
		initDisplayPane(ff);

		netwTableSelector.getItems().add("Internal Table");
		netwTableSelector.getSelectionModel().select(0);
		logger.fine("Initialized GameTab!");
	}

	private void initDisplayPane(FieldFrontend ff) {
		displayPane = new DisplayPane(ff);

		centralSplitPane.getItems().add(displayPane);
	}

	private void initStatBar() {
		matchStatusBar = new MatchStatusBar();

		initToolbarButtons();
		toolbar = new ToolBar(emergencyStopTB, clearLogButton);
		toolbar.setStyle("-fx-background-color:#2a2a2a");

		VBox vbox = new VBox();
		vbox.getChildren().addAll(matchStatusBar, toolbar);

		centralBorderPane.setTop(vbox);
	}

	private void initToolbarButtons() {
		emergencyStopTB = new Button("Emergency Stop");
		emergencyStopTB.setOnAction(arg0 -> fieldFrontend.eStop());

		clearLogButton = new Button("Clear Log");
		clearLogButton.setOnAction(event -> InternalLog.getInstance().clear());
	}

	private void initTables() {
		addSimpleNetworkColumn("Key", 200, "key");
		addSimpleNetworkColumn("Value", 500, "value");

		addSimpleConnColumn("Name", 350, "name");
		addSimpleConnColumn("DS IP", 200, "dsIP");
		addSimpleConnColumn("DS Ping", 150, "dsPing");
		addSimpleConnColumn("Robot IP", 200, "robotIP");
		addSimpleConnColumn("Robot Ping", 150, "robotPing");
		addButtonColumn("Disable", 125, "action", event -> {
			String name = (String) event.getSource();
			logger.info("Disabling " + name);
			fieldFrontend.disable(name);
		});
		addSimpleConnColumn("Status", 200, "status");
		ObservableList<TableConnection> data = FXCollections.observableArrayList();

		connTable.setItems(data);
	}

	private TableColumn<NetworkTableEntry, String> addSimpleNetworkColumn(String name, int minWidth,
			String propertyName) {
		TableColumn<NetworkTableEntry, String> col = new TableColumn<NetworkTableEntry, String>(name);
		col.setCellValueFactory(new PropertyValueFactory<NetworkTableEntry, String>(propertyName));

		netwTable.getColumns().add(col);
		return col;
	}

	private TableColumn<TableConnection, String> addSimpleConnColumn(String name, int minWidth, String propertyName) {
		TableColumn<TableConnection, String> col = new TableColumn<TableConnection, String>(name);
		col.setCellValueFactory(new PropertyValueFactory<TableConnection, String>(propertyName));

		connTable.getColumns().add(col);
		return col;
	}

	private TableColumn<TableConnection, String> addButtonColumn(String name, int minWidth, String propertyName,
			EventHandler<ActionEvent> handler) {
		TableColumn<TableConnection, String> col = new TableColumn<TableConnection, String>(name);
		col.setCellValueFactory(new PropertyValueFactory<TableConnection, String>(propertyName));
		col.setMaxWidth(minWidth);
		col.setMinWidth(minWidth);

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

							btn.setText(item.split(";")[1]);
							btn.setMaxHeight(10);
							btn.setOnAction(e -> handler.handle(new ActionEvent(item.split(";")[0], ActionEvent.NULL_SOURCE_TARGET)));
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

	public void update() {
		controlPane.update();
		matchStatusBar.updateGui();
		matchStatusBar.updateStats(fieldFrontend.getTimePeriod());
	}

	public void setConnections(ArrayList<Connection> conns) {
		updateNetworkTableSelector(conns);
		updateNetworkTable(conns);
		updateConnectionTable(conns);
	}

	private void updateNetworkTable(ArrayList<Connection> conns) {
		ObservableList<NetworkTableEntry> data = FXCollections.observableArrayList();

		NetworkTable table = null;
		String netwTableName = netwTableSelector.getSelectionModel().getSelectedItem();

		for (Connection c : conns) {
			if (c.getConnectionName().equals(netwTableName)) {
				table = c.getTable();
				break;
			}
		}
		if (netwTableName != null) {
			if (netwTableName.equals("Internal Table")) {
				table = internalNetwTable;
			}
		}

		if (table != null) {
			for (Entry<String, String> e : table.entrySet()) {
				data.add(new NetworkTableEntry(e.getKey(), e.getValue()));
			}
		}

		if (data.size() == 0) {
			data.add(new NetworkTableEntry("", ""));
		}

		netwTable.setItems(data);
	}

	@SuppressWarnings("unchecked")
	private void updateNetworkTableSelector(ArrayList<Connection> conns) {
		ArrayList<Connection> connsListed = (ArrayList<Connection>) this.connsInTable.clone();
		ArrayList<Connection> connsIter = (ArrayList<Connection>) conns.clone();

		for (Iterator<Connection> iterator = connsIter.iterator(); iterator.hasNext();) {
			Connection connection = (Connection) iterator.next();

			boolean alreadyListed = false;

			for (Connection c : connsListed) {
				if (connection.getConnectionName().equals(c.getConnectionName())) {
					alreadyListed = true;
					break;
				}
			}

			if (!alreadyListed) {
				if (connection.getConnectionName() != null) {
					if (!connection.getConnectionName().equals("")) {
						netwTableSelector.getItems().add(connection.getConnectionName());
						connsInTable.add(connection);
						logger.fine("Found Network Table: " + connection.getConnectionName());
					}
				}
			} else {
				connsListed.remove(connection);
			}
		}

		for (Connection connection : connsListed) {
			connsInTable.remove(connection);
			netwTableSelector.getItems().remove(connection.getConnectionName());
			logger.warning("Lost networking table? " + connection.getConnectionName());
		}

	}

	private void updateConnectionTable(ArrayList<Connection> conns) {
		ObservableList<TableConnection> data = FXCollections.observableArrayList();

		for (Connection conn : conns) {
			String name = conn.getConnectionName();
			String dsIP = conn.getSocket().getRemoteSocketAddress().toString();
			String dsPing = conn.getCurrentPing() + " ms";
			String robotIP = conn.getTable().get("robot_ip");
			String robotPing = conn.getTable().get("robot_ping");
			String status = conn.isClosed() ? "Closed!" : "Open!";
			TableConnection tc = new TableConnection(name, dsIP, dsPing, robotIP, robotPing, status, name + ";Disable");
			data.add(tc);
		}

		if (data.size() == 0) {
			data.add(new TableConnection("", "", "", "", "", "", ""));
		}

		connTable.setItems(data);
	}

	public void setInternalNetwTable(NetworkTable internalNetwTable) {
		this.internalNetwTable = internalNetwTable;
	}

	public DisplayPane getDisplayPane() {
		return displayPane;
	}
}
