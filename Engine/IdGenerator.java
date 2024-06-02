package Engine;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The IdGenerator class is responsible for generating unique IDs.
 * It keeps track of used IDs and provides the next available ID when requested.
 * IDs can be pushed to the generator to be reused later.
 * If the generator runs out of pushed IDs, it will generate new IDs starting from a specified lowest value.
 * 
 * The generator supports both short and int IDs.
 * 
 * @author joey
 */
public class IdGenerator {
    private static int LOWEST_VALUE = 500;
    private short currentIndex = Short.MIN_VALUE;
    private Queue<Short> holder = new LinkedList<Short>();

    /**
     * Pushes a short ID to the generator.
     * The pushed ID will be added to the list of available IDs.
     *
     * @param id The short ID to be pushed.
     */
    public void push(short id) {
        holder.add(id);
    }

    /**
     * Pushes an int ID to the generator.
     * The pushed ID will be added to the list of available IDs.
     *
     * @param id The int ID to be pushed.
     */
    public void push(int id) {
        holder.add((short) id);
    }

    /**
     * Retrieves the next available ID from the generator.
     * If there are pushed IDs available, the next pushed ID will be returned.
     * Otherwise, a new ID will be generated starting from the lowest value.
     *
     * @return The next available ID.
     */
    public short getNext() {
        if (holder.size() < LOWEST_VALUE || holder.isEmpty()) {
            currentIndex++;
            return currentIndex;
        } else {
            return holder.remove();
        }
    }
}
