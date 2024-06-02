package Engine.Controllers;

import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import MathLib.Vector2;

import java.awt.event.KeyEvent;

/**
 * The class representing a player controller in the game.
 * It extends the Controller class and provides functionality for player input.
 * @author haotian
 
 */
public class PlayerController extends Controller {
    
    /**
     * Constructs a new PlayerController object.
     * 
     * @param entity The Brawler entity controlled by this controller.
     * 
     */
    public PlayerController(Brawler entity) {
        super(entity);

        engine.inputManager.MouseBegan.connect(callBack -> {
            if (callBack.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                if (entity.isLocal){
                    engine.replicator.fire();
                    return;
                }
                entity.fire();
            }
        });
        engine.inputManager.InputBegan.connect(callBack -> {
            if (callBack.getKeyCode() == KeyEvent.VK_X) {
                if (entity.isLocal){
                    engine.replicator.useSuper();
                    return;
                }
                entity.activateSuper();
            }
        });
    }
    
    /**
     * Updates the player's position and velocity based on user input.
     * 
     * @param dt The time elapsed since the last update.
     */
    @Override
    public void update(double dt) {
        
        Vector2 velocity = new Vector2();
        if (engine.inputManager.isKeyDown(KeyEvent.VK_W)) {
            velocity = velocity.add(new Vector2(0,-1));
        }
        if (engine.inputManager.isKeyDown(KeyEvent.VK_S)) {
            velocity = velocity.add(new Vector2(0,1));
        }
        if (engine.inputManager.isKeyDown(KeyEvent.VK_A)) {
            velocity = velocity.add(new Vector2(-1,0));
        
        }
        if (engine.inputManager.isKeyDown(KeyEvent.VK_D)) {
            velocity = velocity.add(new Vector2(1,0));
        }

        Vector2 camera =    engine.camera.getRelativeMouseLoc();
        entity.setAngle((int)(entity.getCenter().angleTo(camera)));


        entity.setVelocity(velocity.mul(entity.getSpeed()));

    }
}
