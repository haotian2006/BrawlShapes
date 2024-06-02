package Engine.EntityComponents.Base.Shapes;

import Engine.EntityHandler;
import Engine.Entities.Entity;
import Engine.EntityComponents.*;
import MathLib.Vector2;
import java.awt.Graphics2D; 

/**
 * The DrawCircle class represents a circle shape that can be drawn on a graphics context.
 * It extends the DrawShape class and provides the implementation for drawing a circle.
 * @author haotian
 */
public class DrawCircle extends DrawShape {
    
    /**
     * Constructs a DrawCircle object with the specified entity.
     * 
     * @param entity the entity associated with the circle shape
     */
    public DrawCircle(Entity entity) {
        super(entity);
    }

    /**
     * Draws the circle shape on the specified graphics context at the given display coordinates and size.
     * 
     * @param g the graphics context to draw on
     * @param displayCoords the display coordinates of the circle shape
     * @param size the size of the circle shape
     */
    public void drawShape(Graphics2D g, Vector2 displayCoords, int size) {
        g.fillOval((int)(displayCoords.X), (int)(displayCoords.Y), size, size);
    }

}
