package org.inspirerobotics.sumobots.display.field;

import org.inspirerobotics.sumobots.display.DisplayBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class Field implements ConnectionListener {

    private final Logger logger = InternalLog.getLogger();
    private final DisplayBackend displayBackend;
    private Connection fieldConnection;

    public Field(DisplayBackend displayBackend) {
        this.displayBackend = displayBackend;
    }

    public boolean attemptConnection(String ip) {
        logger.fine("Attempting Field Connection...");

        try {
            Socket socket = createSocket(ip);

            if (!socket.isConnected())
                throw new IOException();

            return true;
        } catch (IOException e) {
            logger.fine("E: " + e);
        }
        return false;
    }

    private Socket createSocket(String ip) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(Resources.SOCKET_TIMEOUT);
        socket.connect(new InetSocketAddress(ip, Resources.DISPLAY_PORT), Resources.SOCKET_TIMEOUT);

        return socket;
    }

    public void update(){
        if(connected() == false)
            return;

        fieldConnection.update();
    }

    @Override
    public void receivedMessage(Message message, Connection connection) {

    }

    public void shutdown() {
        if (fieldConnection != null)
            fieldConnection.endConnection();
    }

    public boolean connected() {
        if (fieldConnection == null) {
            return false;
        } else if (fieldConnection.isClosed()) {
            return false;
        }
        return true;
    }
}
