package com.inspirerobotics.sumobots.lib.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inspirerobotics.sumobots.lib.Resources;

public class SocketStream {
	
	/**
	 * The reader for the socket
	 */
	private final DataInputStream reader;
	
	/**
	 * The actual socket connection
	 */
	private final Socket socket;
	
	/**
	 * The writer for the socket
	 */
	private final DataOutputStream writer;
	/**
	 * The internal buffer for the stream
	 */
	private final StringBuilder builder = new StringBuilder();
	
	/**
	 * If the socket stream has been closed
	 */
	private boolean closed = false;
	
	/**
	 * The logger
	 */
	private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);
	
	/**
	 * Makes a new SocketStream
	 * 
	 * @param reader 
	 * @param writer
	 */
	public SocketStream(InputStream reader, OutputStream writer, Socket socket) {
		super();
		this.socket = socket;
		this.reader = new DataInputStream(reader);
		this.writer = new DataOutputStream(writer);
	}
	
	/**
	 * Makes a new SocketStream
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public SocketStream(Socket socket) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream(), socket);
	}

	/**
	 * Updates the input stream and adds stuff to the buffer
	 */
	public void update(){
		if(closed)
			return;
		
		String s;
		while(true && !closed){
			try{
				s = reader.readUTF();
				builder.append(s);
			}catch(EOFException e){
				break;
			}catch(SocketTimeoutException e){
				break;
			}catch(IOException e){
				logger.log(Level.SEVERE, "IOException threw while updating SocketStream for socket " + socket.getInetAddress(), e);
				closed = true;
				
				try {
					this.close();
				} catch (IOException e1) {
					logger.log(Level.FINE, "IOException threw while closing socket " + socket.getInetAddress(), e);
				}
				
				
			}
		}
	}
	
	/**
	 * @return if the buffer has a message inside of it
	 */
	public boolean hasNextMessage(){
		return builder.toString().contains(Resources.EOT);
	}
	
	/**
	 * @return the next message in the buffer. If no message is available it will return null
	 */
	public String getNextMessage(){
		int index = builder.toString().indexOf(Resources.EOT);
		
		//If no message is there return null
		if(index == -1){
			return null;
		}
		
		//Remove it from the buffer
		String s = builder.toString().substring(0, index);
		builder.replace(0, index + 1, "");
		return s;
	}
	
	/**
	 * Writes directly to the output stream
	 * @param s
	 */
	public void write(String s){
		if(closed)
			return;
		
		try {
			writer.writeUTF(s);
			writer.flush();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException threw while writing to the SocketStream for socket " + socket.getInetAddress(), e);
			closed = true;
			
			try {
				this.close();
			} catch (IOException e1) {
				logger.log(Level.FINE, "IOException threw while closing socket " + socket.getInetAddress(), e);
			}
		}
	}
	
	/**
	 * Closes the output stream
	 * @throws IOException
	 */
	public void close() throws IOException {
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

	/**
	 * @return the reader
	 */
	public DataInputStream getReader() {
		return reader;
	}

	/**
	 * @return the writer
	 */
	public DataOutputStream getWriter() {
		return writer;
	}
	
}
