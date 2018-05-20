package org.inspirerobotics.sumobots.field.driverstation;

import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.util.logging.Logger;

public class DriverStationListener implements ConnectionListener {

	private final Logger log = InternalLog.getLogger();

	@Override
	public void receivedMessage(Message message, Connection connection) {
		log.info("Received Message: " + message);
	}
}
