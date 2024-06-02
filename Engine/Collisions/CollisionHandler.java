package Engine.Collisions;

import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import MathLib.*;

import Engine.Tile.Tile;

import java.util.*;

import Engine.*;

/**
 * The CollisionHandler class handles collision detection and resolution between entities and tiles.
 * It provides methods to check for collisions between entities and tiles, as well as retrieving entities within a specified bounding box.
 */
public class CollisionHandler {
        private TileHandler tileHandler;
        private Engine engine;
        public static final float EPSILON = 0.001f;

        public CollisionHandler(Engine engine) {
            this.engine = engine;
            if (engine.tileHandler != null) {
                tileHandler = engine.tileHandler;
            }
        }
        /**
         * Updates the collision handler with the specified time step.
         * @param dt The time step in seconds.
         */
        public void update(double dt) {
            for (Entity entity : engine.entityHandler.getAllEntities()) {
                if (entity.getVelocity().equals(Vector2.ZERO)) {
                    continue;
                }
                Vector2 newPos = entityVsTile(entity, dt);
                entity.setPosition(newPos);
            }
        }

        /**
         *  Checks for collisions between an entity and tiles in the game world.
         * @param entity The entity to check for collisions.
         */

        private Tuple2<Float,Vector2> entityVsTileLoop(Entity entity, AABB modifiedAABB ){
            if (tileHandler == null) {
                return new Tuple2<>(1f, new Vector2(0, 0));
            }
            float minTime = 1;
            AABB broadPhase = modifiedAABB.getBroadphase();
            int[] normal = new int[2];
            Vector2 start = modifiedAABB.Position.add(Vector2.ONE.mul(-1f));
            Vector2 end = modifiedAABB.Position.add(modifiedAABB.Size).add(modifiedAABB.Velocity).add(Vector2.ONE.mul(1f));
            for (int x = (int)start.X; x < end.X; x++) {
                for (int y = (int)start.Y; y < end.Y; y++) {
                    Tile at = tileHandler.getTile(x, y);
                    if ( at != null) {
                        AABB tileAABB = at.Collisions;
                        if (!broadPhase.overlaps(tileAABB)) {
                            continue;
                        }   

                        if (!entity.onCollision(at)){
                            continue;
                        }
                        int[] normal_ = new int[2];
                        float time = modifiedAABB.sweptAABB(tileAABB, minTime, normal_);
                       // System.out.println(time);

                        if (time < minTime) {
                            minTime = time; 
                            normal = normal_;
                        }


                    }
                }
            }
        return new Tuple2<>(minTime, new Vector2(normal[0], normal[1]));
    }


    public Entity[] getEntityInBounds(AABB aabb){ 
        Entity[] passes = new Entity[engine.entityHandler.numOfEntities()];
        int realSize = 0;
        for (Entity x : engine.entityHandler.getAllEntities()) {
            if (aabb.overlaps(x.HitBox)) {
                passes[realSize] = x;
                realSize++;
            }
        }

        return Arrays.copyOfRange(passes, 0, realSize);
    }

    public Brawler[] getBrawlerInBounds(AABB aabb){ 
        Brawler[] passes = new Brawler[engine.entityHandler.numOfBrawlers()];
        int realSize = 0;
        for (Brawler x : engine.entityHandler.getAllBrawlers()) {
            if (aabb.overlaps(x.HitBox)) {
                passes[realSize] = x;
                realSize++;
            }
        }

        return Arrays.copyOfRange(passes, 0, realSize);
    }



    public Vector2 entityVsTile(Entity entity,double dt){
        if (tileHandler == null ||entity.getVelocity().equals(Vector2.ZERO)) {

            return entity.getPosition();
        }
        AABB modified = entity.HitBox.copy();
        Vector2 position = modified.Position;
        Vector2 velocity = modified.Velocity.mul((float)dt);
        float remainingTime = 1;
        float minTime = 1;
        Vector2 normal = new Vector2();

        for (int i = 0; i < 2; i++) {
            float vx = velocity.X * (1-Math.abs(normal.X))*remainingTime;
            float vy = velocity.Y*(1-Math.abs(normal.Y))*remainingTime;
            velocity  = new Vector2(vx, vy);

            modified.Velocity = velocity;
            modified.Position = position;
            Tuple2<Float,Vector2> t = entityVsTileLoop(entity, modified);
            minTime = t.X;
            normal = t.Y;
            
            position = position.add(velocity.mul(minTime));
            // if (minTime<1){
            //     position = position.add(normal.mul(EPSILON));
            // }

            if (minTime < 1) {
                
                if (velocity.X > 0 && velocity.X != 0) {
                    position = new Vector2(position.X - EPSILON, position.Y);
                } else if (velocity.X < 0) {
                    position = new Vector2(position.X + EPSILON, position.Y);
                }
                if (velocity.Y > 0) {
                    position = new Vector2(position.X, position.Y - EPSILON);
                } else if (velocity.Y < 0) {
                    position = new Vector2(position.X, position.Y + EPSILON);
                }
            }

            remainingTime = 1-minTime;
            if (remainingTime <= 0) break;
        }
        return position;

    }
}
