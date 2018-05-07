package org.inspirerobotics.sumobots.driverstation.util;

import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.io.IOException;

public class EmptyConnection extends Connection {

	public EmptyConnection() {
		super(null, null);
	}

	@Override
	public void update() {

	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public void sendMessage(Message m) {

	}

	@Override
	public NetworkTable getTable() {
		return null;
	}

	@Override
	public void close() throws IOException {

	}

}
