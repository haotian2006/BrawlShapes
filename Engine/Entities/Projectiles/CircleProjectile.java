package Engine.Entities.Projectiles;

import Engine.Entities.*;
import Engine.EntityComponents.Base.Shapes.*;
import Engine.EntityComponents.Base.*;
import Engine.EntityComponents.Components.*;

import java.util.*;

import Engine.*;

import java.awt.*;

import MathLib.*;

/**
 * Represents a circle projectile in the game.
 * Extends the Projectile class.
 * @see Projectile
 * @author joey
 */
public class CircleProjectile extends Projectile {
    
    /**
     * Constructs a CircleProjectile object with the specified engine and parent brawler.
     * @param engine The game engine.
     * @param parent The parent brawler.
     */
    public CircleProjectile(Engine engine, Brawler parent) {
        super(engine, parent);
        removeComponent(RenderImage.class);
        addComponent(new ColorComponent(this));
        addComponent(new DrawCircle(this));
    }

    /**
     * Constructs a CircleProjectile object with the specified engine.
     * @param engine The game engine.
     */
    public CircleProjectile(Engine engine){
        this(engine, null);
    }

    /**
     * Sets the color of the circle projectile.
     * @param color The color to set.
     */
    public void setColor(Color color) {
        ((ColorComponent) getComponent(ColorComponent.class)).setColor(color);
    }

    /**
     * Draws the circle projectile on the screen.
     * @param g The graphics object.
     * @param center The center position of the projectile.
     */
    public void draw(Graphics2D g, Vector2 center) {
        super.draw(g, center);
    }
}
