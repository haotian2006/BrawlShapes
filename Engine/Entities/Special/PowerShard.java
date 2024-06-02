package Engine.Entities.Special;

import Engine.*;
import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import Engine.Entities.SpecialEntity;
import Engine.EntityComponents.Base.ApplyRotation;
import Engine.EntityComponents.Base.RenderImage;
import Engine.EntityComponents.Base.RenderName;
import Engine.EntityComponents.Base.UpdatePosition;
import Engine.EntityComponents.Components.Health;
import Engine.EntityComponents.Components.PowerPoint;
import Engine.EntityComponents.Components.SuperValue;

import java.util.*; 
import MathLib.Vector2;


/**
 * Represents a PowerShard entity in the game.
 * PowerShard is a special entity that can increase the power of a Brawler.
 * @author haotian
 */
public class PowerShard extends Entity implements SpecialEntity {

    Long lastFired;

    /**
     * Constructs a new PowerShard object.
     * 
     * @param engine the game engine
     */
    public PowerShard(Engine engine) {
        super(engine);

        setScale(.5f);

        HitBox.Size = new Vector2(.1f, .1f);
        addComponent(new UpdatePosition(this));
        addComponent(new RenderImage(this));
    }

    /**
     * Gets the image path for the PowerShard based on its variant.
     * 
     * @return the image path
     */
    public String getImagePathFromVariant() {
        return "Images/Entities/Power.png";
    }

    /**
     * Updates the PowerShard's state.
     * If there is a nearby Brawler, it increases the power of the closest Brawler and destroys itself.
     * 
     * @param dt the time elapsed since the last update
     */
    public void update(double dt) {
        if ( lastFired == null ) {
            lastFired = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastFired < 100) {
            return;
        }

        Brawler closest = null;
        float closestDistance = Float.MAX_VALUE;
        for (Brawler e : engine.entityHandler.getAllBrawlers()) {
            float magnitude = getPosition().distanceTo(e.getPosition());
            if (!((Brawler) e).Destroyed && magnitude < 2.5 && magnitude < closestDistance) {
                closest = (Brawler) e;
                closestDistance = magnitude;
            }
        }

        if (closest != null) {
            ((PowerPoint) closest.getComponent(PowerPoint.class)).increment();
            destroy();
            return;
        }
        super.update(dt);
    }
}
