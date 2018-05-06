package org.inspirerobotics.sumobots.driverstation.backend;

import org.inspirerobotics.sumobots.driverstation.DriverStationBackend;
import org.inspirerobotics.sumobots.driverstation.Settings;
import org.inspirerobotics.sumobots.driverstation.util.EmptyConnection;
import org.inspirerobotics.sumobots.library.Resources;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.connection.ConnectionListener;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;
import org.inspirerobotics.sumobots.library.networking.message.Message;
import org.inspirerobotics.sumobots.library.networking.message.MessageType;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Field implements ConnectionListener {

    private final Logger logger = Logger.getLogger(Resources.LOGGER_NAME);

    private Connection fieldConnection;
    private TimePeriod currentPeriod;
    private final DriverStationBackend backend;

    public Field(DriverStationBackend b) {
        backend = b;
    }

    public boolean tryToCreateConnection(String ip) {
        if (Settings.nonFieldMode) {
            setFieldConnection(new EmptyConnection());
            return true;
        }

        try {
            Socket socket = new Socket(ip, Resources.SERVER_PORT);
            setFieldConnection(new Connection(socket, this));
            logger.info("Found connection!");
            return true;
        } catch (IOException e) {
            //TODO we should probably throw here and catch lower in the stack
        }
        return false;
    }

    @Override
    public void receivedMessage(Message message, Connection connection) {
        MessageType type = message.getType();

        if (type == MessageType.MATCH_STATE_UPDATE) {
            updateMatchStatus(message);
        }
    }

    public void update() {
        fieldConnection.update();

        updateNetworkingTable();
    }

    private void updateNetworkingTable() {
        if (fieldConnection != null) {
            if (fieldConnection.getSocket() != null) {
                fieldConnection.getTable().put("ping", fieldConnection.getCurrentPing() + " ms");
                fieldConnection.getTable().put("connection name", fieldConnection.getConnectionName());
                fieldConnection.getTable().put("ip", fieldConnection.getSocket().getLocalAddress().toString());
            }
        }
    }

    public void setDriverStationName(String name) {
        fieldConnection.sendMessage(ArchetypalMessages.setName(name));
    }

    public void updateMatchStatus(Message message) {
        String timePeriod = (String) message.getData("new_period");
        currentPeriod = TimePeriod.fromString(timePeriod);

        logger.info("Field entering match period: " + currentPeriod.getName());
        backend.sendMessageToFrontend(new InterThreadMessage("new_period", currentPeriod));
    }

    public void shutdown() {
        if (fieldConnection != null)
            fieldConnection.endConnection();
    }

    public Connection getFieldConnection() {
        return fieldConnection;
    }

    public void setNetworkingTable(NetworkTable networkingTable) {
        fieldConnection.setBindedTable(networkingTable);
    }

    public void setFieldConnection(Connection fieldConnection) {
        this.fieldConnection = fieldConnection;
    }

    public TimePeriod getCurrentPeriod() {
        return currentPeriod;
    }
}