package Signal;

/**
 * A connection represents a connection between a signal and a listener.
 * Connections allow you to disconnect a listener from a signal at any time.
 * @author haotian
 */
public interface Connection {
    /**
     * Disconnects the listener from the signal.
     */
    public void disconnect();
}


