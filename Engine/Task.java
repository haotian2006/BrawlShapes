package Engine;


import java.util.function.*;

import Engine.TaskScheduler.lambda;

/**
 * The Task interface represents a task that can be scheduled and executed.
 * @author haotian
 */
public interface Task { 
    /**
     * Sleeps for the specified number of seconds and then executes the given lambda expression.
     * 
     * @param seconds the number of seconds to sleep
     * @param x the lambda expression to execute after sleeping
     * @return the Task object
     */
    public Task sleep(double seconds, lambda x);
    
    /**
     * Sleeps for the specified number of seconds and then executes the given Consumer with the provided data.
     * 
     * @param seconds the number of seconds to sleep
     * @param x the Consumer to execute after sleeping
     * @param data the data to pass to the Consumer
     * @param <T> the type of the data
     * @return the Task object
     */
    public <T> Task sleep(double seconds, Consumer<T> x, T data);
    
    /**
     * Executes the given Consumer for each element in the provided array, with a delay of the specified number of seconds between each execution.
     * 
     * @param seconds the number of seconds to delay between each execution
     * @param data the array of elements
     * @param x the Consumer to execute for each element
     * @param <T> the type of the elements
     * @return the Task object
     */
    public <T> Task forEach(double seconds, T[] data, Consumer<T> x);
    
    /**
     * Executes the given Consumer for the specified number of iterations, with a delay of the specified number of seconds between each execution.
     * 
     * @param seconds the number of seconds to delay between each execution
     * @param iterations the number of iterations
     * @param x the Consumer to execute for each iteration
     * @return the Task object
     */
    public Task forIter(double seconds, int iterations, Consumer<Integer> x);
}
