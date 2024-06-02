package Engine.Entities.Brawlers;

import Engine.Engine;
import Engine.TaskScheduler;
import Engine.Entities.*;
import Engine.Entities.Projectiles.CircleProjectile;
import Engine.Entities.Projectiles.RocketProjectile;
import Engine.EntityComponents.*;
import Engine.EntityComponents.Components.Ammo;
import Engine.EntityComponents.Components.ColorComponent;
import Engine.EntityComponents.Components.Health;
import Engine.EntityComponents.Components.SuperValue;
import MathLib.Vector2;

import java.awt.Color;
import java.util.*;
import java.util.function.Consumer;

/**
 * The Square class represents a specific type of Brawler called Square.
 * It extends the Brawler class and provides additional functionality and behavior specific to the Square Brawler.
 * The Square is a tanky brawler that can fire powerful projectiles at enemies.
 * @see Brawler
 * @author joey
 */
public class Square extends Brawler {
    private long lastFired;

    /**
     * Constructs a new Square object with the specified Engine.
     *
     * @param engine The Engine object associated with the Square.
     */
   private int damage = 350 ;

    public Square(Engine engine) {
        super(engine);
        addComponent(new Health(this, 1200));
            addComponent(new SuperValue(this,damage*4));
            addComponent(new Ammo(this,100,15,33));

    }

    public void powerPointsUpdated(int amt) {
        Health h = getComponent(Health.class);
        h.setMaxHealth(1200 + amt*50);
    }


    /**
     * Fires a projectile from the Square.
     * The Square can only fire if it is not destroyed and enough time has passed since the last firing.
     * The damage of the projectile is determined by the Square's power points.
     * If the Square's super ability is enabled, it uses the super ability and fires multiple projectiles.
     */
    public void fire() {
        if (!canFire() || Destroyed) {
            return;
        }
        int damage = 350 + getPowerPoints() * 25;
        lastFired = System.currentTimeMillis();

        if (isSuperEnabled()) {
            useSuper();
            endSuper();
            return;
        }


        Projectile p = new RocketProjectile(engine, this);
        p.setAngle(this.Angle);
        p.setCenter(getCenter());
        p.setVelocity(Vector2.fromAngle(Angle).mul(20));

        if (isSuperEnabled()) {
            useSuper();
            endSuper();
            return;
        }

        Vector2 Velocity = Vector2.fromAngle(Angle).mul(20);

        TaskScheduler ts = engine.taskScheduler;

        Consumer<Projectile> addProjectile = proj -> {
            proj.setCenter(getCenter());
            proj.setVelocity(Velocity);
            proj.damage = damage;
            entityHandler.add(proj);
            proj.destroyAfter(.35);
        };

        addProjectile.accept(p);
    }

    /**
     * Uses the super ability of the Square.
     * It fires multiple projectiles in different directions.
     * Each projectile has a fixed damage value.
     */
    public void useSuper() {
        int damage = 200+getPowerPoints()*25;
        Projectile p = new RocketProjectile(engine, this);
        p.damage = damage;
        Projectile p1 = new RocketProjectile(engine, this);
        p1.damage = damage;
        Projectile p2 = new RocketProjectile(engine, this);
        p2.damage = damage;
        Projectile p3 = new RocketProjectile(engine, this);
        p3.damage = damage;
        Projectile p4 = new RocketProjectile(engine, this);
        p4.damage = damage;

        p.setCenter(getCenter());
        p1.setVelocity(Vector2.fromAngle(Angle).mul(30));
        p2.setVelocity(Vector2.fromAngle(Angle + 20).mul(30));
        p3.setVelocity(Vector2.fromAngle(Angle - 20).mul(30));
        p4.setVelocity(Vector2.fromAngle(Angle + 40).mul(30));
        p.setVelocity(Vector2.fromAngle(Angle - 40).mul(30));

        TaskScheduler ts = engine.taskScheduler;

        // entityHandler.add(p);
        // entityHandler.add(p);

        ts.scheduleTask(.3, () -> {
            p1.setCenter(getCenter());
            p1.destroyAfter(0.35);
            entityHandler.add(p1);
        }).sleep(.3, () -> {
            p2.setCenter(getCenter());
            p2.destroyAfter(0.4);
            entityHandler.add(p2);
        }).sleep(.3, () -> {
            p4.setCenter(getCenter());
            p4.destroyAfter(0.5);
            entityHandler.add(p4);
        }).sleep(.3, () -> {
            p.setCenter(getCenter());
            p.destroyAfter(0.35);
            entityHandler.add(p);
        }).sleep(.3, () -> {
            p3.setCenter(getCenter());
            p3.destroyAfter(0.4);
            entityHandler.add(p3);
        });
    }

    /**
     * Returns the image path for the Square's variant.
     *
     * @return The image path for the Square's variant.
     */
    public String getImagePathFromVariant() {
        return "Images/Entities/SquareBrawler.png";
    }

    /**
     * Checks if the Square can fire a projectile.
     * The Square can fire if enough time has passed since the last firing.
     *
     * @return true if the Square can fire, false otherwise.
     */
    public boolean canFire() {
        if (System.currentTimeMillis() - lastFired > 1000 &&  getAmmo().canUse()) {
            return true;
        }
        return false;
    }
}
