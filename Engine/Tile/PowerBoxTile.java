package Engine.Tile;

import Engine.Entities.Special.PowerBoxEntity;

public class PowerBoxTile extends Tile implements Damageable{ 

    PowerBoxEntity entity;

    public PowerBoxTile() {
        super(true);
    }
    public String getImage() {
        return "Images/Tiles/PowerBox.png";
    }

    public void onAdd() {
        if (engine.isHost){
            entity = new PowerBoxEntity(engine,this);
            engine.entityHandler.add(entity);
        }
    }


    public float takeDamage(float damage) {
        return entity.takeDamage(damage);
    }

}
