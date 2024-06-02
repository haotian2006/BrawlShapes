package Engine.Entities.Brawlers;


import Engine.Entities.*;
import Engine.Entities.Projectiles.*;
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
 * The Pentagon class represents a specific type of Brawler called Pentagon.
 * It extends the Brawler class and contains additional methods and properties
 * specific to the Pentagon Brawler.
 * Pentagon is medium range brawler that can go invisible for a short duration 
 * @see Brawler
 * @author joey
 */
public class Pentagon extends Brawler {
     
    private long lastFired;
    private   int damage = 120;
    
    /**
     * Constructs a new Pentagon object with the given Engine.
     * 
     * @param engine The Engine object associated with the Pentagon.
     */
    public Pentagon(Engine engine) {
        super(engine); 
        addComponent(new Health(this, 700));
        addComponent(new SuperValue(this, damage*9));
           addComponent(new Ammo(this,100,13,33));
    }

    public void powerPointsUpdated(int amt) {
        Health h = getComponent(Health.class);
        h.setMaxHealth(700 + amt*70);
    }


    /**
     * Activates the super ability of the Pentagon.
     * This method makes the Pentagon invisible for 4 seconds and then
     * makes it visible again.
     */
    public void activateSuper() {
        System.out.println("spam?");
        if (!canUseSuper())
            return;
        setVisible(false);
        engine.taskScheduler.scheduleTask(4, () -> {
            setVisible(true);
            endSuper();
        });

        super.activateSuper();
    }

    /**
     * Fires projectiles from the Pentagon.
     * This method creates multiple projectiles and sets their properties
     * such as damage, velocity, and scale. The projectiles are then added
     * to the entity handler and scheduled to be destroyed after a certain
     * duration.
     */
    public void fire() {
        if (!canFire() || Destroyed) {
            return;
        }
        getAmmo().use();
        lastFired = System.currentTimeMillis();
   
int damage=100 + getPowerPoints() * 10;
        Projectile p = new LeonProjectile(engine, this);
        p.damage = damage;
        Projectile p1 = new LeonProjectile(engine, this);
        p1.damage = damage;
        Projectile p2 = new LeonProjectile(engine, this);
        p2.damage = damage;
        Projectile p3 = new LeonProjectile(engine, this);
        p3.damage = damage;
        Projectile p4 = new LeonProjectile(engine, this);
        p4.damage = damage;

        p.setCenter(getCenter());
        p1.setVelocity(Vector2.fromAngle(Angle).mul(30));
        p2.setVelocity(Vector2.fromAngle(Angle + 5).mul(30));
        p3.setVelocity(Vector2.fromAngle(Angle + 10).mul(30));
        p4.setVelocity(Vector2.fromAngle(Angle + 15).mul(30));
        p.setVelocity(Vector2.fromAngle(Angle + 20).mul(30));
        p.setScale(.9);
        p1.setScale(.9);
        p2.setScale(.9);
        p3.setScale(.9);
        p4.setScale(.9);

        TaskScheduler ts = engine.taskScheduler;  

        Vector2 Velocity = Vector2.fromAngle(Angle).mul(20);

        Consumer<Projectile> addProjectile = proj -> {
            proj.setCenter(getCenter());
            proj.setAngle(Angle);
            proj.setVelocity(Velocity);
            entityHandler.add(proj);
            proj.destroyAfter(.36);
        };

        addProjectile.accept(p);

        ts.scheduleForEachTask(.15, new Projectile[]{p1, p2, p3, p4}, addProjectile);
    }

    /**
     * Sets the visibility of the Pentagon.
     * This method sets the variant of the Pentagon based on the visibility
     * parameter and then calls the super method to set the visibility.
     * 
     * @param v The visibility of the Pentagon.
     */
    public void setVisible(boolean v) {
        if (!v) {
            setVariant("Transparent");
        } else {
            setVariant("Default");
        }
        super.setVisible(v);
    }

    /**
     * Returns the image path based on the variant of the Pentagon.
     * This method returns the image path of the Pentagon based on its
     * current variant. If the variant is "Transparent", it returns the
     * path for the transparent image, otherwise it returns the path for
     * the default image.
     * 
     * @return The image path of the Pentagon.
     */
    public String getImagePathFromVariant() {
        if (Variant.equals("Transparent")) {
            // return "Images/Entities/TransparentPentagon.png";
        }
        return "Images/Entities/Pentagon.png";
    }

    /**
     * Checks if the Pentagon can fire.
     * This method checks if the time elapsed since the last firing of the
     * Pentagon is greater than 750 milliseconds. If it is, the Pentagon
     * can fire, otherwise it cannot.
     * 
     * @return true if the Pentagon can fire, false otherwise.
     */
    public boolean canFire() {
        if (System.currentTimeMillis() - lastFired > 750 &&  getAmmo().canUse()) {
            return true;
        }
        return false;
    }
}
