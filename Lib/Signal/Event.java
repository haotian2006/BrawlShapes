package Signal;

import java.util.function.Consumer;
/**
 * The Event interface is a helper class for signal. It provides methods for connecting to the signal, once, and await. It also provides a method for retrieving the value of the signal.
 * 
 * @author  haotian
 * @param  <T> the type of the value that is passed to the listener
 */
public interface Event<T> {
    /**
     * Connects a consumer to the signal and returns a SignalConnection object.
     * 
     * @param  callBack  the consumer to be connected to the signal
     * @return           a SignalConnection object representing the connection
     */
    public Connection connect(Consumer<T> callBack);
    /**
     * Connects a consumer to the signal and stores the connection in the container.
     * 
     * @param  callBack  the consumer to be connected to the signal
     * @param  container  the container to store the connection in
     * @return           a SignalConnection object representing the connection
     */
    public Connection connect(Consumer<T> callBack, ConnectionContainer container);
    /**
     * Fires the call back once and disconnects, and stores the connection in the container.
     * 
     * @param  callBack  the consumer to be connected to the signal
     * @param  container  the container to store the connection in
     * @return           a SignalConnection object representing the connection
     */
    public Connection once(Consumer<T> callBack, ConnectionContainer container);
    /**
     * Waits for the signal to be fired and returns the value. If the signal is not fired,
     * it waits until the signal is fired. If the waiting is interrupted, it prints a message
     * and returns null.
     * 
     * @return          the value of the signal, or null if waiting is interrupted
     */
    public T await() ;
    /**
     * Fires the call back once and disconnects.
     * 
     * @param  callBack  the consumer to be connected to the signal
     * @return           a SignalConnection object representing the connection
     */
    public Connection once(Consumer<T> callBack);
}



