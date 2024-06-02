package Engine.Entities;

import Engine.*;
import Engine.Entities.Brawlers.*;
import Engine.Entities.Projectiles.CircleProjectile;
import Engine.Entities.Projectiles.KiteProjectile;
import Engine.Entities.Projectiles.LeonProjectile;
import Engine.Entities.Projectiles.RocketProjectile;
import Engine.Entities.Special.PowerBoxEntity;
import Engine.Entities.Special.PowerShard;
import Engine.EntityComponents.*;
import Engine.EntityComponents.Base.*;
import Engine.Session.SessionPlayer;

import java.security.PublicKey;

import Engine.Tile.Tile;
import MathLib.AABB;
import MathLib.Vector2;
import Resources.ResourceManager;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import java.awt.*;
import java.awt.geom.AffineTransform;

import EasingLib.Circ;



import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an entity in the game.
 * @author haotian
 */
public class Entity {

    /**
     * Flag to determine whether to show hitboxes or not.
     */
    public final boolean SHOW_HITBOXES = Engine.DEBUGGING;

    /**
     * The grid scale for the entity.
     */
    public static final int Grid_Scale = TileHandler.Grid_Scale;

    private static HashMap<Byte, Class<? extends Entity>> entityMap = new HashMap<Byte, Class<? extends Entity>>();
    private static int id = 0;

    /**
     * Registers an entity class.
     * 
     * @param c The entity class to register.
     */
    protected static void registerEntityClass(Class<? extends Entity> c) {
        int val = id - 128;
        id++;
        entityMap.put((byte) val, c);
    }

    /**
     * Gets the class index of an entity class.
     * 
     * @param c The entity class.
     * @return The class index.
     * @throws IllegalArgumentException if the entity class is not registered.
     */
    public static byte getClassIndex(Class<? extends Entity> c) {
        for (Map.Entry<Byte, Class<? extends Entity>> entry : entityMap.entrySet()) {
            if (entry.getValue().equals(c)) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException(c.getName() + " is not a registered Entity Class");
    }

    //This registers a class by giving them a ID for replication by the order they are added
    static {
        registerEntityClass(Entity.class);
        registerEntityClass(Brawler.class);
        registerEntityClass(Projectile.class);

        registerEntityClass(Triangle.class);
        registerEntityClass(Circle.class);

        registerEntityClass(PowerBoxEntity.class);

        registerEntityClass(PowerShard.class);

        registerEntityClass(CircleProjectile.class);
        registerEntityClass(RocketProjectile.class);
        registerEntityClass(Square.class);
        registerEntityClass(Pentagon.class);
        registerEntityClass(LeonProjectile.class);
        registerEntityClass(Kite.class);
        registerEntityClass(KiteProjectile.class);
    }

    public AABB HitBox;
    public int Angle = 0;
    public double Scale = 1;

    public short EntityID;
    public UUID ID;
    public final Engine engine;
    public final EntityHandler entityHandler;

    public boolean isVisible = true;

    public String Name = "Entity";

    public boolean Destroyed = false;

    private BufferedImage Image;

    public String Variant = "Default";

    private ComponentContainer Components;

    public float Speed = 0.0f;

    public SessionPlayer owner = null;

    public boolean isLocal = false;
    public boolean isSpecial = false;

    /**
     * Constructs a new Entity object.
     * 
     * @param engine The game engine.
     */
    @SuppressWarnings("unchecked")
    public Entity(Engine engine) {
        this.engine = engine;
        this.entityHandler = engine.entityHandler;
        this.HitBox = new AABB(new Vector2(3, 3), new Vector2(.95f, .95f));
        this.ID = UUID.randomUUID();

        Components = new ComponentContainer();
        setVariant(Variant);
    }

    /**
     * Creates an Entity object from component data.
     * 
     * @param engine The game engine.
     * @param data   The component data.
     * @return The created Entity object.
     */
    public static Entity fromComponentData(Engine engine, ComponentData data) {
        Class<?> c = entityMap.get(data.entityType);

        Entity entity;
        try {
            entity = (Entity) c.getConstructor(Engine.class).newInstance(engine);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        entity.EntityID = data.entityId;
        entity.ID = data.entityUUID;

        engine.entityHandler.parseDataFor(entity, data.components);

        return entity;
    }

    /**
     * Gets the class ID of the entity.
     * 
     * @return The class ID.
     */
    public byte getClassID() {
        return getClassIndex(this.getClass());
    }

    /**
     * Prints the components of the entity.
     */
    public void printComponents() {
        System.out.println(Components);
    }

    /**
     * Removes a component from the entity.
     * 
     * @param c The component class to remove.
     */
    public void removeComponent(Class<? extends AbstractComponent> c) {
        Components.remove(c);
    }

    /**
     * Adds a component to the entity.
     * 
     * @param c The component to add.
     */
    public void addComponent(AbstractComponent c) {
        Components.add(c);
    }

    /**
     * Adds a component to the entity at the specified index.
     * 
     * @param c     The component to add.
     * @param index The index to add the component at.
     */
    public void addComponent(AbstractComponent c, int index) {
        Components.add(c, index);
    }

    /**
     * Gets a component of the entity.
     * 
     * @param <T> The type of the component.
     * @param c   The component class.
     * @return The component instance, or null if not found.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractComponent> T getComponent(Class<? extends AbstractComponent> c) {
        AbstractComponent component = Components.get(c);
        if (component == null)
            return null;
        return (T) component;
    }

    /**
     * Checks if the entity has a component.
     * 
     * @param c The component class.
     * @return True if the entity has the component, false otherwise.
     */
    public boolean hasComponent(Class<? extends AbstractComponent> c) {
        return Components.get(c) != null;
    }

    /**
     * Sets the owner of the entity.
     * 
     * @param p The owner player.
     */
    public void setOwner(SessionPlayer p) {
        owner = p;
        if ((!engine.isHost && p.isLocal())) {
            isLocal = true;
        } else if (engine.isHost && !p.isBot && !p.isLocal()) {
            isSpecial = true;
        }
    }

    /**
     * Updates the image of the entity.
     * 
     * @param path The image path.
     */
    private void updateImage(String path) {
        BufferedImage image = ResourceManager.getImage(path);
        if (image == null)
            return;
        image = ResourceManager.scaleImage(image, (int) (Grid_Scale * Scale), (int) (Grid_Scale * Scale));
        Image = image;
    }

    /**
     * Gets the image path from the variant.
     * 
     * @return The image path.
     */
    public String getImagePathFromVariant() {
        return "Images/Tiles/Transparent.png";
    }

    /**
     * Sets the variant of the entity.
     * 
     * @param v The variant.
     */
    public void setVariant(String v) {
        if (v.equals(Variant))
            return;
        Variant = v;
        updateImage(getImagePathFromVariant());
        entityHandler.setComponent(this, 15, v);
    }

    /**
     * Gets the image of the entity.
     * 
     * @return The image.
     */
    public BufferedImage getImage() {
        return Image;
    }

    /**
     * Sets the scale of the entity.
     * 
     * @param s The scale.
     */
    public void setScale(double s) {
        Scale = s;
        updateImage(getImagePathFromVariant());
        entityHandler.setComponent(this, 13, s);
    }

    /**
     * Gets the speed of the entity.
     * 
     * @return The speed.
     */
    public float getSpeed() {
        return Speed;
    }

    /**
     * Sets the speed of the entity.
     * 
     * @param speed The speed.
     */
    public void setSpeed(float speed) {
        Speed = speed;
        entityHandler.setComponent(this, 12, speed);
    }

    /**
     * Calculates the hash code of the entity.
     * 
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    /**
     * Checks if the entity is equal to another object.
     * 
     * @param obj The object to compare.
     * @return True if the entity is equal to the object, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        return this.ID.equals(other.ID);
    }

    /**
     * Resizes the entity.
     * 
     * @param v The new size.
     */
    public void resize(Vector2 v) {
        if (v.equals(HitBox.Size))
            return;
        HitBox.resize(v);
        entityHandler.setComponent(this, 11, HitBox.Size);
    }

    /**
     * Translates the entity.
     * 
     * @param v The translation vector.
     */
    public void translate(Vector2 v) {
        if (v.equals(Vector2.ZERO))
            return;
        HitBox.translate(v);
        entityHandler.setComponent(this, 0, HitBox.Position);
    }

    /**
     * Sets the velocity of the entity.
     * 
     * @param v The velocity vector.
     */
    public void setVelocity(Vector2 v) {
        if (HitBox.Velocity.equals(v))
            return;
        HitBox.Velocity = v;
        entityHandler.setComponent(this, 2, v);
    }

    /**
     * Sets the center of the entity.
     * 
     * @param v The center vector.
     */
    public void setCenter(Vector2 v) {
        if (HitBox.getCenter().equals(v))
            return;
        HitBox.setCenter(v);
        entityHandler.setComponent(this, 0, HitBox.Position);
    }

    /**
     * Sets the position of the entity.
     * 
     * @param v The position vector.
     */
    public void setPosition(Vector2 v) {
        if (HitBox.Position.equals(v))
            return;
        HitBox.Position = v;
        entityHandler.setComponent(this, 0, v);
    }

    /**
     * Sets the angle of the entity.
     * 
     * @param a The angle.
     */
    public void setAngle(int a) {
        if (Angle == a)
            return;
        Angle = a;
        entityHandler.setComponent(this, 1, a);
    }

    /**
     * Gets the center of the entity.
     * 
     * @return The center vector.
     */
    public Vector2 getCenter() {
        return HitBox.getCenter();
    }

    /**
     * Gets the position of the entity.
     * 
     * @return The position vector.
     */
    public Vector2 getPosition() {
        return HitBox.Position;
    }

    /**
     * Gets the velocity of the entity.
     * 
     * @return The velocity vector.
     */
    public Vector2 getVelocity() {
        return HitBox.Velocity;
    }

    /**
     * Checks if the entity collides with another object.
     * 
     * @param o The object to check collision with.
     * @return True if the entity collides with the object, false otherwise.
     */
    public boolean onCollision(Object o) {
        if (o instanceof Tile) {
            Tile t = (Tile) o;
            return t.CanCollide;
        }
        return false;
    }

    /**
     * Sets the visibility of the entity.
     * 
     * @param v The visibility flag.
     */
    public void setVisible(boolean v) {
        isVisible = v;
        entityHandler.setComponent(this, 17, v);
    }

    /**
     * Checks if the entity should be visible.
     * 
     * @return True if the entity should be visible, false otherwise.
     */
    public boolean shouldBeVisible() {
        return isVisible;
    }

    /**
     * Updates the entity.
     * 
     * @param dt The time step.
     */
    public void update(double dt) {
        for (AbstractComponent c : Components) {
            if (c.updatable == false) {
                continue;
            }
            if (Destroyed)
                break;
            c.update(dt);
        }
    }

    /**
     * Gets the display coordinates of the entity.
     * 
     * @param center The center vector.
     * @return The display coordinates.
     */
    public Vector2 getDisplayCoords(Vector2 center) {
        return HitBox.getCenter().sub(new Vector2(((float) Scale - 1) * .5f, ((float) Scale - 1) * .5f))
                .mul(Grid_Scale).relativeTo(engine.scaledCameraLoc).add(center);
    }

    /**
     * Gets the display size of the entity.
     * 
     * @return The display size.
     */
    public int getDisplaySize() {
        return (int) (Scale * Grid_Scale);
    }

    static final AlphaComposite transparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
    static final AlphaComposite notTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);

    /**
     * Draws the entity.
     * 
     * @param g      The graphics object.
     * @param center The center vector.
     */
    public void draw(Graphics2D g, Vector2 center) {
        Vector2 display = getDisplayCoords(center);

        AffineTransform originalTransform = g.getTransform();

        boolean shouldBeVisible = shouldBeVisible();
        if (shouldBeVisible || engine.camera.getEntity() == this) {
            if (!shouldBeVisible) {
                g.setComposite(transparent);
            }
            for (AbstractComponent c : Components) {
                if (c.drawable == false) {
                    continue;
                }
                if (Destroyed)
                    break;

                c.draw(g, center, display);
            }
            if (!shouldBeVisible) {
                g.setComposite(notTransparent);
            }
        }

        if (SHOW_HITBOXES) {
            Vector2 hitbox = HitBox.Size.mul(Engine.GRID_SCALE);
            g.setTransform(originalTransform);
            g.setColor(Color.BLACK);
            display = HitBox.getCenter().sub(new Vector2(((float) HitBox.Size.X - 1) * .5f,
                    ((float) HitBox.Size.Y - 1) * .5f)).mul(Grid_Scale).relativeTo(engine.scaledCameraLoc)
                    .add(center);
            g.drawRect((int) (display.X), (int) (display.Y), (int) hitbox.X, (int) hitbox.Y);
        }
        // g.fillRect((int)display.X, (int)display.Y, Size, Size);

    }

    /**
     * Destroys the entity.
     */
    public void destroy() {
        if (Destroyed)
            return;
        Destroyed = true;
        engine.entityHandler.remove(this);
    }

    /**
     * Destroys the entity after a specified time.
     * 
     * @param t The time delay.
     */
    public void destroyAfter(double t) {
        engine.taskScheduler.scheduleTask(t, () -> destroy());
    }

    /**
     * Gets the replication data of the entity.
     * 
     * @return The replication data.
     */
    public ComponentData getReplicationData() {
        ComponentData data = new ComponentData(this);
        data.setComponent(0, HitBox.Position);
        data.setComponent(1, Angle);
        data.setComponent(2, HitBox.Velocity);
        data.setComponent(11, HitBox.Size);
        data.setComponent(12, Speed);
        data.setComponent(13, Scale);
        data.setComponent(15, Variant);
        data.setComponent(16, Name);
        data.setComponent(17, isVisible);
        

        for (AbstractComponent c : Components) {
            if (! (c instanceof SerializableComponent)) {
                continue;
            }
            SerializableComponent sc = (SerializableComponent) c;
            sc.replicateEntity(data); 
        }
        return data;
    }


    static {

        EntityHandler.registerMethod(12, (entity,obj) -> { //Speed
            float value = (float) obj;
            entity.Speed = value;
        });

        EntityHandler.registerMethod(0, (entity,vector) -> { //Position
            Vector2 v = (Vector2) vector;
           // System.out.println("Position: " + v);
            //entity.HitBox.Position = v;
            entity.setPosition(v);
        });
    
        EntityHandler.registerMethod(11, (entity,vector) -> { //Size
            Vector2 v = (Vector2) vector;
            entity.HitBox.Size = v;
        });
    
        EntityHandler.registerMethod(2, (entity,obj) -> { //Velocity
            Vector2 v = (Vector2) obj;
           // entity.HitBox.Velocity = v;
            entity.setVelocity(v);
        });
    
        EntityHandler.registerMethod(13, (entity,obj) -> { //Scale
            double value = (double) obj;
            entity.Scale = value;
            entity.updateImage(entity.getImagePathFromVariant());
        });
    
        EntityHandler.registerMethod(1, (entity,obj) -> { //Angle
            int value = (int) obj;
            //entity.Angle = value;
            entity.setAngle(value);
        });
    
        EntityHandler.registerMethod(15, (entity,obj) -> { //Variant
            String value = (String) obj;
            entity.Variant = value;
            entity.updateImage(entity.getImagePathFromVariant());
        });

        EntityHandler.registerMethod(16, (entity,obj) -> { //Name
            String value = (String) obj;
            entity.Name = value;
        });

        EntityHandler.registerMethod(17, (entity,obj) -> { //Visible
            boolean value = (boolean) obj;
            entity.setVisible(value);
        });
    }

}
