package Engine.EntityComponents.Components;

import java.awt.Color;

import Engine.EntityHandler;
import Engine.Entities.Entity;
import Engine.EntityComponents.ComponentData;
import MathLib.Vector2;

import Engine.EntityComponents.SerializableComponent;
import Engine.EntityComponents.AbstractComponent;

/**
 * The ColorComponent class represents a component that stores color information for an entity.
 * It implements the SerializableComponent interface to support serialization.
 * @author haotian
 */
public class ColorComponent extends AbstractComponent implements SerializableComponent {

    /**
     * The color value stored in this component.
     */
    public Color color;

    /**
     * Static initializer block that registers the ColorComponent class with the EntityHandler.
     * This allows the component to be serialized and deserialized correctly.
     */
    static {
        EntityHandler.registerComponent(29, ColorComponent.class, (component, value) -> {
            ColorComponent c = (ColorComponent) component;
            c.color = (Color) value;
        });
    }

    /**
     * Constructs a ColorComponent for the specified entity.
     *
     * @param entity the entity associated with this component
     */
    public ColorComponent(Entity entity) {
        super(entity, false, false);
    }

    /**
     * Constructs a ColorComponent for the specified entity with the given color.
     *
     * @param entity the entity associated with this component
     * @param color  the color value to set
     */
    public ColorComponent(Entity entity, Color color) {
        super(entity, false, false);
        this.color = color;
    }

    /**
     * Sets the color value of this component.
     *
     * @param color the color value to set
     */
    public void setColor(Color color) {
        this.color = color;
        entityHandler.setComponent(entity, 29, color);
    }

    /**
     * Returns the color value stored in this component.
     *
     * @return the color value
     */
    public Color getColor() {
        return color;
    }

    /**
     * Replicates the entity's color component data to the specified ComponentData object.
     *
     * @param data the ComponentData object to replicate the data to
     */
    public void replicateEntity(ComponentData data) {
        data.setComponent(29, color);
    }
}
