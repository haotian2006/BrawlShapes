package Engine.Tile;

/**
 * The Damageable interface represents an tile that can take damage.
 * @author haotian
 */
public interface Damageable {
    
    /**
     * Takes the specified amount of damage.
     * 
     * @param damage the amount of damage to be taken
     * @return the remaining health after taking the damage
     */
    public float takeDamage(float damage);
}
