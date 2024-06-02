package EasingLib;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * The Tween class provides easing functions for animations. It uses reflection to
 * invoke the appropriate easing methods based on the specified easing function and type.
 * @author haotian
 */
public class Tween {
    
    /** A lookup table that caches methods for different easing functions and types. */
    private static HashMap<EasingFunction, HashMap<EasingType, Method>> LookUpTable = new HashMap<>();

    /** The easing method to be used for this tween. */
    private Method method;

    /**
     * Constructs a Tween object with the specified easing function and type.
     *
     * @param func The easing function to use.
     * @param type_ The type of easing to apply.
     */
    public Tween(EasingFunction func, EasingType type_) {
        method = getMethod(func, type_);
    }

    /**
     * Gets the interpolated value at the specified alpha value.
     *
     * @param start The starting value of the tween.
     * @param goal The goal value of the tween.
     * @param alpha The normalized time (0 to 1) of the tween.
     * @param duration The duration of the tween.
     * @return The interpolated value.
     */
    public float getValue(float start, float goal, float alpha, float duration) {
        try {
            return (float) method.invoke(null, alpha, start, goal - start, duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the interpolated value at the specified alpha value with a default duration of 1.
     *
     * @param start The starting value of the tween.
     * @param goal The goal value of the tween.
     * @param alpha The normalized time (0 to 1) of the tween.
     * @return The interpolated value.
     */
    public float getValue(float start, float goal, float alpha) {
        return getValue(start, goal, alpha, 1);
    }

    /**
     * Retrieves the method for the specified easing function and type, caching it if necessary.
     *
     * @param func The easing function.
     * @param type_ The type of easing.
     * @return The method for the specified function and type.
     */
    private static Method getMethod(EasingFunction func, EasingType type_) {
        if (!LookUpTable.containsKey(func)) {
            LookUpTable.put(func, new HashMap<>());
        }
        if (!LookUpTable.get(func).containsKey(type_)) {
            try {
                Class<?> myClass = Class.forName("EasingLib." + func.name());
                Method method = myClass.getMethod(type_.name(), float.class, float.class, float.class, float.class);
                LookUpTable.get(func).put(type_, method);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return LookUpTable.get(func).get(type_);
    }

    /**
     * Static method to get the interpolated value using the specified easing function and type.
     *
     * @param func The easing function to use.
     * @param type The type of easing to apply.
     * @param start The starting value of the tween.
     * @param goal The goal value of the tween.
     * @param alpha The normalized time (0 to 1) of the tween.
     * @param duration The duration of the tween.
     * @return The interpolated value.
     */
    public static float getValue(EasingFunction func, EasingType type, float start, float goal, float alpha, float duration) {
        Method method = getMethod(func, type);
        if (method == null) {
            return 0;
        }
        try {
            return (float) method.invoke(null, alpha, start, goal - start, duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
