package Engine.Entities.Special;

import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import Engine.Entities.SpecialEntity;
import Engine.EntityComponents.Base.ApplyRotation;
import Engine.EntityComponents.Base.RenderImage;
import Engine.EntityComponents.Base.RenderName;
import Engine.EntityComponents.Base.UpdatePosition;
import Engine.EntityComponents.Components.Health;
import Engine.EntityComponents.Components.SuperValue;
import MathLib.Vector2;
import Engine.Tile.*;

import java.util.*;

import Engine.Engine;
/**
 * Represents a Power Box entity in the game.
 * Power Box entities are special entities that can be damaged and destroyed.
 * When destroyed, they drop a Power Shard and remove the corresponding tile from the game.
 * @author haotian
 */
public class PowerBoxEntity extends Entity implements SpecialEntity {

    private PowerBoxTile tile;

    /**
     * Constructs a PowerBoxEntity object with the specified engine and tile.
     * 
     * @param engine the game engine
     * @param tile   the PowerBoxTile associated with this entity
     */
    public PowerBoxEntity(Engine engine, PowerBoxTile tile) {
        this(engine);
        this.tile = tile;
        HitBox.Position = tile.Position;
    }

    /**
     * Constructs a PowerBoxEntity object with the specified engine.
     * 
     * @param engine the game engine
     */
    public PowerBoxEntity(Engine engine) {
        super(engine);
        setScale(1.5f);
        addComponent(new Health(this, 1000f));
    }

    /**
     * Takes damage and reduces the entity's health.
     * If the health reaches 0 or below, the entity is destroyed and a Power Shard is dropped.
     * 
     * @param damage the amount of damage to be taken
     * @return the actual amount of damage taken
     */
    public float takeDamage(float damage) {
        float h = getHealth();
        setHealth(getHealth() - damage);
        if (getHealth() <= 0) {
            PowerShard s = new PowerShard(engine);
            s.HitBox.Position = HitBox.Position;
            engine.entityHandler.add(s);
            engine.tileHandler.removeTile((int) tile.Position.X, (int) tile.Position.Y);
            destroy();
        }
        return h - getHealth();
    }

    /**
     * Sets the health of the entity.
     * 
     * @param health the new health value
     */
    public void setHealth(float health) {
        Health h = getComponent(Health.class);
        h.setHealth(health);
    }

    /**
     * Returns the current health of the entity.
     * 
     * @return the current health value
     */
    public float getHealth() {
        Health h = getComponent(Health.class);
        return h.Health;
    }

    /**
     * Returns the position of the entity.
     * The position is determined by the adjacent tiles.
     * 
     * @return the position of the entity as a Vector2 object
     */
    public Vector2 getPosition() {
        int x = (int) tile.Position.X;
        int y = (int) tile.Position.Y;
        Tile up = engine.tileHandler.getTile(x, y + 1);
        Tile right = engine.tileHandler.getTile(x + 1, y);
        Tile down = engine.tileHandler.getTile(x, y - 1);
        Tile left = engine.tileHandler.getTile(x - 1, y);

        if (up == null || !up.CanCollide) {
            return new Vector2(x, y + 1);
        } else if (right == null || !right.CanCollide) {
            return new Vector2(x + 1, y);
        } else if (down == null || !down.CanCollide) {
            return new Vector2(x, y - 1);
        } else if (left == null || !left.CanCollide) {
            return new Vector2(x - 1, y);
        }
        return new Vector2(-1000000, -1000000);
    }
}
