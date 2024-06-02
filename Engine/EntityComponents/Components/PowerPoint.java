package Engine.EntityComponents.Components;

import Engine.EntityHandler;
import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import Engine.EntityComponents.ComponentData;
import Engine.EntityComponents.SerializableComponent;
import MathLib.Vector2;
import Resources.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The PowerPoint class represents a component that keeps track of points for an entity.
 * It extends the AbstractComponent class and implements the SerializableComponent interface.
 * @author haotian
 */
public class PowerPoint extends AbstractComponent implements SerializableComponent{
    public int points = 0;

    private BufferedImage image = ResourceManager.getImage("Images/Projectiles/Bolt.png"); 

    static {
        EntityHandler.registerComponent(27, PowerPoint.class , (component,value) -> {
            PowerPoint c = (PowerPoint)component;

            c.points = (int)value;
        });
    }
    
    /**
     * Constructs a PowerPoint object for the specified entity.
     * @param entity the entity associated with this PowerPoint object
     */
    public PowerPoint(Entity entity) {
        super(entity, false, true);
    }   

    /**
     * Increments the points by 1 and updates the component value in the entity handler.
     */
    public void increment(){
        points ++;
        if (entity instanceof Brawler){
            Brawler brawler = (Brawler)entity;
            brawler.powerPointsUpdated(points);
        }
        entityHandler.setComponent(entity, 27, points);
    }

    /**
     * Replicates the component data to the specified ComponentData object.
     * @param data the ComponentData object to replicate the data to
     */
    public void replicateEntity(ComponentData data) {
        data.setComponent(27, points);
    }

    /**
     * Draws the PowerPoint component on the graphics context. 
     * Used for debugging.
     * @param g the graphics context to draw on
     * @param center the center position of the entity
     * @param displayCoords the display coordinates of the entity
     * 
     */
    public void draw( Graphics2D g, Vector2 center, Vector2 displayCoords) {
        int Size = (int)(entity.getDisplaySize() * 0.8);
        int SizeX = Size;
        int SizeY = (int)(4*entity.Scale-1);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, (int)(entity.getDisplaySize() * 0.16)));

        String text = points+"";

        Dimension  textSize = g.getFontMetrics().getStringBounds(text, g).getBounds().getSize();
        int textX = (int)(displayCoords.X + SizeX/2 - textSize.getWidth()/2.0)+4- (int)(g.getFontMetrics().getLeading() / 2.0);
        int textY = (int)(displayCoords.Y+SizeY/2 + textSize.getHeight()/2 - 24);

        g.drawImage(image, (int)(textX- textSize.getWidth()/2-8), textY - (int)textSize.getHeight()-6 ,(int)textSize.getHeight(),(int)textSize.getHeight() ,null);
        g.drawString(text, textX , textY - (int)(g.getFontMetrics().getAscent() / 2.0)-4);
    }
}
