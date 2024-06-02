package Engine.EntityComponents.Base;

import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import MathLib.Vector2;
import java.awt.Graphics2D;

/**
 * The ApplyRotation class is responsible for applying rotation to an entity when drawing it.
 * It is a component that can be added to an entity to enable rotation functionality.
 * @author haotian
 */
public class ApplyRotation extends AbstractComponent implements BaseComponent{
    
    /**
     * Constructs a new ApplyRotation object with the specified entity.
     * 
     * @param entity the entity to apply rotation to
     */
    public ApplyRotation(Entity entity){
        super(entity, false, true);
    }

    /**
     * Draws the entity with rotation applied.
     * 
     * @param g the Graphics2D object to draw on
     * @param center the center position of the entity
     * @param displayCoords the display coordinates of the entity
     */
    public void draw(Graphics2D g, Vector2 center,Vector2 displayCoords){
        int Size = entity.getDisplaySize();
        g.rotate(Math.toRadians(entity.Angle), (int)(displayCoords.X+Size/2), (int)(displayCoords.Y+Size/2));
    }
}
