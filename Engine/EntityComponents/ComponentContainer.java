package Engine.EntityComponents;

import java.util.*;

/**
 * The ComponentContainer class represents a container for storing and managing components.
 * It provides methods for adding, removing, and accessing components.
 * @author haotian
 */
public class ComponentContainer implements Iterable<AbstractComponent> {
    private ArrayList<AbstractComponent> components;

    /**
     * Constructs an empty ComponentContainer.
     */
    public ComponentContainer() {
        components = new ArrayList<AbstractComponent>();
    }

    /**
     * Returns an iterator over the components in this container.
     *
     * @return an iterator over the components in this container
     */
    public Iterator<AbstractComponent> iterator() {
        return components.iterator();
    }

    /**
     * Returns the first component of the specified type found in this container.
     *
     * @param c the type of the component to find
     * @return the first component of the specified type found, or null if not found
     */
    public AbstractComponent foundType(AbstractComponent c) {
        for (AbstractComponent component : components) {
            if (component.equals(c)) {
                return component;
            }
        }
        return null;
    }

    /**
     * Returns the component of the specified type found in this container.
     *
     * @param c the type of the component to get
     * @return the component of the specified type found, or null if not found
     */
    public AbstractComponent get(Class<? extends AbstractComponent> c) {
        for (AbstractComponent component : components) {
            if (component.getClass() == c) {
                return component;
            }
        }
        return null;
    }

    /**
     * Adds a component to this container. If a component of the same type already exists, it will be replaced.
     *
     * @param c the component to add
     */
    public void add(AbstractComponent c) {
        AbstractComponent old = foundType(c);
        if (old != null) {
            int index = components.indexOf(old);
            components.set(index, c);
        } else {
            components.add(c);
        }
    }

    /**
     * Removes all components from this container.
     */
    public void clear() {
        components.clear();
    }

    /**
     * Adds a component to this container at the specified index. If a component of the same type already exists, it will be replaced.
     *
     * @param c     the component to add
     * @param index the index at which to add the component
     * @return the replaced component, or null if no component was replaced
     */
    public AbstractComponent add(AbstractComponent c, int index) {
        AbstractComponent old = foundType(c);
        if (old == null) {
            components.add(index, c);
            return null;
        }
        components.remove(old);
        components.add(index, c);
        return old;
    }

    /**
     * Removes a component from this container.
     *
     * @param c the component to remove
     * @return true if the component was removed, false otherwise
     */
    public boolean remove(AbstractComponent c) {
        return components.remove(c);
    }

    /**
     * Removes a component of the specified type from this container.
     *
     * @param c the type of the component to remove
     * @return true if the component was removed, false otherwise
     */
    public boolean remove(Class<? extends AbstractComponent> c) {
        AbstractComponent component = get(c);
        if (component == null) return false;
        return components.remove(component);
    }

    /**
     * Removes the component at the specified index from this container.
     *
     * @param index the index of the component to remove
     * @return the removed component
     */
    public AbstractComponent remove(int index) {
        return components.remove(index);
    }

    /**
     * Returns a string representation of this container.
     *
     * @return a string representation of this container
     */
    public String toString() {
        return components.toString();
    }
}
