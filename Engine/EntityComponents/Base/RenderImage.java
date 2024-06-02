package Engine.EntityComponents.Base;

import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import MathLib.Vector2;
import java.awt.Graphics2D;

/**
 * The RenderImage class represents a component that handles rendering an image for an entity.
 * It extends the AbstractComponent class and implements the BaseComponent interface.
 * @author haotian
 */
public class RenderImage extends AbstractComponent implements BaseComponent {

    /**
     * Constructs a new RenderImage object with the specified entity.
     *
     * @param entity the entity associated with this RenderImage
     */
    public RenderImage(Entity entity) {
        super(entity, false, true);
    }

    /**
     * Draws the image associated with the entity on the specified graphics context.
     *
     * @param g            the graphics context to draw on
     * @param center       the center position of the entity
     * @param displayCoords the display coordinates of the entity
     */
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords) {
        g.drawImage(entity.getImage(), (int) displayCoords.X, (int) displayCoords.Y, null);
    }
}
