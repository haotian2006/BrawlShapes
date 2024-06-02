package Engine.Controllers;

import Engine.Engine;
import Engine.Entities.*;
import Engine.Entities.Special.PowerBoxEntity;
import Engine.Entities.Special.PowerShard;
import Engine.EntityComponents.Components.Health;
import Engine.Tile.*;
import MathLib.Vector2;
import java.util.*;

/**
 * The AIController class is responsible for controlling AI entities in the game.
 * It manages the behavior and decision-making process of the AI, including movement, targeting, and attacking.
 * @author Joey
 */
public class AIController extends Controller {

    private Entity target = null;
    private List<Vector2> currentPath;
    private int node = 0;
    private float lastHealth;

    /**
     * Constructs an AIController for the specified Brawler entity.
     * 
     * @param entity The Brawler entity this controller will manage.
     */
    public AIController(Brawler entity) {
        super(entity);  
        lastHealth = entity.getHealth();
        entity.setSpeed(5.1f);
    }

    /**
     * Updates the state of the AIController.
     * 
     * @param dt The time elapsed since the last update.
     */
    public void update(double dt) {
      
        if (currentPath == null) {
            Entity e = findClosestEntity();
            SpecialEntity p = findClosestSpecial();
           
            if (e != null && p != null) {
                target = null;
                float d1 = entity.getPosition().distanceTo(e.getPosition());
                float d2 = entity.getPosition().distanceTo(((Entity) p).getPosition());
                
                if (d2 < d1) {
                    if (p instanceof SpecialEntity && !(p instanceof PowerShard)) {
                        target = (Entity) p;
                        float x = entity.getCenter().angleTo(target.getCenter());
                        entity.setAngle((int) x);
                        moveTowardsPath();
                        attack();
                        float currentHealth = entity.getHealth();
                        if (currentHealth < lastHealth || e.getPosition().sub(entity.getPosition()).magnitude() < 5) {
                           
                            target = e;
                            moveTowardsPath();
                            lastHealth = currentHealth;
                        }
                    } else if (p instanceof PowerShard) {
                        target = (Entity) p;
                        float x = entity.getCenter().angleTo(target.getCenter());
                        entity.setAngle((int) x);
                        moveTowardsPath();
                    }
                } else if (! (e instanceof PowerShard)) {
                    target = e;
                    attack();
                }
            } else if (e != null) {
                target = e;
            } else if (p != null) {
                target = (Entity) p;
            }
            node = 0;
            if (target == null) {
                return;
            }
            if (target instanceof Brawler) {
                Vector2 direction = target.getPosition().sub(entity.getPosition());
                direction = entity.getPosition().add(direction.normalize().mul(direction.magnitude() - 1));
                updatePath(engine.tileHandler.pathFinder.findPath(entity.getPosition(), target.getPosition()));
            } else {
                updatePath(engine.tileHandler.pathFinder.findPath(entity.getPosition(), target.getPosition()));
            }
        }
        moveTowardsPath();

        if (target instanceof Brawler) {
            float magnitude = entity.getPosition().sub(target.getPosition()).magnitude();
            if (magnitude < 10) {
                float x = entity.getCenter().angleTo(target.getCenter());
                entity.setAngle((int) x);
                attack();
            }
        }
        AIlogic();
    }

    /**
     * Finds the closest Brawler entity to the controlled entity.
     * 
     * @return The closest Brawler entity.
     */
    public Entity findClosestEntity() {
        float x = 100000;
        Brawler e = null;
        for (Brawler b : engine.entityHandler.getAllBrawlers()) {
            float magnitude = b.getPosition().distanceTo(entity.getPosition());
            if (magnitude < x && b != entity) {
                x = magnitude;
                e = b;
            }
        }
        return e;
    }

    /**
     * Finds the closest SpecialEntity to the controlled entity.
     * 
     * @return The closest SpecialEntity.
     */
    public SpecialEntity findClosestSpecial() {
        float x = 100000;
        SpecialEntity e = null;
        for (SpecialEntity b : engine.entityHandler.getAllSpecial()) {
            float magnitude = ((Entity) (b)).getPosition().distanceTo(entity.getPosition());
            if (magnitude < x) {
                x = magnitude;
                e = b;
            }    
        }
        return e;
    }   

    /**
     * Moves the controlled entity towards the next node in the current path.
     */
    private void moveTowardsPath() {
        if (currentPath == null) {
            entity.setVelocity(Vector2.ZERO);
            return;
        }
        if (node >= currentPath.size()) {
            updatePath(null);
            return;
        }
        Vector2 target = currentPath.get(node).add(new Vector2(.5f, .5f));
      
        float magnitude = target.sub(entity.getCenter()).magnitude();
        if (magnitude < .7) {
            node += 1;
            moveTowardsPath();
            return;
        }
    
        float x = entity.getCenter().angleTo(target);
        entity.setAngle((int) x);
        Vector2 direction = target.sub(entity.getCenter()).normalize();
        if (direction.isNaN()) {
            return;
        } else {
            entity.setVelocity(direction.normalize().mul(entity.getSpeed()));
        }
    }

    /**
     * Updates the current path for the controlled entity.
     * 
     * @param path The new path to follow.
     */
    private void updatePath(List<Vector2> path) {
        if (path == null) {
            entity.setVelocity(Vector2.ZERO);
            if (currentPath != null && Engine.DEBUGGING) {
                for (Vector2 v : currentPath) {
                    if (engine.tileHandler.getTile((int) v.X, (int) v.Y) instanceof DebugTile) {
                        engine.tileHandler.setTile((int) v.X, (int) v.Y, new FloorTile());
                    }
                }
            }
            currentPath = null;
            return;
        }

        currentPath = path;
        if (!Engine.DEBUGGING) {
           return;
        }
        for (Vector2 v : currentPath) {
            Tile x = engine.tileHandler.getTile((int) v.X, (int) v.Y);
            if (x == null || x instanceof FloorTile) {
                engine.tileHandler.setTile((int) v.X, (int) v.Y, new DebugTile());
            }
        }
    }

    /**
     * Executes an attack action for the controlled entity. Executes the flee action.
     */
    public void attack() {
        if (!entity.canUseSuper()) {
            if (target != null && entity.canFire()) {
                entity.fire();
            }
        } else {
            entity.activateSuper();
            entity.fire();
        }
    }

    /**
     * Executes additional AI logic for the controlled entity.
     */
    public void AIlogic() {
        Entity e = findClosestEntity();
        if(e==null)
        {
            return;
        }
        if (entity.getHealth() < 0.35 * ((Health) entity.getComponent(Health.class)).MaxHealth||(target instanceof Brawler &&target.getPosition().sub(entity.getPosition()).magnitude()<2)) {
           
            Vector2 v = e.getPosition();
            float x = entity.getCenter().angleTo(v);
            Vector2 direction = v.sub(entity.getCenter()).normalize().inverse();
            updatePath(engine.tileHandler.pathFinder.findPath(entity.getPosition(), direction));
            entity.setVelocity(direction.normalize().mul(entity.getSpeed()*1.2f));
        }
    }
}
