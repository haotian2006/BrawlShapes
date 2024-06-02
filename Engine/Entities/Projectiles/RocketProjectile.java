package Engine.Entities.Projectiles;
//package Engine.Entities.Projectiles;

import Engine.Entities.*;
import Engine.EntityComponents.Base.Shapes.*;
import Engine.EntityComponents.Base.*;
import Engine.EntityComponents.Components.*;
import Engine.Tile.Damageable;

import java.util.*;

import Engine.*;

import java.awt.*;

import MathLib.*;

/**
 * Represents a rocket projectile in the game.
 * Extends the Projectile class.
 * @see Projectile
 * @author joey
 */
public class RocketProjectile extends Projectile {

    /**
     * Constructs a RocketProjectile object with the specified engine and parent brawler.
     * 
     * @param engine The game engine.
     * @param parent The parent brawler that fired the projectile.
     */
    public RocketProjectile(Engine engine, Brawler parent) {
        super(engine, parent);
        setScale(1);
    }

    /**
     * Constructs a RocketProjectile object with the specified engine.
     * 
     * @param engine The game engine.
     */
    public RocketProjectile(Engine engine) {
        this(engine, null);
    }

    /**
     * Inflicts damage to a brawler and nearby brawlers.
     * 
     * @param b The brawler to inflict damage to.
     */
    public void doDamage(Brawler b) {
        float dmg = b.takeDamage(damage);
        brawler.didDamage(dmg);
        for (Brawler c : engine.entityHandler.getAllBrawlers()) {
            if (this.getPosition().distanceTo(c.getPosition()) < 1 && c != brawler && c != b) {
                b.takeDamage(dmg * 0.5f);
                brawler.didDamage(dmg * 0.5f);
            }
        }
    }

    /**
     * Inflicts damage to a damageable object and nearby brawlers.
     * 
     * @param b The damageable object to inflict damage to.
     */
    public void doDamage(Damageable b) {
        float dmg = b.takeDamage(damage);
        if (brawler != null) {
            for (Brawler c : engine.entityHandler.getAllBrawlers()) {
                if (this.getPosition().distanceTo(c.getPosition()) < 1 && c != brawler && c != b) {
                    b.takeDamage(dmg * 0.5f);
                    brawler.didDamage(dmg * 0.5f);
                }
            }
        }
    }

    /**
     * Returns the image path for the rocket projectile variant.
     * 
     * @return The image path for the rocket projectile variant.
     */
    public String getImagePathFromVariant() {
        return "Images/Projectiles/RocketProjectile.png";
    }
}
