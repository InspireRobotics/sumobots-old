package org.inspirerobotics.sumobots.library.networking;

import org.inspirerobotics.sumobots.library.Resources;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketStream {

	private final DataInputStream reader;

	private final Socket socket;

	private final DataOutputStream writer;

	private final StringBuilder builder = new StringBuilder();

	private boolean closed = false;

	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

	public SocketStream(InputStream reader, OutputStream writer, Socket socket) {
		super();
		this.socket = socket;
		this.reader = new DataInputStream(reader);
		this.writer = new DataOutputStream(writer);
	}

	protected SocketStream(Socket socket, DataInputStream reader, DataOutputStream writer) {
		this.socket = socket;
		this.reader = reader;
		this.writer = writer;
	}

	public SocketStream(Socket socket) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream(), socket);
	}

	public void update() {
		if (closed)
			return;

		String s;
		while (true && !closed) {
			try {
				s = reader.readUTF();
				builder.append(s);
			} catch (EOFException e) {
				break;
			} catch (SocketTimeoutException e) {
				break;
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"IOException threw while updating SocketStream for socket " + socket.getInetAddress(), e);
				closed = true;

				try {
					this.close();
				} catch (IOException e1) {
					logger.log(Level.FINE, "IOException threw while closing socket " + socket.getInetAddress(), e);
				}

			}
		}
	}

	public boolean hasNextMessage() {
		return builder.toString().contains(Resources.EOT);
	}

	public String getNextMessage() {
		int index = builder.toString().indexOf(Resources.EOT);

		// If no message is there return null
		if (index == -1) {
			return null;
		}

		// Remove it from the buffer
		String s = builder.toString().substring(0, index);
		builder.replace(0, index + 1, "");
		return s;
	}

	public void write(String s) {
		if (closed)
			return;

		try {
			writer.writeUTF(s);
			writer.flush();
		} catch (IOException e) {
			logger.log(Level.SEVERE,
					"IOException threw while writing to the SocketStream for socket " + socket.getInetAddress(), e);
			closed = true;

			try {
				this.close();
			} catch (IOException e1) {
				logger.log(Level.FINE, "IOException threw while closing socket " + socket.getInetAddress(), e);
			}
		}
	}

	public void close() throws IOException {
		if (isClosed())
			return;

		logger.fine("Closing socket stream: " + socket.getInetAddress());
		reader.close();
		writer.close();
		closed = true;
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	public boolean isClosed() {
		return closed;
	}

	public DataInputStream getReader() {
		return reader;
	}

	public DataOutputStream getWriter() {
		return writer;
	}

}
