package org.inspirerobotics.sumobots.field.display;

import org.inspirerobotics.sumobots.field.FieldBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.networking.Server;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.Message;

import java.util.logging.Logger;

public class DisplayServer extends Server {

    private final Logger log = InternalLog.getLogger();
    private final FieldBackend fieldBackend;
    private Connection connection;

    public DisplayServer(FieldBackend fieldBackend) {
        super(new DisplayListener(), "display_server", Resources.DISPLAY_PORT);
        this.fieldBackend = fieldBackend;
    }

    @Override
    protected void onConnectionCreated(Connection c) {
        if (connection != null) {
            log.severe("Two displays connected at once... Shutting down second connection!");
            c.endConnection();
            return;
        }

        super.onConnectionCreated(c);

        connection = c;
        sendConnectionStatusToFrontend();
    }

    public void update() {
        super.update();

        if(connection == null)
            return;

        if (connection.isClosed()) {
            log.warning("Lost display connection....");
            connection = null;
            sendConnectionStatusToFrontend();
        }
    }

    private void sendConnectionStatusToFrontend() {
        if (connection != null) {
            InterThreadMessage m = new InterThreadMessage("display_connection", true);
            fieldBackend.sendMessageToFrontend(m);
        } else {
            InterThreadMessage m = new InterThreadMessage("display_connection", false);
            fieldBackend.sendMessageToFrontend(m);
        }
    }
}

class DisplayListener implements ConnectionListener {

    @Override
    public void receivedMessage(Message message, Connection connection) {

    }
}