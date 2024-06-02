package Engine.Controllers;

import Engine.Entities.Brawler;

import Engine.Engine;

/**
 * The Controller class is an abstract class that represents a controller for a Brawler entity in the game.
 * It provides a common interface for all controllers and contains methods for updating the controller and destroying it.
 */
public abstract class Controller {
    public final Brawler entity;
    public final Engine engine;

    /**
     * Constructs a new Controller object with the specified Brawler entity.
     *
     * @param entity the Brawler entity associated with this controller
     */
    public Controller(Brawler entity){
        this.entity = entity;
        this.engine = entity.engine;
    }

    /**
     * Updates the controller with the specified time step.
     *
     * @param dt the time step in seconds
     */
    abstract public void update(double dt);

    /**
     * Destroys the controller.
     */
    public void destroy(){

    }
}
