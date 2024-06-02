package Engine.Tile;

import MathLib.AABB;
import MathLib.Vector2;
import java.awt.image.BufferedImage;
import Resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

import Engine.*;
import Engine.Entities.Brawler;
import Engine.Entities.Projectile;
import Engine.Entities.Brawlers.Circle;
import Engine.Entities.Brawlers.Triangle;
import Engine.Entities.Projectiles.CircleProjectile;
import Engine.Entities.Special.PowerBoxEntity;
import Engine.Entities.Special.PowerShard;

import java.util.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Represents a tile in the game world.
 * @author haotian
 */
public class Tile {

    private static HashMap<String, BufferedImage> Image_Cache = new HashMap<String, BufferedImage>();

    public Vector2 Position;
    public final AABB Collisions;
    public BufferedImage Image;
    public final boolean CanCollide;
    public boolean IsTransparent = true;

    public Engine engine;

    /**
     * Constructs a new Tile object with the specified position.
     * 
     * @param Position The position of the tile.
     */
    public Tile(Vector2 Position) {
        this(Position, true);
    }

    /**
     * Constructs a new Tile object with the specified position and collision flag.
     * 
     * @param Position    The position of the tile.
     * @param CanCollide  A flag indicating whether the tile can collide with other objects.
     */
    public Tile(Vector2 Position, boolean CanCollide) {
        this.CanCollide = CanCollide;

        this.Position = Position;
        this.Collisions = getHitBox();
        if (Collisions != null) {
            Collisions.Position = Position;
        }
        IsTransparent = getTransparency();
        setImage();
    }

    /**
     * Sets the engine for the tile.
     * 
     * @param e The engine object.
     */
    public void setEngine(Engine e) {
        engine = e;
    }

    /**
     * Called when the tile is destroyed.
     */
    public void onDestroy() {

    }

    /**
     * Called when the tile is added to the game world.
     */
    public void onAdd() {

    }

    /**
     * Constructs a new Tile object with default values.
     */
    public Tile() {
        this(new Vector2(), true);
    }

    /**
     * Constructs a new Tile object with the specified collision flag and default position.
     * 
     * @param CanCollide  A flag indicating whether the tile can collide with other objects.
     */
    public Tile(boolean CanCollide) {
        this(new Vector2(), CanCollide);
    }

    /**
     * Sets the position of the tile.
     * 
     * @param v The new position.
     */
    public void setPosition(Vector2 v) {
        Position = v;
        if (Collisions != null) {
            Collisions.Position = Position;
        }
    }

    /**
     * Sets the image of the tile.
     * 
     * @param img The image to set.
     */
    public void setImage(BufferedImage img) {
        Image = img;
    }

    /**
     * Loads and sets the image of the tile.
     */
    public void setImage() {
        String img = getImage();
        if (Image_Cache.containsKey(img)) {
            Image = Image_Cache.get(img);
            return;
        }
        InputStream stream = ResourceManager.getResource(img);

        try {
            Image = javax.imageio.ImageIO.read(stream);
            BufferedImage newImage = new BufferedImage(Image.getWidth(),
                    Image.getHeight(), IsTransparent ? BufferedImage.TYPE_INT_ARGB
                            : BufferedImage.TYPE_INT_RGB);
            newImage.getGraphics().drawImage(Image, 0, 0, null);
            Image = newImage;
            Image = ResourceManager.scaleImage(Image, TileHandler.Grid_Scale,
                    TileHandler.Grid_Scale);
            Image_Cache.put(img, Image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the transparency flag of the tile.
     * 
     * @return true if the tile is transparent, false otherwise.
     */
    public boolean getTransparency() {
        return false;
    }

    /**
     * Gets the image path of the tile.
     * 
     * @return The image path.
     */
    public String getImage() {
        return "Images/TopBar/Close.png";
    }

    /**
     * Gets the hitbox of the tile.
     * 
     * @return The hitbox.
     */
    public AABB getHitBox() {
        return new AABB(Vector2.ZERO, Vector2.ONE);
    }

    /**
     * Returns the fully qualified name of the Tile class.
     * 
     * @return The class name.
     */
    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
