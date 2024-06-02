package Engine.EntityComponents.Components;

import Engine.EntityComponents.AbstractComponent;
import Engine.Entities.*;
/**
 * The RegenHealth class represents a component that regenerates the health of an entity over time.
 * It extends the AbstractComponent class.
 * @author joey
 */
public class RegenHealth extends AbstractComponent {

    public final float rate;
    private float lastHealth;
    private long lastTime;

    /**
     * Constructs a new RegenHealth object with the specified parent entity and regeneration rate.
     *
     * @param parent The parent entity of this component.
     * @param rate   The regeneration rate of the health.
     */
    public RegenHealth(Entity parent, float rate) {
        super(parent, true, false);
        this.rate = rate;
        lastHealth = -100000;
    }

    /**
     * Updates the regeneration of the health based on the elapsed time.
     *
     * @param delta The time elapsed since the last update.
     */
    public void update(double delta) {
        if (entity.isLocal) {
            return;
        }
        float currentHealth = ((Brawler) (entity)).getHealth();
        long now = System.currentTimeMillis();
        if (lastHealth == -100000) {
            lastHealth = currentHealth;
        }
        if (rate == 0) return;
        Health h = entity.getComponent(Health.class);

        if (currentHealth < lastHealth) {
            lastTime = System.currentTimeMillis();
        }

        if (h != null) {
            if (now - lastTime > 2000) {
                h.setHealth(h.Health + (float) (rate * delta));
            }
        }
        lastHealth = currentHealth;
    }
}
