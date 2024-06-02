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
 * Represents a projectile used by the Leon (pentagon) brawler in the game.
 * Inherits from the Projectile class.
 * @see Projectile
 * @author joey
 */
public class LeonProjectile extends Projectile {

    /**
     * Constructs a new LeonProjectile object with the specified engine and parent brawler.
     * 
     * @param engine the game engine
     * @param parent the parent brawler
     */
    public LeonProjectile(Engine engine, Brawler parent) {
        super(engine, parent);
        setScale(1);
    }

    /**
     * Constructs a new LeonProjectile object with the specified engine.
     * 
     * @param engine the game engine
     */
    public LeonProjectile(Engine engine) {
        this(engine, null);
    }

    /**
     * Inflicts damage to a brawler.
     * The damage inflicted is doubled if the projectile was created less than 100 milliseconds ago.
     * 
     * @param b the brawler to inflict damage to
     */
    public void doDamage(Brawler b) {
        float damage1 = damage;
        if (System.currentTimeMillis() - timecreated < 100) {
            damage1 *= 2f;
        }
        float dmg = b.takeDamage(damage1);

        if (brawler != null) {
            brawler.didDamage(dmg);
        }
    }

    /**
     * Inflicts damage to a damageable object.
     * 
     * @param b the damageable object to inflict damage to
     */
    public void doDamage(Damageable b) {
        float dmg = b.takeDamage(damage);
        if (brawler != null) {
            // brawler.didDamage(dmg);
        }
    }

    /**
     * Returns the image path for the projectile variant.
     * 
     * @return the image path for the projectile variant
     */
    public String getImagePathFromVariant() {
        return "Images/Projectiles/PentProjectile.png";
    }
}
