package Engine.EntityComponents;

/**
 * The SerializableComponent interface represents a component that can be serialized.
 * Classes implementing this interface can replicate entity data.
 * @author ha
 * 
 */
public interface SerializableComponent {
    
    /**
     * Replicates the entity data using the provided ComponentData object.
     * 
     * @param data the ComponentData object used to replicate the entity data
     */
    public void replicateEntity(ComponentData data);
}
