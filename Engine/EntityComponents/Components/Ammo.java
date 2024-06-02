package Engine.EntityComponents.Components;

import Engine.EntityComponents.AbstractComponent;
import MathLib.Vector2;
import Engine.Engine;
import Engine.EntityHandler;
import Engine.Entities.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The Ammo class represents the ammunition component of an entity.
 * It keeps track of the current ammo, maximum ammo, and the rate at which ammo is consumed and replenished.
 * @author haotian
 */
public class Ammo extends AbstractComponent {

    /**
     * The rate at which ammo is replenished per second.
     */
    public final float rate;

    /**
     * The maximum amount of ammo.
     */
    public final float max;

    /**
     * The rate at which ammo is consumed per use.
     */
    public final float useRate;

    /**
     * The current amount of ammo.
     */
    public float current;

    static {
        EntityHandler.registerComponent(30, Ammo.class, (obj, value) -> {
            float a = (float) value;
            obj.current = a;
        });
    }

    /**
     * Constructs a new Ammo object with the specified parameters.
     *
     * @param parent   the parent entity
     * @param max      the maximum amount of ammo
     * @param rate     the rate at which ammo is replenished per second
     * @param useRate  the rate at which ammo is consumed per use
     */
    public Ammo(Entity parent, float max, float rate, float useRate) {
        super(parent, true, Engine.DEBUGGING);
        this.rate = rate;
        this.max = max;
        current = max;
        this.useRate = useRate;
    }

    /**
     * Checks if there is enough ammo to use.
     *
     * @return true if there is enough ammo, false otherwise
     */
    public boolean canUse() {
        return current > useRate;
    }

    /**
     * Uses ammo by reducing the current amount.
     */
    public void use() {
        setAmmo(current - useRate);
    }

    /**
     * Sets the current amount of ammo.
     *
     * @param value the new value for the current ammo
     */
    public void setAmmo(float value) {
        float last = current;
        current = Math.min(max, value);
        if (current != last)
            entityHandler.setComponent(entity, 30, current);
    }

    /**
     * Updates the current amount of ammo based on the elapsed time.
     *
     * @param delta the elapsed time in seconds
     */
    public void update(double delta) {
        setAmmo(current + (float) (rate * delta));
    }

    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords) {
        int Size = entity.getDisplaySize();
        int SizeX = Size;
        int SizeY = (int) (4 * entity.Scale - 1);

        int boundSizeX = (int) (current / max * (SizeX - 1));
        int boundSizeY = SizeY - 1;

        g.setColor(java.awt.Color.BLACK);
        g.drawRect((int) (displayCoords.X), (int) (displayCoords.Y - 60), SizeX, SizeY);
        g.setColor(java.awt.Color.yellow);

        g.fillRect((int) (displayCoords.X + 1), (int) (displayCoords.Y - 59), boundSizeX, boundSizeY);
    }

}
