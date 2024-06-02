package Signal;

import java.util.*;

/**
 * A utility class for managing connections. It allows you to add connections and disconnect all of them at once.
 * @author haotian
 */
public class ConnectionContainer {
    private List<Connection> connections = new LinkedList<Connection>();

    /**
     * Adds a connection to the list of connections.
     * @param connection the connection to be added
     */
    public void add(Connection connection) {
        connections.add(connection);
    }

    /**
     * Disconnects all of the connections in the list.
     */
    public void disconnectAll() {
        for (Connection connection : connections) {
            connection.disconnect();
        }

        connections.clear();
    }
}

