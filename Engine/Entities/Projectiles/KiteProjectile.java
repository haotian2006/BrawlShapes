package Engine.Entities.Projectiles;
//package Engine.Entities.Projectiles;

import Engine.Entities.*;
import Engine.EntityComponents.Base.Shapes.*;
import Engine.EntityComponents.Base.*;
import Engine.EntityComponents.Components.*;
import Engine.Tile.Damageable;

import java.util.*;

import Engine.*;

import java.awt.*;

import MathLib.*;



public class KiteProjectile extends Projectile {


    public KiteProjectile(Engine engine, Brawler parent) {
        super(engine, parent);
        setScale(.5);
    }

    public KiteProjectile(Engine engine) {
        this(engine, null);
    }


    public void doDamage(Brawler b) {
        float damage1 = damage;
      
        float dmg = b.takeDamage(damage1);

        if (brawler != null) {
            brawler.didDamage(dmg);
        }
    }


    public String getImagePathFromVariant() {
        if (Variant == "Super"){
            return "Images/Projectiles/KiteSuperProjectile.png";
        }
        return "Images/Projectiles/KiteProjectile.png";
    }
}
