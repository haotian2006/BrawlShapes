package Engine.EntityComponents.Base.Shapes;

import Engine.EntityHandler;
import Engine.Entities.Entity;
import Engine.EntityComponents.*;
import Engine.EntityComponents.Components.ColorComponent;
import MathLib.Vector2;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The abstract class DrawShape represents a base class for drawing shapes on a graphics context.
 * It extends the AbstractComponent class.
 * @author haotian
 */
public abstract class DrawShape extends AbstractComponent {
    
    private ColorComponent colorComponent;

    /**
     * Constructs a DrawShape object with the specified entity.
     * 
     * @param entity the entity associated with the DrawShape object
     */
    public DrawShape(Entity entity) {
        super(entity,false,true);
        colorComponent = entity.getComponent(ColorComponent.class);
    }

    /**
     * Draws the shape on the specified graphics context at the given display coordinates and size.
     * 
     * @param g the graphics context to draw on
     * @param displayCoords the display coordinates of the shape
     * @param size the size of the shape
     */
    abstract public void drawShape(Graphics2D g, Vector2 displayCoords, int size);

    /**
     * Draws the shape on the specified graphics context at the given center and display coordinates.
     * If a color component is available, it sets the color of the graphics context to the color of the component.
     * Otherwise, it sets the color to red.
     * 
     * @param g the graphics context to draw on
     * @param center the center of the shape
     * @param displayCoords the display coordinates of the shape
     */
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords){
        if (colorComponent != null) {
            g.setColor(colorComponent.color);
        }else{
            g.setColor(java.awt.Color.RED);
        }
        drawShape(g, displayCoords, (int)entity.getDisplaySize());
    }
}
