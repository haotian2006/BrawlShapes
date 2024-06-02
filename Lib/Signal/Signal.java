package Signal;


import java.util.LinkedList ;
import java.util.List;
import java.util.function.Consumer;
import java.util.concurrent.locks.*;

/**
 * A signal is a type of event that can be used to notify other parts of the program when something happens.
 * Signals are useful for decoupling code and allowing for greater flexibility.
 * 
 * @author  haotian
 * 
 * @param  <T> the type of the value that is passed to the listener
 */
public class Signal<T> {
	protected class SignalConnection implements Connection {
		private Consumer<T> callBack;
		private Signal<T> parent;
		
		SignalConnection(Signal<T> parent, Consumer<T> callBack) {
			this.parent = parent;
			this.callBack = callBack;
		}
		
		private void invoke(T value) {
			callBack.accept(value);
		}
		
		public void disconnect() {
			parent.disconnectConnection(this);
		}

	}

	
	protected class SignalEvent implements Event<T> {
		private Signal<T> parent;
		
		SignalEvent(Signal<T> parent) {
			this.parent = parent;		
		}
		
		public SignalConnection connect(Consumer<T> callBack) {
			return parent.connect(callBack);
		}
		public T await() {
			return parent.await();
		}

		@SuppressWarnings("unchecked")
		public SignalConnection once(Consumer<T> callBack) {
			Connection[] connectionLocal = new Connection[1];
			//Annoying way of getting this to work
			connectionLocal[0] = parent.connect((value) -> {
				callBack.accept(value);
				connectionLocal[0].disconnect(); 
			});
			return (SignalConnection)connectionLocal[0];
		}


		public SignalConnection once(Consumer<T> callBack, ConnectionContainer container) {
			SignalConnection connection = once(callBack);
			container.add(connection);
			return connection;
		}	

		public Connection connect(Consumer<T> callBack, ConnectionContainer container) {
			Connection connection = connect(callBack);
			container.add(connection);
			return connection;
		}
	}
	
	

	private List<SignalConnection> connections;
	public Event<T> event;
	private Lock lock;
	private Condition await;
	private T value;

	public Signal(){
		lock = new ReentrantLock();
		await = lock.newCondition();
		connections = new LinkedList<SignalConnection>();
		event = new SignalEvent(this);
	}
	
		/**
		 * Connects a consumer to the signal and returns a SignalConnection object.
		 *
		 * @param  callBack  the consumer to be connected to the signal
		 * @return           a SignalConnection object representing the connection
		 */
	public SignalConnection connect(Consumer<T> callBack) {
		SignalConnection connection = new SignalConnection(this,callBack);
		connections.add(connection);
		return connection;
	}

	/**
	 * Waits for the signal to be fired and returns the value. If the signal is not fired,
	 * it waits until the signal is fired. If the waiting is interrupted, it prints a message
	 * and returns null.
	 *
	 * @return         	the value of the signal, or null if waiting is interrupted
	 */
	public T await() {
		lock.lock();
		try {
			await.await();
			return value;
		} catch (InterruptedException e) {
			System.out.println("Signal await interrupted");
			return null;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Sets the value of the signal and notifies all connected SignalConnections.
	 *
	 * @param  value  the new value to set for the signal
	 */
	public void fire(T value) {
		this.value = value;
		lock.lock();
		try {
			this.value = value;
			await.signalAll();
		} finally {
			lock.unlock();
		}
		if(connections.size() == -1) System.out.println("Signal connections size is -1"); ;
		for(SignalConnection connection : connections)
			connection.invoke(value);
	}
	
	/**
	 * Clears all connections in the signal.
	 */
	public void disconnectAll() {
		connections.clear();
	}



		/**
		 * Removes the given connection from the list of connections.
		 *
		 * @param  connection  the connection to be disconnected
		 */
    private void disconnectConnection(SignalConnection connection ) {
		connections.remove(connection);
	}

	
	/**
	 * Overrides the finalize method of the Object class to disconnect all connections
	 * before the object is garbage collected.
	 *
	 * @throws Throwable 	if an error occurs during the finalization process
	 */
	@Override
	protected void finalize() throws Throwable {
		disconnectAll();
	}
	
}



