package org.inspirerobotics.sumobots.field.driverstation;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.logging.Logger;

public class DriverStationServer extends Server {

	private final Logger log = InternalLog.getLogger();

	private boolean shouldAcceptConnections = true;

	public DriverStationServer() {
		super(new DriverStationListener(), "Field", Resources.SERVER_PORT);
	}

	public void disableDS(String name) {
		for (Connection c : this.getConnections()) {
			if (c.getConnectionName().equals(name)) {
				c.sendMessage(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
			}
		}
	}

	public void setShouldAcceptConnections(boolean shouldAcceptConnections) {
		this.shouldAcceptConnections = shouldAcceptConnections;

		if (shouldAcceptConnections) {
			this.reopenServer();
		} else {
			this.closeServer(false);
		}
	}

	public boolean shouldAcceptConnections() {
		return shouldAcceptConnections;
	}

	public void updateNetworkingTable(NetworkTable table) {
		table.put("IP", "" + getServerSocket().getLocalSocketAddress());
		table.put("Port", "" + getServerSocket().getLocalPort());
	}

	public void killDS(String name) {
		for (Connection c : this.getConnections()) {
			if (c.getConnectionName().equals(name)) {
				c.endConnection();
				break;
			}
		}
	}
}
