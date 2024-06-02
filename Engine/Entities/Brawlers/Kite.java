package Engine.Entities.Brawlers;

import Engine.Entities.*;
import Engine.Entities.Projectiles.*;
import Engine.EntityComponents.*;
import Engine.EntityComponents.Components.*;
import MathLib.Vector2;

import java.awt.Color;
import java.util.*;
import java.util.function.Consumer;

import Engine.*;

/**
 * Represents a specific type of Brawler called Kite.
 * Kite is a subclass of Brawler and has additional properties and behaviors.
 * Kite is a long-range brawler that can fire projectiles at enemies.
 * @see Brawler
 * @author haotian
 */
public class Kite extends Brawler {
     
    private long lastFired;

    /**
     * Constructs a new Kite object.
     * @param engine The game engine.
     */
private int damage=100;
    public Kite(Engine engine) {
        super(engine);
        addComponent(new Health(this, 551));
        addComponent(new SuperValue(this,damage*5));
        addComponent(new Ammo(this,100,9,30));
    }
    public void powerPointsUpdated(int amt) {
        Health h = getComponent(Health.class);
        h.setMaxHealth(551 + amt*30);
    }

    /**
     * Uses the super ability of the Kite.
     */
    public void useSuper() {
       int damage = 600 + getPowerPoints() * 15;
        TaskScheduler ts = engine.taskScheduler;
        

        Consumer<Integer> addProjectile = integer -> {
            KiteProjectile p = new KiteProjectile(engine, this);
            p.damage = damage*2 ;
            p.setCenter(getCenter());
    
            p.destroyAfter(3);
    
            entityHandler.add(p);
            Vector2 Velocity = Vector2.fromAngle(Angle).mul(80);
            p.setVariant("Super");
            p.setAngle(Angle);
            p.setScale(.8);
            p.resize(new Vector2(.5f, .5f));
            p.setVelocity(Velocity);
        };

        ts.scheduleForTask(.07, 2, addProjectile);
    }

    /**
     * Fires a projectile from the Kite.
     */
    public void fire() {
        if (!canFire() || Destroyed) {
            return;
        }
        lastFired = System.currentTimeMillis();
     float damage = 500+15*getPowerPoints();

        if (isSuperEnabled()) {
            useSuper();
            endSuper();
            return;
        }
        getAmmo().use();
        KiteProjectile p = new KiteProjectile(engine, this);
        p.damage = damage;
        p.setCenter(getCenter());

        p.destroyAfter(1.7);

        entityHandler.add(p);
        Vector2 Velocity = Vector2.fromAngle(Angle).mul(40);
        p.setAngle(Angle);
        p.setScale(.8);
        p.resize(new Vector2(.5f, .5f));
        p.setVelocity(Velocity);
    }

    /**
     * Gets the image path for the Kite based on its variant.
     * @return The image path.
     */
    public String getImagePathFromVariant() {
        return "Images/Entities/Kite.png";
    }

    /**
     * Checks if the Kite can fire a projectile.
     * @return True if the Kite can fire, false otherwise.
     */
    public boolean canFire() {
        if (System.currentTimeMillis() - lastFired > 800 &&  getAmmo().canUse()) {
            return true;
        }
        return false;
    }
}
