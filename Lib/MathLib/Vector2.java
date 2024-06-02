package MathLib;

import java.util.Arrays;
import java.awt.*;
import java.io.Serializable;
/**
 * A 2D vector class
 * @author Haotian
 */
public class Vector2  implements Serializable {

    /**
     * A vector with all components set to 0
     */
    public static final Vector2 ZERO = new Vector2(0, 0);

    /**
     * A vector with all components set to 1
     */
    public static final Vector2 ONE = new Vector2(1, 1);

    /**
     * A vector pointing along the X axis
     */
    public static final Vector2 X_AXIS = new Vector2(1, 0);

    /**
     * A vector pointing along the Y axis
     */
    public static final Vector2 Y_AXIS = new Vector2(0, 1);

    public final float X;
    public final float Y;

    public Vector2(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    public Vector2() {
        this(0, 0);
    }


    /**
     * Calculates the magnitude of the vector
     * @return the magnitude of the vector
     */
    public float magnitude() {
        return (float) Math.sqrt(X * X + Y * Y);
    }

    /**
     * Adds a vector to this vector
     * @param v the vector to add
     * @return the result of the addition
     */
    public Vector2 add(Vector2 v) {
        return new Vector2(X + v.X, Y + v.Y);
    }

    /**
     * Subtracts a vector from this vector
     * @param v the vector to subtract
     * @return the result of the subtraction
     */
    public Vector2 sub(Vector2 v) {
        return new Vector2(X - v.X, Y - v.Y);
    }

    /**
     * Multiplies a vector by a scalar
     * @param s the scalar to multiply by
     * @return the result of the multiplication
     */
    public Vector2 mul(float s) {
        return new Vector2(X * s, Y * s);
    }

    /**
     * Divides a vector by a scalar
     * @param s the scalar to divide by
     * @return the result of the division
     */
    public Vector2 div(float s) {
        return new Vector2(X / s, Y / s);
    }

    /**
     * Calculates the dot product of two vectors
     * @param v the vector to dot product with
     * @return the dot product of the two vectors
     */
    public float dot(Vector2 v) {
        return X * v.X + Y * v.Y;
    }

    /**
     * Calculates the cross product of two vectors
     * @param v the vector to cross product with
     * @return the cross product of the two vectors
     */
    public float cross(Vector2 v) {
        return X * v.Y - Y * v.X;
    }

    /**
     * Normalizes the vector to a magnitude of 1
     * @return a normalized version of the vector
     */
    public Vector2 normalize() {
        return div(magnitude());
    }

    /**
     * Checks if the vector is NaN
     * @return true if the vector is NaN, false otherwise
     */
    public boolean isNaN(){
        return Float.isNaN(X)||Float.isNaN(Y);
    }

    /**
     * Floors the vector to the nearest integer
     * @return the floored vector
     */
    public Vector2 floor(){
        return new Vector2((int)X, (int)Y);
    }

    /**
     * Calculates the angle to a vector in degrees
     * @param v the vector to calculate the angle to
     * @return the angle to the vector in degrees
     */
    public float angleTo(Vector2 v) {
        float dx = v.X - this.X;
        float dy = v.Y - this.Y;

        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Calculates the distance to a vector
     * @param v the vector to calculate the distance to
     * @return the distance to the vector
     */
    public float distanceTo(Vector2 v) {
        return sub(v).magnitude();
    }

    /**
     * Calculates the relative vector to another vector
     * @param v the vector to calculate the relative vector to
     * @return the relative vector
     */
    public Vector2 relativeTo(Vector2 v) {
        return new Vector2(X - v.X, Y - v.Y);
    }

    /**
     * Inverts the vector
     * @return the inverted vector
     */
    public Vector2 inverse(){
        return new Vector2(-X, -Y);
    }

    @Override
    public int hashCode() {
        return Double.hashCode(Math.pow(X,Float.hashCode(Y)<<2));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector2 other = (Vector2) obj;
    
        return other.X == this.X && other.Y == this.Y;
    }

    @Override
    public String toString() {
        return "Vector2("+ X + ", " + Y + ')';
    }

    /**
     * Creates a vector from the given angle in degrees
     * @param angle the angle to create the vector from in degrees
     * @return the vector from the given angle
     */
    public static Vector2 fromAngle(float angle) {
        double radians = Math.toRadians(angle);

        float x = (float) Math.cos(radians);
        float y = (float) Math.sin(radians);

        return new Vector2(x, y);
    }

    /**
     * Creates a vector from the given point
     * @param p the point to create the vector from
     * @return the vector from the given point
     */
    public static Vector2 fromPoint(Point p){
        return new Vector2(p.x, p.y);
    }

}

