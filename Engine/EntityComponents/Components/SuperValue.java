package Engine.EntityComponents.Components;

import Engine.Engine;
import Engine.EntityHandler;
import Engine.Entities.Entity;
import Engine.EntityComponents.*;
import MathLib.Vector2;


import java.awt.*;

/**
 * Represents a component that stores a super amount and an enabled state for an entity.
 * @author haotian
 */
public class SuperValue extends AbstractComponent implements SerializableComponent {

    public float superAmt = 0;
    public boolean enabled = false;
    public final int totalNeeded;

    /**
     * Static initializer block that registers the component with the entity handler.
     */
    static {
        EntityHandler.registerComponent(26, SuperValue.class, (component, value) -> {
            SuperValue c = (SuperValue) component;
            c.superAmt = (float) value;
        });

        EntityHandler.registerComponent(28, SuperValue.class, (component, value) -> {
            SuperValue c = (SuperValue) component;
            c.enabled = (boolean) value;
        });
    }

    /**
     * Constructs a new SuperValue component with the specified entity and total needed value.
     *
     * @param entity The entity associated with this component.
     * @param needed The total needed value.
     */
    public SuperValue(Entity entity, int needed) {
        super(entity, false, Engine.DEBUGGING);
        totalNeeded = needed;
    }

    /**
     * Sets the super amount to the specified value.
     *
     * @param value The new super amount value.
     */
    public void setSuper(float value) {
        float old = superAmt;
        value = Math.max(Math.min(value, totalNeeded), 0);
        superAmt = value;
        if (old == superAmt)
            return;

        entityHandler.setComponent(entity, 26, superAmt);
    }

    /**
     * Increments the super amount by the specified value.
     *
     * @param value The value to increment the super amount by.
     */
    public void incrementSuper(float value) {
        setSuper(superAmt + value);
    }

    /**
     * Sets the enabled state of this component.
     *
     * @param value The new enabled state value.
     */
    public void setSuperBool(boolean value) {
        if (value == enabled)
            return;
        enabled = value;
        entityHandler.setComponent(entity, 28, enabled);
    }

    /**
     * Replicates the component data to the specified ComponentData object.
     *
     * @param data The ComponentData object to replicate the data to.
     */
    public void replicateEntity(ComponentData data) {
        data.setComponent(26, superAmt);
        data.setComponent(28, enabled);
    }

    /**
     * Draws the component on the specified Graphics2D object.
     *
     * @param g             The Graphics2D object to draw on.
     * @param center        The center position of the entity.
     * @param displayCoords The display coordinates of the entity.
     */
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords) {
        int Size = entity.getDisplaySize();
        int SizeX = Size;
        int SizeY = (int) (4 * entity.Scale - 1);

        int boundSizeX = (int) (superAmt / totalNeeded * (SizeX - 1));
        int boundSizeY = SizeY - 1;

        g.setColor(java.awt.Color.BLACK);
        g.drawRect((int) (displayCoords.X), (int) (displayCoords.Y - 40), SizeX, SizeY);
        g.setColor(java.awt.Color.cyan);

        g.fillRect((int) (displayCoords.X + 1), (int) (displayCoords.Y - 39), boundSizeX, boundSizeY);
    }
}
