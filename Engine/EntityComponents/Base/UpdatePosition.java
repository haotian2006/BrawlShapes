package Engine.EntityComponents.Base;

import Engine.Entities.*;
import Engine.EntityComponents.AbstractComponent;
import MathLib.Vector2;

/**
 * The UpdatePosition class is responsible for updating the position of an entity.
 * It implements the BaseComponent interface and extends the AbstractComponent class.|
 * @author haotian
 */
public class UpdatePosition extends AbstractComponent implements BaseComponent {

    /**
     * Constructs a new UpdatePosition object for the specified entity.
     *
     * @param entity the entity whose position will be updated
     */
    public UpdatePosition(Entity entity) {
        super(entity, true, false);
    }

    /**
     * Updates the position of the entity based on the elapsed time.
     *
     * @param dt the elapsed time since the last update
     */
    @Override
    public void update(double dt) {
        if (entity.isSpecial) {
            return;
        }
        Vector2 newPos = engine.collisionHandler.entityVsTile(entity, dt);
        entity.setPosition(newPos);
    }
}
