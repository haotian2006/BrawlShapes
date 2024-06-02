package Engine.Entities;

import Engine.*;
import Engine.Controllers.Controller;
import Engine.Entities.Special.PowerShard;
import Engine.EntityComponents.*;
import Engine.EntityComponents.Base.*;
import Engine.EntityComponents.Components.*;
import Engine.Tile.GrassTile;
import MathLib.AABB;
import MathLib.Vector2;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.*;

/**
 * The abstract class representing a Brawler entity in the game.
 * It extends the Entity class and provides common functionality and attributes for all Brawlers.
 */
public abstract class Brawler extends Entity {

    private Controller controller;

    /**
     * Constructs a new Brawler object.
     * @param engine The game engine.
     */
    public Brawler(Engine engine) {
        super(engine);
        this.HitBox = new AABB(new Vector2(2f, 2f), new Vector2(.8f, .8f));
        setScale(1.8f);
        setSpeed(4);
        
        addComponent(new Ammo(this, 100,15,20));
        addComponent(new SuperValue(this, 100));
        addComponent(new RenderName(this));
        addComponent(new RegenHealth(this, 100));
        addComponent(new Health(this, 100f));
        addComponent(new PowerPoint(this));
        addComponent(new UpdatePosition(this));
        addComponent(new ApplyRotation(this));
        addComponent(new RenderImage(this));
    }

    /**
     * Sets the controller for the Brawler.
     * @param c The controller to set.
     */
    public void setController(Controller c) {
        controller = c;
    }

    /**
     * Called when the Brawler inflicts damage to another entity.
     * @param damage The amount of damage inflicted.
     */
    public void didDamage(float damage) {
        incrementSuper(damage);
    }

    /**
     * Abstract method to fire a projectile or perform an attack.
     */
    public abstract void fire();

    /**
     * Abstract method to check if the Brawler can fire.
     * @return true if the Brawler can fire, false otherwise.
     */
    public abstract boolean canFire();

    /**
     * Checks if the Brawler's super ability is enabled.
     * @return true if the super ability is enabled, false otherwise.
     */
    public boolean isSuperEnabled() {
        SuperValue sv = getComponent(SuperValue.class);
        return sv.enabled;
    }

    /**
     * Ends the Brawler's super ability.
     */
    public void endSuper() {
        SuperValue sv = getComponent(SuperValue.class);
        sv.setSuperBool(false);
        sv.setSuper(0);
    }

    /**
     * Activates the Brawler's super ability.
     * If the super ability cannot be used, nothing happens.
     */
    public void activateSuper() {
        if (!canUseSuper())
            return;
        SuperValue sv = getComponent(SuperValue.class);
        sv.setSuperBool(true);
    }

    /**
     * Increments the Brawler's super ability value.
     * @param value The value to increment the super ability by.
     */
    public void incrementSuper(float value) {
        SuperValue sv = getComponent(SuperValue.class);
        sv.incrementSuper(value);
    }

    /**
     * Checks if the Brawler can use its super ability.
     * @return true if the Brawler can use its super ability, false otherwise.
     */
    public boolean canUseSuper() {
        SuperValue sv = getComponent(SuperValue.class);
        return sv.superAmt >= sv.totalNeeded && !sv.enabled && !Destroyed;
    }

    /**
     * Gets the Brawler's Ammo component.
     * @return The Ammo component.
     */
    public Ammo getAmmo() {
        return getComponent(Ammo.class);
    }

    /**
     * Checks if the Brawler is currently in a grass tile.
     * works by checking the corners of the Brawler's hitbox.
     * @return true if the Brawler is in a grass tile, false otherwise.
     */
    public boolean inGrass() {
        Vector2[] corners = new Vector2[4];
        Vector2 pos = getPosition();
        float x = pos.X;
        float y = pos.Y;

        float w = HitBox.Size.X;
        float h = HitBox.Size.Y;

        corners[0] = new Vector2(x, y);
        corners[1] = new Vector2(x + w, y);
        corners[2] = new Vector2(x + w, y + h);
        corners[3] = new Vector2(x, y + h);

        for (int i = 0; i < corners.length; i++) {
            Vector2 coord = corners[i].floor();

            if (!(engine.tileHandler.getTile((int) coord.X, (int) coord.Y) instanceof GrassTile))
                return false;
        }
        return true;
    }

    /**
     * Checks if the Brawler should be visible.
     * The Brawler is not visible if it is in a grass tile and another Brawler is nearby.
     * @return true if the Brawler should be visible, false otherwise.
     */
    public boolean shouldBeVisible() {
        Brawler b = (Brawler) engine.camera.getEntity();
        boolean grass = inGrass();
        if (grass) {
            if (b != this && b != null) {
                float range = b.inGrass() ? 2f : .8f;
                if (getCenter().distanceTo(b.getCenter()) < range) {
                    grass = false;
                }
            }
        }
        return isVisible && !grass;
    }

    /**
     * Inflicts damage to the Brawler.
     * @param damage The amount of damage to inflict.
     * @return The actual amount of damage taken.
     */
    public float takeDamage(float damage) {
        float h = getHealth();
        setHealth(getHealth() - damage);
        return h - getHealth();
    }

    /**
     * Sets the health of the Brawler.
     * If the health is less than 0, the Brawler is destroyed.
     * @param health The health value to set.
     */
    public void setHealth(float health) {
        Health h = getComponent(Health.class);
        if (health < 0) {
            this.destroy();
        }
        h.setHealth(health);
    }

    /**
     * Gets the current health of the Brawler.
     * @return The current health value.
     */
    public float getHealth() {
        Health h = getComponent(Health.class);
        return h.Health;
    }

    /**
     * Gets the number of power points the Brawler has.
     * @return The number of power points.
     */
    public int getPowerPoints() {
        PowerPoint h = getComponent(PowerPoint.class);
        return h.points;
    }

    public void powerPointsUpdated(int amt) {
        Health h = getComponent(Health.class);
    }

    /**
     * Updates the state of the Brawler entity.
     * 
     * @param dt The time elapsed since the last update.
     */
    @Override
    public void update(double dt) {
        if (controller != null)
            controller.update(dt);
        super.update(dt);
    }

    /**
     * Destroys the entity.
     */
    public void destroy() {
        if(Destroyed)
            return;
       if (engine.isHost){
        for ( int i = 0; i < getPowerPoints(); i++){
            PowerShard p = new PowerShard(engine);
            p.setCenter(getPosition().floor().add(new Vector2((float) Math.random() * .8f , (float) Math.random() * .8f)));
            engine.entityHandler.add(p);
        }
       }
       super.destroy();
    }


}
