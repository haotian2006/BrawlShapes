package Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.text.TabStop;

import MathLib.Vector2;
import Signal.Event;
import Signal.Signal;

/**
 * The TaskScheduler class provides a way to schedule and execute tasks at specific intervals.
 * It uses a priority queue to manage the tasks based on their delay time.
 * @author haotian
 */
public class TaskScheduler {
    public final int clockHZ;
    private ScheduledExecutorService loop;
    private long timePassed = 0;

    private boolean isLooping = false;

    private Stack<Task> taskStack = new Stack<Task>();

    private static Signal<Double> PreRenderSignal = new Signal<Double>();
    private static Signal<Double> PreSimulationSignal = new Signal<Double>();
    private static Signal<Double> PostSimulationSignal = new Signal<Double>();

    public static final Event<Double> PreRender = PreRenderSignal.event;
    public static final Event<Double> PreSimulation = PreSimulationSignal.event;
    public static final Event<Double> PostSimulation = PostSimulationSignal.event;

    private PriorityQueue<SchedulerTask> tasksPQ = new PriorityQueue<SchedulerTask>(1000, (a, b) -> (int) (a.delay - b.delay));

    private Engine engine;
    private boolean running = true;

    /**
     * Functional interface for lambda expressions used in tasks.
     */
    @FunctionalInterface
    public interface lambda {
        /**
         * Executes the lambda expression.
         */
        public void run();
    }

    /**
     * Represents a task that can be scheduled and executed by the TaskScheduler.
     */
    @SuppressWarnings("unchecked")
    public class SchedulerTask implements Task {
        protected double delay;
        protected lambda task;
        protected TaskScheduler ts;
        protected SchedulerTask nextTask;

        /**
         * Constructs a SchedulerTask with the specified delay and lambda expression.
         * 
         * @param ts      The TaskScheduler instance.
         * @param seconds The delay in seconds before executing the task.
         * @param x       The lambda expression to execute.
         */
        protected SchedulerTask(TaskScheduler ts, double seconds, lambda x) {
            this.ts = ts;
            delay = seconds;
            task = x;
        }

        /**
         * Constructs a SchedulerTask with the specified delay.
         * 
         * @param ts      The TaskScheduler instance.
         * @param seconds The delay in seconds before executing the task.
         */
        protected SchedulerTask(TaskScheduler ts, double seconds) {
            this.ts = ts;
            delay = seconds;
        }

        /**
         * Initializes the task by setting the delay time.
         */
        protected void init() {
            delay = ts.timePassed + delay * 1000;
        }

        /**
         * Executes the task and adds the next task if available.
         */
        protected void run() {
            task.run();
            if (nextTask != null) {
                ts.addTask(nextTask);
                nextTask = null;
            }
        }

        /**
         * Gets the delay time of the task.
         * 
         * @return The delay time in seconds.
         */
        public double getDelay() {
            return delay;
        }

        /**
         * Creates and schedules a new SchedulerTask with the specified delay and lambda expression.
         * 
         * @param seconds The delay in seconds before executing the task.
         * @param x       The lambda expression to execute.
         * @return The new SchedulerTask instance.
         */
        public SchedulerTask sleep(double seconds, lambda x) {
            nextTask = new SchedulerTask(ts, seconds, x);
            return nextTask;
        }

        /**
         * Creates and schedules a new SchedulerTask with the specified delay, consumer function, and data.
         * 
         * @param <T>     The type of the data.
         * @param seconds The delay in seconds before executing the task.
         * @param x       The consumer function to execute.
         * @param data    The data to pass to the consumer function.
         * @return The new SchedulerTaskParam instance.
         */
        public <T> Task sleep(double seconds, Consumer<T> x, T data) {
            nextTask = new SchedulerTaskParam<T>(ts, seconds, x, data);
            return (SchedulerTaskParam<T>) nextTask;
        }

        /**
         * Creates and schedules a new ForEachTask with the specified delay, data array, and consumer function.
         * 
         * @param <T>     The type of the data.
         * @param seconds The delay in seconds before executing the task.
         * @param data    The array of data elements.
         * @param x       The consumer function to execute for each data element.
         * @return The new ForEachTask instance.
         */
        public <T> ForEachTask<T> forEach(double seconds, T[] data, Consumer<T> x) {
            nextTask = new ForEachTask<T>(ts, seconds, data, x, 0);
            return (ForEachTask<T>) nextTask;
        }

        /**
         * Creates and schedules a new ForTask with the specified delay, number of iterations, and consumer function.
         * 
         * @param seconds    The delay in seconds before executing the task.
         * @param iterations The number of iterations.
         * @param x          The consumer function to execute for each iteration.
         * @return The new ForTask instance.
         */
        public ForTask forIter(double seconds, int iterations, Consumer<Integer> x) {
            nextTask = new ForTask(ts, seconds, iterations, x);
            return (ForTask) nextTask;
        }
    }

    /**
     * Represents a SchedulerTask with a parameter.
     *
     * @param <T> The type of the parameter.
     */
    public class SchedulerTaskParam<T> extends SchedulerTask {
        protected T param;
        protected Consumer<T> task2;

        /**
         * Constructs a SchedulerTaskParam with the specified delay, consumer function, and parameter.
         * 
         * @param ts      The TaskScheduler instance.
         * @param seconds The delay in seconds before executing the task.
         * @param x       The consumer function to execute.
         * @param data    The parameter to pass to the consumer function.
         */
        protected SchedulerTaskParam(TaskScheduler ts, double seconds, Consumer<T> x, T data) {
            super(ts, seconds, null);
            task2 = x;
            this.param = data;
        }

        /**
         * Executes the task with the parameter and adds the next task if available.
         */
        protected void run() {
            task2.accept(param);
            if (nextTask != null) {
                ts.addTask(nextTask);
                nextTask = null;
            }
        }
    }

    /**
     * Represents a SchedulerTask for iterating a specific number of times.
     */
    public class ForTask extends SchedulerTaskParam<Integer> {
        protected int currentIter = 0;
        protected double initTime;

        /**
         * Constructs a ForTask with the specified delay, number of iterations, and consumer function.
         * 
         * @param ts         The TaskScheduler instance.
         * @param seconds    The delay in seconds before executing the task.
         * @param iter       The number of iterations.
         * @param x          The consumer function to execute for each iteration.
         */
        public ForTask(TaskScheduler ts, double seconds, int iter, Consumer<Integer> x) {
            super(ts, seconds, x, iter);
            if (iter <= 0)
                throw new IllegalArgumentException("iterations must be bigger or equal to 1");

            initTime = seconds;
            currentIter = iter;
        }

        /**
         * Executes the task with the current iteration count and adds the next task if available.
         */
        protected void run() {
            task2.accept(currentIter);
            if (currentIter <= 1) {
                if (nextTask != null) {
                    ts.addTask(nextTask);
                    nextTask = null;
                }
            } else {
                ForTask t = new ForTask(ts, initTime, --currentIter, task2);
                t.nextTask = nextTask;
                ts.addTask(t);
            }
        }
    }

    /**
     * Represents a SchedulerTask for iterating over an array of data elements.
     *
     * @param <T> The type of the data elements.
     */
    public class ForEachTask<T> extends SchedulerTask {
        protected Consumer<T> task;
        protected T[] data;
        protected int index = 0;
        protected double initTime;

        /**
         * Constructs a ForEachTask with the specified delay, data array, consumer function, and starting index.
         * 
         * @param ts      The TaskScheduler instance.
         * @param seconds The delay in seconds before executing the task.
         * @param data    The array of data elements.
         * @param x       The consumer function to execute for each data element.
         * @param idx     The starting index in the data array.
         */
        protected ForEachTask(TaskScheduler ts, double seconds, T[] data, Consumer<T> x, int idx) {
            super(ts, seconds);
            task = x;
            index = idx;
            this.data = data;
            initTime = seconds;
        }

        /**
         * Initializes the task by setting the delay time and handling empty data array.
         */
        protected void init() {
            delay = ts.timePassed + delay * 1000;
            if (data.length <= 0) {
                delay = ts.timePassed;
            }
        }

        /**
         * Executes the task with the current data element and adds the next task if available.
         */
        protected void run() {
            if (index >= data.length) {
                if (nextTask != null) {
                    ts.addTask(nextTask);
                    nextTask = null;
                }
                return;
            }

            task.accept(data[index]);
            if (index >= data.length - 1 && nextTask != null) {
                ts.addTask(nextTask);
                nextTask = null;
            } else {
                ForEachTask<T> t = new ForEachTask<T>(ts, initTime, data, task, ++index);
                t.nextTask = nextTask;
                ts.addTask(t);
            }
        }
    }

    /**
     * Constructs a TaskScheduler with the specified engine and default clockHZ value of 30.
     * 
     * @param engine The Engine instance.
     */
    protected TaskScheduler(Engine engine) {
        this(engine, 30);
    }

    /**
     * Constructs a TaskScheduler with the specified engine and clockHZ value.
     * 
     * @param engine  The Engine instance.
     * @param clockHZ The clock frequency in Hertz.
     */
    protected TaskScheduler(Engine engine, int clockHZ) {
        this.engine = engine;
        this.clockHZ = clockHZ;
        loop = Executors.newSingleThreadScheduledExecutor();

        start();
    }

    /**
     * Adds a task to the task queue.
     * 
     * @param t The task to add.
     */
    private void addTask(SchedulerTask t) {
        if (isLooping) {
            taskStack.add(t);
            return;
        }

        t.init();
        tasksPQ.add(t);
    }

    /**
     * Pauses the task scheduler.
     */
    public void pause() {
        running = false;
    }

    /**
     * Resumes the task scheduler.
     */
    public void resume() {
        running = true;
    }

    /**
     * Stops the task scheduler.
     */
    public void stop() {
        running = false;
        loop.shutdown();
    }

    /**
     * Schedules a task with the specified delay and lambda expression.
     * 
     * @param seconds The delay in seconds before executing the task.
     * @param x       The lambda expression to execute.
     * @return The scheduled SchedulerTask instance.
     */
    public SchedulerTask scheduleTask(double seconds, lambda x) {
        SchedulerTask t = new SchedulerTask(this, seconds, x);
        addTask(t);
        return t;
    }

    /**
     * Schedules a task with the specified delay, consumer function, and data.
     * 
     * @param <T>     The type of the data.
     * @param seconds The delay in seconds before executing the task.
     * @param x       The consumer function to execute.
     * @param data    The data to pass to the consumer function.
     * @return The scheduled SchedulerTaskParam instance.
     */
    public <T> SchedulerTaskParam<T> scheduleTask(double seconds, Consumer<T> x, T data) {
        SchedulerTaskParam<T> t = new SchedulerTaskParam<T>(this, seconds, x, data);
        addTask(t);
        return t;
    }

    /**
     * Schedules a task to iterate over an array of data elements.
     * 
     * @param <T>     The type of the data.
     * @param seconds The delay in seconds before executing the task.
     * @param data    The array of data elements.
     * @param x       The consumer function to execute for each data element.
     * @return The scheduled ForEachTask instance.
     */
    public <T> ForEachTask<T> scheduleForEachTask(double seconds, T[] data, Consumer<T> x) {
        ForEachTask<T> t = new ForEachTask<T>(this, seconds, data, x, 0);
        addTask(t);
        return t;
    }

    /**
     * Schedules a task to iterate a specific number of times.
     * 
     * @param seconds    The delay in seconds before executing the task.
     * @param iterations The number of iterations.
     * @param x          The consumer function to execute for each iteration.
     * @return The scheduled ForTask instance.
     */
    public ForTask scheduleForTask(double seconds, int iterations, Consumer<Integer> x) {
        ForTask t = new ForTask(this, seconds, iterations, x);
        addTask(t);
        return t;
    }

    /**
     * Starts the task scheduler in a separate thread.
     */
    private void start() {
        Thread thread = new Thread(() -> {
            int iters = 0;
            long preRenderTime = System.nanoTime();
            long preSimulationTime = System.nanoTime();
            long simulationTime = System.nanoTime();
           try {
            while (running) {
                long start = System.currentTimeMillis();

                long currentTime = System.nanoTime();
                double deltaTime = (double) (currentTime - preRenderTime) / 1_000_000_000.0;
                preRenderTime = currentTime;
                boolean pass = iters > 60;

                if (!pass) {
                    iters++;
                }

                if (pass) {
                    PreRenderSignal.fire(deltaTime);
                    engine.onRender(deltaTime);
                }

                currentTime = System.nanoTime();
                deltaTime = (double) (currentTime - preSimulationTime) / 1_000_000_000.0;
                preSimulationTime = currentTime;

                if (pass) {
                    PreSimulationSignal.fire(deltaTime);
                    engine.onUpdate(deltaTime);
                }

                currentTime = System.nanoTime();
                deltaTime = (double) (currentTime - simulationTime) / 1_000_000_000.0;
                simulationTime = currentTime;
                if (pass) {
                    PostSimulationSignal.fire(deltaTime);
                }

                isLooping = true;
                while (!tasksPQ.isEmpty() && tasksPQ.peek().delay < timePassed) {
                    tasksPQ.poll().run();
                }

                isLooping = false;

                for (Task t : taskStack) {
                    addTask((SchedulerTask) t);
                }
                taskStack.clear();

                try {
                    Thread.sleep(1000 / clockHZ);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                timePassed += (System.currentTimeMillis() - start);
            }
           }catch (Exception e) {
               e.printStackTrace();
           }
        });
        thread.start();
    }
}
