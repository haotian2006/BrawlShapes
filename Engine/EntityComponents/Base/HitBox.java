package Engine.EntityComponents.Base;

import Engine.Engine;
import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import MathLib.Vector2;

import java.awt.Graphics2D;


/**
 * The HitBox class represents a hitbox component for an entity.
 * It extends the AbstractComponent class and implements the BaseComponent interface.
 * @author haotian
 */
public class HitBox extends AbstractComponent implements BaseComponent {

    /**
     * Constructs a HitBox object for the specified entity.
     *
     * @param entity the entity associated with the hitbox
     */
    public HitBox(Entity entity) {
        super(entity, false, true);
    }

    /**
     * Draws the hitbox on the specified graphics context.
     *
     * @param g            the graphics context to draw on
     * @param center       the center position of the hitbox
     * @param displayCoords the display coordinates of the hitbox
     */
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords) {
        Vector2 hitboxSize = entity.HitBox.Size.mul(Engine.GRID_SCALE);
        g.drawRect((int) (displayCoords.X + hitboxSize.X / 2), (int) (displayCoords.Y + hitboxSize.Y / 2),
                (int) hitboxSize.X, (int) hitboxSize.Y);
    }
}
