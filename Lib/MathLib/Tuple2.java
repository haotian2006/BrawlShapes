package MathLib;

/**
 * A class for representing a pair of objects
 * @param <X> the type of the first object
 * @param <Y> the type of the second object
 * @author Haotian
 */
public class Tuple2<X,Y> {
    /**
     * The first item in the tuple
     */
    public X X;
    
    /**
     * The second item in the tuple
     */
    public Y Y;

    /**
     * Constructor to create a tuple with two items
     * @param x the first item in the tuple
     * @param y the second item in the tuple
     */
    public Tuple2(X x, Y y) {
        this.X = x;
        this.Y = y;
    }

    /**
     * Default constructor to create a tuple with two null items
     */
    public Tuple2() {
        this(null, null);
    }
}

