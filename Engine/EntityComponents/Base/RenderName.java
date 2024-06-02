package Engine.EntityComponents.Base;

import Engine.EntityHandler;
import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import Engine.EntityComponents.ComponentData;
import MathLib.Vector2;

import Engine.EntityComponents.SerializableComponent;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.Font;
import java.awt.Dimension;

/**
 * The RenderName class is responsible for rendering the name of an entity on the screen.
 * It implements the BaseComponent interface and extends the AbstractComponent class.\
 * @author haotian
 */
public class RenderName extends AbstractComponent implements BaseComponent {

    /**
     * Constructs a new RenderName object for the given entity.
     *
     * @param entity the entity to render the name for
     */
    public RenderName(Entity entity) {
        super(entity, false, true);
    }

    /**
     * Draws the name of the entity on the screen.
     *
     * @param g            the Graphics2D object to draw on
     * @param center       the center position of the entity
     * @param displayCoords the display coordinates of the entity
     */
    @Override
    public void draw(Graphics2D g, Vector2 center, Vector2 displayCoords) {
        int Size = (int) (entity.getDisplaySize() * 0.8);
        int SizeX = Size;
        int SizeY = (int) (4 * entity.Scale - 1);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, (int) (entity.getDisplaySize() * 0.17)));

        String text = entity.Name;

        Dimension textSize = g.getFontMetrics().getStringBounds(text, g).getBounds().getSize();
        int textX = (int) (displayCoords.X + SizeX / 2 - textSize.getWidth() / 2.0) + 4;
        int textY = (int) (displayCoords.Y + SizeY / 2 + textSize.getHeight() / 2 - 12);

        g.drawString(text, textX - (int) (g.getFontMetrics().getLeading() / 2.0),
                textY - (int) (g.getFontMetrics().getAscent() / 2.0));
    }
}
