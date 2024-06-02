package Engine.Entities.Brawlers;


import Engine.Entities.*;
import Engine.Entities.Projectiles.CircleProjectile;
import Engine.EntityComponents.*;
import Engine.EntityComponents.Components.Ammo;
import Engine.EntityComponents.Components.ColorComponent;
import Engine.EntityComponents.Components.Health;
import Engine.EntityComponents.Components.SuperValue;
import MathLib.Vector2;

import java.awt.Color;
import java.util.*;
import java.util.function.Consumer;

import Engine.*;

/**
 * Represents a Circle brawler entity in the game.
 * Extends the Brawler class.
 * @see Brawler
 * @author joey
 */
/**
 * The Circle class represents a specific type of Brawler called Circle.
 * Circle is a subclass of Brawler and has additional properties and behaviors.
 * Circle is a short range brawler that fires like a shot gun
 */
public class Circle extends Brawler {

    /*
     * The time when the brawler last fired a projectile.
     */
    private long lastFired;
    private int damage=  200;
    
    /**
     * Constructs a new Circle object with the given Engine.
     * 
     * @param engine the Engine object associated with the Circle
     */
    public Circle(Engine engine) {
        super(engine);
        addComponent(new Health(this, 850));
         addComponent(new SuperValue(this,damage*6));
         addComponent(new Ammo(this,100,10,25));
         this.setSpeed(5.1f);
    }

    public void powerPointsUpdated(int amt) {
        Health h = getComponent(Health.class);
        h.setMaxHealth(850 + amt*56);
    }

    /**
     * Fires a projectile from the Circle.
     * The Circle can only fire a projectile if it is not destroyed and enough time has passed since the last firing.
     * The damage of the projectile is determined by the power points of the Circle.
     * If the Circle's super ability is enabled, it will use the super ability and fire multiple projectiles.
     */
    public void fire() {
        if (!canFire() || Destroyed) {
            return;
        }
        lastFired = System.currentTimeMillis();
      

        if (isSuperEnabled()) {
            useSuper();
            endSuper();
            return;
        }
        getAmmo().use();
        Projectile p = new Projectile(engine, this);
        Projectile p1 = new Projectile(engine, this);
        Projectile p2 = new Projectile(engine, this);
        p.setCenter(getCenter());
        p1.setCenter(getCenter());
        p2.setCenter(getCenter());
        p.setVelocity(Vector2.fromAngle(Angle).mul(10));
        p1.setVelocity(Vector2.fromAngle(Angle - 10).mul(10));
        p2.setVelocity(Vector2.fromAngle(Angle + 10).mul(10));

        TaskScheduler ts = engine.taskScheduler;

        Consumer<Projectile> addProjectile = proj -> {
            proj.damage = damage + getPowerPoints() * 5;
            proj.setCenter(getCenter());
            entityHandler.add(proj);
            proj.destroyAfter(.6);
        };

        addProjectile.accept(p);
        ts.scheduleForEachTask(0, new Projectile[] { p1, p2 }, addProjectile);
    }

    /**
     * Uses the super ability of the Circle.
     * The Circle's super ability fires multiple projectiles in different directions.
     * Each projectile has a fixed damage value.
     */
    public void useSuper() {
        int damage1 = 250+getPowerPoints()*6;
        Projectile p = new CircleProjectile(engine, this);
        p.damage = damage1;
        p.setCenter(getCenter());
        Projectile p1 = new CircleProjectile(engine, this);

        p1.damage = damage1;
        p1.setCenter(getCenter());
        Projectile p2 = new CircleProjectile(engine, this);
        p2.setCenter(getCenter());
        p2.damage = damage1;
        Projectile p3 = new CircleProjectile(engine, this);
        p3.damage = damage1;
        p3.setCenter(getCenter());
        Projectile p4 = new CircleProjectile(engine, this);
        p4.damage = damage1;
        p4.setCenter(getCenter());
        Projectile p5 = new CircleProjectile(engine, this);
        p5.damage = damage1;
        p5.setCenter(getCenter());
        Projectile p6 = new CircleProjectile(engine, this);
        p6.damage = damage1;
        p6.setCenter(getCenter());

        p1.setVelocity(Vector2.fromAngle(Angle).mul(15));
        p2.setVelocity(Vector2.fromAngle(Angle + 10).mul(15));
        p3.setVelocity(Vector2.fromAngle(Angle - 10).mul(15));
        p4.setVelocity(Vector2.fromAngle(Angle + 20).mul(15));
        p.setVelocity(Vector2.fromAngle(Angle - 20).mul(15));
        p5.setVelocity(Vector2.fromAngle(Angle + 30).mul(15));

        TaskScheduler ts = engine.taskScheduler;

        Consumer<Projectile> addProjectile = proj -> {
            ((CircleProjectile) proj).setColor(Color.CYAN);
            proj.setCenter(getCenter());
            proj.setScale(0.7);
            entityHandler.add(proj);
            proj.destroyAfter(.8f);
        };

        addProjectile.accept(p);
        ts.scheduleForEachTask(0, new Projectile[] { p1, p2, p3, p4, p5 }, addProjectile);
    }

    /**
     * Returns the image path for the Circle's variant.
     * 
     * @return the image path for the Circle's variant
     */
    public String getImagePathFromVariant() {
        return "Images/Entities/Circle.png";
    }

    /**
     * Checks if the Circle can fire a projectile.
     * The Circle can fire a projectile if enough time has passed since the last firing.
     * 
     * @return true if the Circle can fire a projectile, false otherwise
     */
    public boolean canFire() {
        if (System.currentTimeMillis() - lastFired > 1000 &&  getAmmo().canUse()) {
           return true;
        }
        return false;
    }
}
