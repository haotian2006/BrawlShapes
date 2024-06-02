package Engine.EntityComponents;

import Engine.Entities.Entity;
import MathLib.Vector2;
import java.awt.Graphics2D;

import Engine.*;

/**
 * The AbstractComponent class represents a base class for all components in the game engine.
 * It provides common functionality and properties that are shared by all components.
 * @author haotian
 */
public abstract class AbstractComponent {

    /**
     * Indicates whether the component is updatable.
     */
    public final boolean updatable;

    /**
     * Indicates whether the component is drawable.
     */
    public final boolean drawable;

    /**
     * The entity that this component belongs to.
     */
    public final Entity entity;

    /**
     * The engine that the entity belongs to.
     */
    public final Engine engine;

    /**
     * The entity handler that manages the entity.
     */
    public final EntityHandler entityHandler;

    /**
     * Constructs a new AbstractComponent object.
     * 
     * @param entity     The entity that this component belongs to.
     * @param updatable  Indicates whether the component is updatable.
     * @param drawable   Indicates whether the component is drawable.
     */
    public AbstractComponent(Entity entity, boolean updatable, boolean drawable){
        this.updatable = updatable;
        this.drawable = drawable;
        this.entity = entity;
        this.engine = entity.engine;
        this.entityHandler = engine.entityHandler;
    }

    /**
     * Returns the hash code value for this component.
     * 
     * @return The hash code value for this component.
     */
    public int hashCode(){
        return getClass().hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj  The reference object with which to compare.
     * @return     true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    /**
     * Updates the component.
     * 
     * @param dt  The time step for the update.
     */
    public void update(double dt){

    };

    /**
     * Draws the component.
     * 
     * @param g              The Graphics2D object used for drawing.
     * @param center         The center position of the component.
     * @param displayCoords  The display coordinates of the component.
     */
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords){

    };

}
