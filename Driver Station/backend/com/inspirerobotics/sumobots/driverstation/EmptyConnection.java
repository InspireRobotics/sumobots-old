package com.inspirerobotics.sumobots.driverstation;

import java.io.IOException;

import com.inspirerobotics.sumobots.lib.networking.connection.Connection;
import com.inspirerobotics.sumobots.lib.networking.message.Message;
import com.inspirerobotics.sumobots.lib.networking.tables.NetworkTable;

public class EmptyConnection extends Connection{

	public EmptyConnection(){
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
