package Engine.Controllers;

import Engine.Engine;
import Engine.Entities.*;
import Engine.Entities.Special.PowerBoxEntity;
import Engine.Entities.Special.PowerShard;
import Engine.EntityComponents.Components.Health;
import Engine.Tile.*;
import MathLib.Vector2;
import java.util.*;
public class OldAIController extends Controller {

    private Entity target=null;
    private List<Vector2>currentPath;
    private int node=0;
    private float lastHealth;
      public OldAIController(Brawler entity){
        super(entity);  
lastHealth=entity.getHealth();
entity.setSpeed(5.1f);
    }

    public void update(double dt) {
        //System.out.println("hi");
         if(currentPath==null)
         {
            Entity e = findClosestEntity();
           SpecialEntity p = findClosestSpecial();
           
            if (e != null && p != null) {
               target = null;
                float d1 = entity.getPosition().distanceTo(e.getPosition());
                float d2 = entity.getPosition().distanceTo(((Entity) p).getPosition());
                // if(e.getPosition().sub(entity.getPosition()).magnitude()<5){
                //   target=e;
                //   moveTowardsPath();
                //     }
                
                if (d2 < d1) {

                  //  System.out.println("hello");
                    if( p instanceof SpecialEntity )//&& (target == null || target instanceof SpecialEntity))
                    {
                       // System.out.println("hi there");
                       
                        target=(Entity) p;
                        float x=entity.getCenter().angleTo(target.getCenter());
                        entity.setAngle((int)x);
                        moveTowardsPath();
                        attack();
                        float currentHealth=entity.getHealth();
                        if(currentHealth<lastHealth||e.getPosition().sub(entity.getPosition()).magnitude()<5)
                        {
                            System.out.println("im here");
                    target=e;
                       moveTowardsPath();
                    lastHealth=currentHealth;
                  //  attack();
                        }
                    }
                 //   System.out.println(p);
                   else if(p instanceof PowerShard )
                    {
                        System.out.println("hi");
                        target=(Entity)p;
                        float x=entity.getCenter().angleTo(target.getCenter());
                        entity.setAngle((int)x);
                        moveTowardsPath();

                    }
        
                } else {
                    target = e;
                    attack();
                }
            } else if (e != null) {
                target = e;
            } else if (p != null) {
                target = (Entity)p;
            }
            node=0;
            if (target == null) {
                return;
            }
            if(target instanceof Brawler){
            
            Vector2 direction= target.getPosition().sub(entity.getPosition());
            direction=entity.getPosition().add(direction.normalize().mul(direction.magnitude()-1));
            updatePath(engine.tileHandler.pathFinder.findPath(entity.getPosition(), target.getPosition()));
            
        }
         else{
        updatePath(engine.tileHandler.pathFinder.findPath(entity.getPosition(), target.getPosition()));
         }
        }
        moveTowardsPath();

        if(target instanceof Brawler)
        {
            float magnitude=entity.getPosition().sub(target.getPosition()).magnitude();
            if(magnitude<10){
                float x=entity.getCenter().angleTo(target.getCenter());
                entity.setAngle((int)x);
                attack();
            }
        }
        AIlogic();
      
    }
    public Entity findClosestEntity()
    {
        float x=100000;
        Brawler e=null;
         for(Brawler b:engine.entityHandler.getAllBrawlers())
        {
            float magnitude=b.getPosition().distanceTo(entity.getPosition());
            if(magnitude<x&&b!=entity )
            {
                x=magnitude;
                e=b;
            }

        }
        return e;
    }

    public SpecialEntity findClosestSpecial()
    {
        float x=100000;
       SpecialEntity e=null;
         for(SpecialEntity b:engine.entityHandler.getAllSpecial())
        {
            float magnitude=((Entity) (b)).getPosition().distanceTo(entity.getPosition());
            if(magnitude<x)
            {
                x=magnitude;
                e= b;
            }    
         
        }
        return e;
    }   

    private void moveTowardsPath(){
        if(currentPath==null)
        {
            entity.setVelocity(Vector2.ZERO);
            return;
        }
    if(node>=currentPath.size())
    {
        updatePath(null);
        return;
    }
        Vector2 target=currentPath.get(node).add(new Vector2(.5f,.5f));
        //System.out.println(currentPath.size());
      
        float magnitude=target.sub(entity.getCenter()).magnitude();
        //System.out.println(magnitude);
        if(magnitude<.8)
        {
        
            node+=1;
             moveTowardsPath();
             return;
        }
    
        float x=entity.getCenter().angleTo(target);
        entity.setAngle((int)x);
        Vector2 direction=target.sub(entity.getCenter()).normalize();
       // direction =target.sub(entity.getCenter()).add(direction.mul(2));
        if(direction.isNaN()){
            return;
        }
        // System.out.println(target); 
        // System.out.println(entity.getPosition());
        // if(target!=null&&target.sub(entity.getPosition()).magnitude()<5)
        // {
        //     entity.setVelocity(Vector2.ZERO);
        // }
        else
            entity.setVelocity(direction.normalize().mul(entity.getSpeed()));

    }

   private void updatePath(List<Vector2> path){
        if (path == null) {
            entity.setVelocity(Vector2.ZERO);
            if (currentPath != null && Engine.DEBUGGING) {
                for (Vector2 v : currentPath) {
                    if (engine.tileHandler.getTile((int) v.X, (int) v.Y) instanceof DebugTile){
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
            if (x == null || x instanceof FloorTile){
                engine.tileHandler.setTile((int) v.X, (int) v.Y, new DebugTile());
            }
        }

    }
    public void attack()
    {
        if(entity.canUseSuper()==false){
        if(target!=null && entity.canFire())
        {
            //System.out.println("hi1");
            entity.fire();
        }
    }
    else if(entity.canUseSuper()==true){
       // System.out.println("hi2");
        entity.activateSuper();
        entity.fire();
    }

    }
public void AIlogic(){
    if(entity.getHealth()<0.3*((Health)entity.getComponent(Health.class)).MaxHealth)
    {
        Entity e=findClosestEntity();
        Vector2 v=e.getPosition();
        float x=entity.getCenter().angleTo(v);
        Vector2 direction=v.sub(entity.getCenter()).normalize().inverse();
        updatePath(engine.tileHandler.pathFinder.findPath(entity.getPosition(), direction));
    ///  moveTowardsPath();
         entity.setVelocity(direction.normalize().mul(entity.getSpeed()));
        
    }
  
    
    

}
        
}