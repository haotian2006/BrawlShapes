package Engine.EntityComponents.Components;

import Engine.EntityHandler;
import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import Engine.EntityComponents.ComponentData;
import MathLib.Vector2;

import Engine.EntityComponents.SerializableComponent;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The Health component represents the health of an entity.
 * It keeps track of the current health and the maximum health of the entity.
 * @author haotian
 */
public class Health extends AbstractComponent implements SerializableComponent {

    /**
     * The current health of the entity.
     */
    public float Health;

    /**
     * The maximum health of the entity.
     */
    public  float MaxHealth;

    static {
        EntityHandler.registerComponent(25, Health.class, (component, value) -> {
            Health c = (Health) component;
            c.Health = (float) value;
        });
    }
    static {
        EntityHandler.registerComponent(32, Health.class, (component, value) -> {
            Health c = (Health) component;
            c.MaxHealth = (float) value;
        });
    }

    /**
     * Constructs a new Health component with the specified entity and maximum health.
     *
     * @param entity    the entity this component belongs to
     * @param maxHealth the maximum health of the entity
     */
    public Health(Entity entity, float maxHealth) {
        super(entity, false, true);
        MaxHealth = maxHealth;
        Health = maxHealth;
    }

    /**
     * Sets the health of the entity.
     * The health value is clamped between 0 and the maximum health.
     * If the health value changes, the component is updated in the entity handler.
     *
     * @param health the new health value
     */
    public void setHealth(float health) {
        float last = Health;
        Health = Math.min(MaxHealth, Math.max(0, health));
        if (Health != last)
            entityHandler.setComponent(entity, 25, Health);
    }

    public void setMaxHealth(float maxHealth) {
        float last = MaxHealth;
        MaxHealth = maxHealth;
        if (MaxHealth != last)
            entityHandler.setComponent(entity, 32, MaxHealth);
    }


    /**
     * Replicates the health component data to the specified ComponentData object.
     *
     * @param data the ComponentData object to replicate the data to
     */
    public void replicateEntity(ComponentData data) {
        data.setComponent(25, Health);
        data.setComponent(32, MaxHealth);
    }

    @Override
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords) {
        int Size = entity.getDisplaySize();
        int SizeX = Size;
        int SizeY = (int) (4 * entity.Scale - 1);

        int boundSizeX = (int) (Health / MaxHealth * (SizeX - 1));
        int boundSizeY = SizeY - 1;

        g.setColor(java.awt.Color.BLACK);
        g.drawRect((int) (displayCoords.X), (int) (displayCoords.Y), SizeX, SizeY);
        g.setColor(Color.GREEN);
        if (engine.camera.getEntity() != entity) {
            g.setColor(Color.red);
        }
        g.fillRect((int) (displayCoords.X + 1), (int) (displayCoords.Y + 1), boundSizeX, boundSizeY);
    }
}
