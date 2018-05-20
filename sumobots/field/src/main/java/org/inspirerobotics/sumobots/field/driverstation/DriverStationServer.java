package org.inspirerobotics.sumobots.field.driverstation;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.logging.Logger;

public class DriverStationServer extends Server {

	private final Logger log = InternalLog.getLogger();

	public DriverStationServer() {
		super(new DriverStationListener(), "Field");
	}

	public void disableDS(String name) {
		for (Connection c : this.getConnections()) {
			if (c.getConnectionName().equals(name)) {
				c.sendMessage(ArchetypalMessages.enterNewMatchPeriod(TimePeriod.DISABLED));
			}
		}
	}

	public void updateNetworkingTable(NetworkTable table) {
		table.put("IP", "" + getServerSocket().getLocalSocketAddress());
		table.put("Port", "" + getServerSocket().getLocalPort());
	}

}
