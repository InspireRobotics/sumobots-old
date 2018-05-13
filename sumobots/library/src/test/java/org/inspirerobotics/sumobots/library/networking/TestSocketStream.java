package org.inspirerobotics.sumobots.library.networking;

import java.io.IOException;
import java.net.Socket;

public class TestSocketStream extends SocketStream {

	public TestSocketStream() {
		super(new Socket(), null, null);
	}

	@Override
	public void update() {

	}

	@Override
	public boolean hasNextMessage() {
		return false;
	}

	@Override
	public String getNextMessage() {
		return super.getNextMessage();
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	protected void finalize() throws Throwable {

	}
}
