package Engine.EntityComponents;

import Engine.Entities.Entity;

import java.util.HashMap;

import java.io.Serializable;

import java.util.Iterator;

import java.util.UUID;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * The ComponentData class represents the data associated with an entity's components.
 * It stores the entity's ID, type, UUID, and a map of component IDs to their corresponding values.
 * Used for replicating entity data between the host and clients in the game engine.
 * @author haotian
 */
public class ComponentData implements Serializable {

    public final transient Entity entity;
    public final short entityId;
    public final byte entityType;
    public final UUID entityUUID;
    public final HashMap<Byte, Serializable> components = new HashMap<Byte, Serializable>();

    /**
     * Constructs a ComponentData object for the given entity.
     * @param entity The entity associated with the component data.
     */
    public ComponentData(Entity entity) {
        this.entity = entity;
        this.entityId = entity.EntityID;
        this.entityType = entity.getClassID();
        this.entityUUID = entity.ID;
    }

    /**
     * Sets the value of a component with the specified ID.
     * @param id The ID of the component.
     * @param value The value to set for the component.
     * @throws IllegalArgumentException if the ID is not in the range [0, 255].
     */
    public void setComponent(int id, Serializable value) {
        if (id < 0 || id > 255) {
            throw new IllegalArgumentException("[EntityHandler] id must be in range [0,255] but was " + id);
        }
        id -= 128;
        components.put((byte) id, value);
    }

    /**
     * Returns a string representation of the ComponentData object.
     * @return A string representation of the ComponentData object.
     */
    public String getString() {
        StringBuilder sb = new StringBuilder(toString() + " : { type= " + entityType + " \ncomponents={");
        boolean isFirst = true;
        for (HashMap.Entry<Byte, Serializable> entry : components.entrySet()) {
            if (!isFirst) {
                sb.append(", ");
            }
            Byte key = entry.getKey();
            Serializable value = entry.getValue();
            sb.append(key.byteValue() - Byte.MIN_VALUE).append("=").append(value);
            isFirst = false;
        }
        sb.append("}}");
        return sb.toString();
    }
}
