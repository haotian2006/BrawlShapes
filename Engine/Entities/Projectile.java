package Engine.Entities;


import Engine.*;
import Engine.Entities.Special.PowerBoxEntity;
import Engine.EntityComponents.Base.ApplyRotation;
import Engine.EntityComponents.Base.HitBox;
import Engine.EntityComponents.Base.RenderImage;
import Engine.EntityComponents.Base.RenderName;
import Engine.EntityComponents.Base.UpdatePosition;
import Engine.EntityComponents.Components.Health;
import Engine.EntityComponents.Components.SuperValue;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.security.PublicKey;
import java.util.UUID;

import Engine.Tile.Damageable;
import Engine.Tile.Tile;
import Engine.Tile.WaterTile;
import MathLib.AABB;
import MathLib.Vector2;


/**
 * Represents a projectile entity in the game.
 * Projectiles are fired by Brawlers and inflict damage to other entities.
 * They are destroyed upon collision with other entities.
 * @author haotian
 */
public class Projectile extends Entity {

    public final Brawler brawler;
    public long timecreated=System.currentTimeMillis();
    public float damage = 5;;

    /**
     * Constructs a new Projectile object with the specified engine and parent Brawler.
     * 
     * @param engine the game engine
     * @param parent the parent Brawler
     */
    public Projectile(Engine engine, Brawler parent) {
        super(engine);
        setScale(.5);

        resize(Vector2.ONE.mul(.3f));
        addComponent(new UpdatePosition(this));
        addComponent(new ApplyRotation(this));
        addComponent(new RenderImage(this));

        brawler = parent;
    }

    /**
     * Constructs a new Projectile object with the specified engine.
     * 
     * @param engine the game engine
     */
    public Projectile(Engine engine) {
        this(engine, null);
    }

    /**
     * Inflicts damage to a Brawler.
     * 
     * @param b the Brawler to damage
     */
    public void doDamage(Brawler b) {
        float dmg = b.takeDamage(damage);
        if (brawler != null) {
            brawler.didDamage(dmg);
        }
    }

    /**
     * Inflicts damage to a Damageable tile.
     * @see Damageable
     * @param b the Damageable object to damage
     */
    public void doDamage(Damageable b) {
        float dmg = b.takeDamage(damage);
        if (brawler != null) {
            // brawler.didDamage(dmg);
        }
    }

    /**
     * Handles collision with another object.
     * 
     * @param o the object to check collision with
     * @return true if collision occurred, false otherwise
     */
    public boolean onCollision(Object o) {
        if (super.onCollision(o)) {
            if (o instanceof Damageable) {
                doDamage((Damageable) o);
            }
            if (o instanceof WaterTile) {
                return false;
            }
            destroy();
            return true;
        }
        return false;
    }

    /**
     * Checks for collision with other entities and performs damage if necessary.
     */
    public void checkEntities() {
        Brawler[] hits = engine.collisionHandler.getBrawlerInBounds(HitBox);
        for (Entity e : hits) {
            if (e != this && e != brawler) {
                Brawler b = (Brawler) e;
                doDamage(b);

                destroy();
                continue;
            }
        }
    }

    /**
     * Updates the projectile's state.
     * 
     * @param delta the time elapsed since the last update
     */
    public void update(double delta) {
        checkEntities();

        super.update(delta);
    }

    /**
     * Gets the image path for the projectile variant.
     * 
     * @return the image path
     */
    public String getImagePathFromVariant() {
        return "Images/Projectiles/RedCircle.png";
    }
}
