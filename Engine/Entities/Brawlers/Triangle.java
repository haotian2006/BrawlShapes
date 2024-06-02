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
 * The Triangle class represents a specific type of Brawler called Triangle.
 * It extends the Brawler class and implements its own unique behavior and attributes.
 * Triangle is a medium range brawler that fires bursts of projectiles at enemies.
 * @see Brawler
 * @author joey
 */
public class Triangle extends Brawler {
     
    private long lastFired;
    private int damage = 100 ;
    /**
     * Constructs a new Triangle object with the specified Engine.
     * Initializes the Triangle's health component with a value of 800.
     * 
     * @param engine the Engine object associated with the Triangle
     */
    public Triangle(Engine engine) {
        super(engine);
        addComponent(new Health(this, 800));
        addComponent(new SuperValue(this,damage*7));
        addComponent(new Ammo(this,100,12,30));
    }

    public void powerPointsUpdated(int amt) {
        Health h = getComponent(Health.class);
        h.setMaxHealth(800 + amt*60);
    }

    /**
     * Uses the Triangle's super ability.
     * Calculates the damage based on the power points and schedules the creation of multiple projectiles.
     */
    public void useSuper() {
       int damage = 160 + getPowerPoints() * 5;
        TaskScheduler ts = engine.taskScheduler;

        Vector2 Velocity = Vector2.fromAngle(Angle).mul(20);
   
        Consumer<Integer> addProjectile = intager -> {
            CircleProjectile proj = new CircleProjectile(engine, this);
            ((ColorComponent) proj.getComponent(ColorComponent.class)).setColor(Color.blue);
            proj.damage = damage;
            proj.setScale(.6);
            proj.resize(new Vector2(.6f, .6f));
            proj.setCenter(getCenter());
            proj.setVelocity(Velocity);
            entityHandler.add(proj);
            proj.destroyAfter(.4);
        };

        ts.scheduleForTask(.1, 8, addProjectile);
    }

    /**
     * Fires a projectile from the Triangle.
     * If the Triangle's super ability is enabled, it uses the super ability and ends it.
     * Otherwise, it creates multiple projectiles and schedules their creation.
     */
    public void fire() {
        if (!canFire() || Destroyed) {
            return;
        }
        lastFired = System.currentTimeMillis();
    
        int damage = 70 + getPowerPoints() * 7;
        if (isSuperEnabled()) {
            useSuper();
            endSuper();
            return;
        }
        getAmmo().use();

        CircleProjectile p = new CircleProjectile(engine, this);
        p.damage = damage;
        CircleProjectile p1 = new CircleProjectile(engine, this);
        CircleProjectile p2 = new CircleProjectile(engine, this);
        CircleProjectile p3 = new CircleProjectile(engine, this);
        
        p.setCenter(getCenter());

        Vector2 Velocity = Vector2.fromAngle(Angle).mul(20);
   
        TaskScheduler ts = engine.taskScheduler;

        Consumer<CircleProjectile> addProjectile = proj -> {
            proj.setColor(Color.red);
            proj.damage=damage + getPowerPoints() * 15;
            proj.setScale(.5);
            proj.resize(new Vector2(.5f, .5f));
            proj.setCenter(getCenter());
            proj.setVelocity(Velocity);
            entityHandler.add(proj);
            proj.destroyAfter(.4);
        };

        addProjectile.accept(p);

        ts.scheduleForEachTask(.15, new CircleProjectile[]{p1, p2, p3, p}, addProjectile);
    }

    /**
     * Returns the image path for the Triangle's variant.
     * 
     * @return the image path for the Triangle's variant
     */
    public String getImagePathFromVariant() {
        return "Images/Entities/Triangle.png";
    }

    /**
     * Checks if the Triangle is able to fire a projectile.
     * 
     * @return true if the Triangle can fire, false otherwise
     */
    public boolean canFire() {
        if (System.currentTimeMillis() - lastFired > 800 && getAmmo().canUse()) {
            return true;
        }
        return false;
    }
}

