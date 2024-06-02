package Engine;

import java.util.*;
import java.util.function.*;

import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import Engine.Entities.SpecialEntity;
import Engine.Entities.Special.PowerBoxEntity;
import Engine.EntityComponents.AbstractComponent;
import MathLib.Vector2;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


import java.io.*;
import Signal.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * The EntityHandler class manages entities, their components, and their interactions.
 * It provides functionality to add, remove, and update entities, as well as handling their components.
 * @author haotian
 */
public class EntityHandler {

    private Signal<Entity> entityAdded = new Signal<>();
    public final Event<Entity> EntityAdded = entityAdded.event;

    private Signal<Entity> entityRemoved = new Signal<>();
    public final Event<Entity> EntityRemoved = entityRemoved.event;

    /**
     * The ComponentMap class represents a map of components in the EntityHandler class.
     * It stores the data in a HashMap where the keys are of type Short and the values are HashMaps
     * with keys of type Byte and values of type Serializable.
     */
    protected class ComponentMap implements Serializable {
        private static final long serialVersionUID = 1L;
        public HashMap<Short, HashMap<Byte, Serializable>> data = new HashMap<>();

        /**
         * Puts a value into the component map.
         *
         * @param key The key of the outer map.
         * @param innerKey The key of the inner map.
         * @param value The value to be put into the map.
         */
        public void put(Short key, byte innerKey, Serializable value) {
            data.computeIfAbsent(key, k -> new HashMap<>()).put(innerKey, value);
        }

        /**
         * Checks if the component map is empty.
         *
         * @return true if the component map is empty, false otherwise.
         */
        public boolean isEmpty() {
            return data.isEmpty();
        }

        /**
         * Clears the component map.
         */
        public void clear() {
            data.clear();
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            if (data == null) {
                throw new NullPointerException("data cannot be null");
            }

            oos.writeInt(data.size());
            for (HashMap.Entry<Short, HashMap<Byte, Serializable>> entry : data.entrySet()) {
                oos.writeShort(entry.getKey());
                oos.writeInt(entry.getValue().size());
                for (HashMap.Entry<Byte, Serializable> innerEntry : entry.getValue().entrySet()) {
                    oos.writeByte(innerEntry.getKey());
                    oos.writeObject(innerEntry.getValue());
                }
            }
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            int size = ois.readInt();
            if (size < 0) {
                throw new StreamCorruptedException("Invalid size of ComponentMap: " + size);
            }

            data = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                short key = ois.readShort();
                int innerSize = ois.readInt();
                if (innerSize < 0) {
                    throw new StreamCorruptedException("Invalid size of inner map in ComponentMap: " + innerSize);
                }

                HashMap<Byte, Serializable> innerMap = new HashMap<>(innerSize);
                for (int j = 0; j < innerSize; j++) {
                    byte innerKey = ois.readByte();
                    Serializable value = (Serializable) ois.readObject();
                    innerMap.put(innerKey, value);
                }
                data.put(key, innerMap);
            }
        }

        /**
         * Returns a string representation of the component map.
         *
         * @return The string representation of the component map.
         */
        public String getString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.toString()).append(" : {\n");
            for (HashMap.Entry<Short, HashMap<Byte, Serializable>> entry : data.entrySet()) {
                short key = (short) (entry.getKey() - Short.MIN_VALUE);
                sb.append("  ").append(key).append(": {\n");
                for (HashMap.Entry<Byte, Serializable> innerEntry : entry.getValue().entrySet()) {
                    sb.append("    ").append(innerEntry.getKey().byteValue() - Byte.MIN_VALUE)
                      .append(": ").append(innerEntry.getValue()).append("\n");
                }
                sb.append("  }\n");
            }
            sb.append("}\n");
            return sb.toString();
        }
    }

    // Map of component methods
    private static final HashMap<Byte, BiConsumer<Entity, Serializable>> ComponentMethods = new HashMap<>();

    // Initial size for entity maps
    private final int INIT_SIZE = 100;

    // Maps to store entities by different keys
    private HashMap<UUID, Entity> entities = new HashMap<>(INIT_SIZE);
    private HashMap<UUID, Brawler> brawlers = new HashMap<>(INIT_SIZE);
    private HashMap<UUID, SpecialEntity> specialEntities = new HashMap<>(INIT_SIZE);
    private HashMap<Short, Entity> entitiesById = new HashMap<>(INIT_SIZE);
    private HashMap<Short, List<ComponentMap>> delayedData = new HashMap<>();

    // ID generator for entities
    private IdGenerator idGenerator = new IdGenerator();

    // Current component data map
    private ComponentMap currentComponentData = new ComponentMap();

    // Reference to the engine
    public final Engine engine;

    /**
     * Gets the next available ID.
     *
     * @return The next available ID.
     */
    public short getNextId() {
        return idGenerator.getNext();
    }

    /**
     * Registers a method for a component with the given ID.
     *
     * @param id The ID of the component.
     * @param m The method to register.
     */
    public static void registerMethod(int id, BiConsumer<Entity, Serializable> m) {
        if (id < 0 || id > 255) {
            throw new IllegalArgumentException("[EntityHandler] id must be in range [0,255] but was " + id);
        }
        id -= 128;
        if (!ComponentMethods.containsKey((byte) id)) {
            ComponentMethods.put((byte) id, m);
        } else {
            throw new IllegalArgumentException("[EntityHandler] Method already registered for id " + (id + 128));
        }
    }

    /**
     * Registers a component with the given ID and class.
     *
     * @param id The ID of the component.
     * @param c The class of the component.
     * @param method The method to register.
     * @param <T> The type of the component.
     */
    public static <T extends AbstractComponent> void registerComponent(int id, Class<T> c, BiConsumer<? super T, ? super Serializable> method) {
        BiConsumer<Entity, Serializable> f = (entity, value) -> {
            T component = entity.getComponent(c);
            if (component == null) {
                return;
            }
            method.accept(component, value);
        };
        registerMethod(id, f);
    }

    /**
     * Parses the data in the given ComponentMap and updates the corresponding entities.
     * If an entity does not exist, the data is stored in a delayedData map for future processing.
     *
     * @param map The ComponentMap containing the data to be parsed.
     */
    synchronized protected void parseData(ComponentMap map) {
        for (HashMap.Entry<Short, HashMap<Byte, Serializable>> entry : map.data.entrySet()) {
            Entity entity = entitiesById.get(entry.getKey());
            if (entity == null) {
                delayedData.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(map);
                continue;
            }
            parseDataFor(entity, entry.getValue());
        }
    }

    /**
     * Parses data for a specific entity.
     *
     * @param entity The entity to update.
     * @param comp The component data to parse.
     */
    public void parseDataFor(Entity entity, HashMap<Byte, Serializable> comp) {
        for (HashMap.Entry<Byte, Serializable> entry : comp.entrySet()) {
            if (entity.isLocal && entry.getKey()+128 < 10){

                continue;
            }
            ComponentMethods.get(entry.getKey()).accept(entity, entry.getValue());
        }
    }

    /**
     * Constructs an EntityHandler with the specified engine.
     *
     * @param engine The engine to associate with this handler.
     */
    protected EntityHandler(Engine engine) {
        this.engine = engine;
    }

    /**
     * Sets a component value for an entity.
     *
     * @param entity The entity to update.
     * @param id The ID of the component.
     * @param value The value to set.
     */
    synchronized public void setComponent(Entity entity, int id, Serializable value) {
        if (!entities.containsKey(entity.ID)  || (!engine.isHost && !entity.isLocal) || (!engine.isHost && entity.isLocal && id > 10)) {
            return;
        }
        id += 128;
        currentComponentData.put(entity.EntityID, (byte) id, value);
    }

    /**
     * Adds an entity to the handler.
     *
     * @param entity The entity to add.
     */
    synchronized public void add(Entity entity) {
        if (entity.Destroyed) {
            return;
        }
        if (engine.isHost) {
            entity.EntityID = getNextId();
            engine.replicator.sendEntity(entity);
        }
        entitiesById.put(entity.EntityID, entity);
        entities.put(entity.ID, entity);
        //System.out.println( "Adding entity " + entity.ID + " " + entity);
        if (entity instanceof Brawler) {
            brawlers.put(entity.ID, (Brawler) entity);
        } else if (entity instanceof SpecialEntity) {
            specialEntities.put(entity.ID, (SpecialEntity) entity);
        }
        if (delayedData.containsKey(entity.EntityID)) {
            for (ComponentMap map : delayedData.get(entity.EntityID)) {
                parseData(map);
            }
            delayedData.remove(entity.EntityID);
        }
        entityAdded.fire(entity);
    }

    /**
     * Removes an entity by its UUID.
     *
     * @param id The UUID of the entity to remove.
     * @return The removed entity, or null if not found.
     */
    synchronized public Entity remove(UUID id) {
        Entity e = entities.get(id);
      //  System.out.println("Removing entity " + id + " " + e);
        if (e == null) {
           // engine.taskScheduler.scheduleTask(2, newId -> remove2(newId), id);
            return null;
        }
        if (engine.isHost) {
            engine.replicator.removeEntity(e);
            idGenerator.push(e.EntityID);
        }

        entities.remove(id);
        entitiesById.remove(e.EntityID);
       // System.out.println("Removed entity " + id + " " + e);
        if (e instanceof Brawler) {
            brawlers.remove(e.ID);
        } else if (e instanceof SpecialEntity) {
            specialEntities.remove(e.ID);
        }
        entityRemoved.fire(e);
        return e;
    }

    synchronized public Entity remove2(UUID id) {
        Entity e = entities.get(id);
        if (e == null) {
            
            return null;
        }
        if (engine.isHost) {
            engine.replicator.removeEntity(e);
        }
        if (e instanceof Brawler) {
            brawlers.remove(e.ID);
        } else if (e instanceof SpecialEntity) {
            specialEntities.remove(e.ID);
        }
        entitiesById.remove(e.EntityID);
        idGenerator.push(e.EntityID);
        entityRemoved.fire(e);
        return entities.remove(id);
    }

    /**
     * Removes an entity.
     *
     * @param e The entity to remove.
     * @return The removed entity, or null if not found.
     */
    public Entity remove(Entity e) {
        if (e == null) return null;
        return remove(e.ID);
    }

    /**
     * Removes an entity by its short ID.
     *
     * @param e The short ID of the entity to remove.
     * @return The removed entity, or null if not found.
     */
    public Entity remove(short e) {
        return remove(entitiesById.get(e));
    }

    /**
     * Gets an entity by its UUID.
     *
     * @param id The UUID of the entity to get.
     * @return The entity, or null if not found.
     */
    public Entity get(UUID id) {
        return entities.get(id);
    }

    /**
     * Gets an entity by its short ID.
     *
     * @param id The short ID of the entity to get.
     * @return The entity, or null if not found.
     */
    public Entity get(short id) {
        return entitiesById.get(id);
    }

    /**
     * Checks if the handler contains an entity with the given UUID.
     *
     * @param id The UUID of the entity.
     * @return true if the handler contains the entity, false otherwise.
     */
    public boolean contains(UUID id) {
        return entities.containsKey(id);
    }

    /**
     * Checks if the handler contains the given entity.
     *
     * @param e The entity to check.
     * @return true if the handler contains the entity, false otherwise.
     */
    public boolean contains(Entity e) {
        return entities.containsKey(e.ID);
    }

    /**
     * Gets the number of entities managed by the handler.
     *
     * @return The number of entities.
     */
    public int numOfEntities() {
        return entities.size();
    }

    /**
     * Gets the number of brawlers managed by the handler.
     *
     * @return The number of brawlers.
     */
    public int numOfBrawlers() {
        return brawlers.size();
    }

    /**
     * Gets the number of special entities managed by the handler.
     *
     * @return The number of special entities.
     */
    public int numbOfSpecial() {
        return specialEntities.size();
    }

    /**
     * Gets a collection of all entities managed by the handler.
     *
     * @return A collection of all entities.
     */
    public Collection<Entity> getAllEntities() {
        return  new ArrayList<>(entities.values());
    }

    /**
     * Gets a collection of all brawlers managed by the handler.
     *
     * @return A collection of all brawlers.
     */
    public ArrayList<Brawler> getAllBrawlers() {
        return new ArrayList<>(brawlers.values());
    }

    /**
     * Gets a collection of all special entities managed by the handler.
     *
     * @return A collection of all special entities.
     */
    public Collection<SpecialEntity> getAllSpecial() {
        return  new ArrayList<>(specialEntities.values());
    }

    /**
     * Updates the state of all entities managed by the handler.
     *
     * @param dt The delta time since the last update.
     */
    synchronized protected void update(double dt) {
        if (engine.session.isHost) {
            for (Entity e : new ArrayList<>(entities.values())) {
                if (e != null && !e.Destroyed) {
                    long start = System.nanoTime();
                    e.update(dt);
                    long end = System.nanoTime();
                    if (end - start > 1000000) {
                        // System.out.println("Updating entity " + e.getClass().getName() + " took " + (end - start) / 1000000.0 + " ms");
                    }
                }
            }
        } else if (engine.getPrimary() != null) {
            engine.getPrimary().update(dt);
        }

        if (!currentComponentData.isEmpty()) {
            engine.replicator.sendEntityData(currentComponentData);
            currentComponentData = new ComponentMap();
        }
    }

    /**
     * Draws all entities managed by the handler.
     *
     * @param g The Graphics2D object used for drawing.
     * @param center The center point for drawing.
     */
    synchronized protected void draw(Graphics2D g, Vector2 center) {
        AffineTransform originalTransform = g.getTransform();
        for (Entity e : new ArrayList<>(entities.values())) {
            if (e != null && !e.Destroyed) {
                e.draw(g, center);
                g.setTransform(originalTransform);
            }
        }
    }

    /**
     * Destroys the handler, disconnecting all events.
     */
    public void destroy() {
        entityAdded.disconnectAll();
        entityRemoved.disconnectAll();
         
    }
}
