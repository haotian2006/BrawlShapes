package Engine;

import java.util.*;
import Networking.*;
import Signal.ConnectionContainer;
import Engine.*;

import Engine.Entities.*;
import Engine.EntityComponents.*;
import Engine.Session.SessionPlayer;
import MathLib.Vector2;
    
import java.io.*;

import Engine.Tile.*;

/**
 * The Replicator class handles the replication of entities and game data between the host and clients in the game engine.
 * It is responsible for sending and receiving entity data and game map data between the host and clients.
 * @author haotian
 */
public class Replicator {
    public final Engine engine;
    public final NetworkHandler networkHandler;

    private final ConnectionContainer connectionContainer = new ConnectionContainer();

    private HashSet<UUID> toRemove = new HashSet<UUID>();

    public final boolean isHost;

    /**
     * Constructs a Replicator object.
     * @param engine The game engine.
     */
    public Replicator(Engine engine){
        this.engine = engine;
        isHost = engine.session.isHost;
        networkHandler = engine.session.networkHandler;
        if (networkHandler == null){
            return;
        }

        if (!engine.session.isHost){
            networkHandler.getRemote("EntityAdded").onMessage.connect(packet -> {
                ComponentData data = (ComponentData)packet.getPayLoad();
                Entity entity = Entity.fromComponentData(engine,data);
                engine.entityHandler.add(entity);
                if (toRemove.contains(entity.ID) ){
                    entity.destroy();
                }
            }, connectionContainer);
    
            networkHandler.getRemote("EntityRemoved").onMessage.connect(packet -> {
                UUID id = (UUID)packet.getPayLoad();;
                Entity entity = engine.entityHandler.get(id);
                if (entity == null){
                    toRemove.add(id);
                    return;
                } 
                entity.destroy();
    
            }, connectionContainer);

            networkHandler.getRemote("ReplicateMap").onMessage.connect(packet -> {
                char[][] map = ( char[][] )packet.getPayLoad();
                engine.tileHandler.loadMap(new Map(map));
            }, connectionContainer);

            
            networkHandler.getRemote("AddTile").onMessage.connect(packet -> {
                TileData data = (TileData)packet.getPayLoad();
                engine.tileHandler.addTile(data.x, data.y,data.tile);
            }, connectionContainer);

            networkHandler.getRemote("RemoveTile").onMessage.connect(packet -> {
                TileData data = (TileData)packet.getPayLoad();
                engine.tileHandler.removeTile(data.x, data.y);
            }, connectionContainer);

            networkHandler.getRemote("SetOwner").onMessage.connect(packet -> {
                UUID id = (UUID)packet.getPayLoad();
                System.out.println(engine.session.localPlayer.toString());
                engine.setOwner(engine.session.localPlayer,id);
            }, connectionContainer);
            networkHandler.getRemote("EntityComponents").onMessage.connect(packet -> {
                engine.entityHandler.parseData((EntityHandler.ComponentMap)packet.getPayLoad());
            }, connectionContainer);
    
        }else{
            networkHandler.getRemote("EntityComponents").onMessage.connect(packet -> {
                engine.entityHandler.parseData((EntityHandler.ComponentMap)packet.getPayLoad());
            }, connectionContainer);

            networkHandler.getRemote("UseSuper").onMessage.connect(packet -> {
                Brawler b = (Brawler)engine.entityHandler.get((UUID)packet.getPayLoad());
                if (b != null){
                    b.activateSuper();
                }
            }, connectionContainer);

            
            networkHandler.getRemote("Fire").onMessage.connect(packet -> {
                Brawler b = (Brawler)engine.entityHandler.get((UUID)packet.getPayLoad());
                if (b != null){
                    b.fire();
                }
            }, connectionContainer);
    
        }


        
    }

    /**
     * Fires the primary weapon of the player's controlled entity.
     */
    public void fire(){
        if (networkHandler == null){
            return;
        }

        Packet packet = new Packet();
        packet.addToPayLoad(engine.primaryEntity);
        networkHandler.getRemote("Fire").fireServer(packet);
    }

    /**
     * Activates the super ability of the player's controlled entity.
     */
    public void useSuper(){
        if (networkHandler == null){
            return;
        }

        Packet packet = new Packet();
        packet.addToPayLoad(engine.primaryEntity);
        networkHandler.getRemote("UseSuper").fireServer(packet);
    }

    /**
     * Sets the owner of an entity for a specific player.
     * @param e The entity to set the owner for.
     * @param p The player who will be the owner of the entity.
     */
    protected void setOwnerForEntity(Entity e, SessionPlayer p){
        if (networkHandler == null){
            return;
        }

        Packet packet = new Packet();
        packet.addToPayLoad(e.ID);
        networkHandler.getRemote("SetOwner").fireClient(p.getClient(), packet);
    }

    /**
     * Adds a tile to the game map.
     * @param tile The character representation of the tile.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
    protected void addTile(char tile,Short x,Short y){
        if (networkHandler == null){
            return;
        }

        Packet packet = new Packet();
        packet.addToPayLoad(new TileData(tile, x, y));
        networkHandler.getRemote("AddTile").fireAllClients(packet);
    }

    /**
     * Removes a tile from the game map.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
    protected void removeTile(Short x,Short y){
        if (networkHandler == null){
            return;
        }
        Packet packet = new Packet();
        packet.addToPayLoad(new TileData('X', x, y));
        networkHandler.getRemote("RemoveTile").fireAllClients(packet);
    }

    /**
     * Sends the game map data to all clients.
     * @param map The game map data.
     */
    protected void sendMapData(char[][] map){
        if (networkHandler == null){
            return;
        }
        Packet packet = new Packet();
        packet.addToPayLoad(map);
        networkHandler.getRemote("ReplicateMap").fireAllClients(packet);
    }

    /**
     * Sends the entity component data to all clients or the server.
     * @param map The entity component data.
     */
    protected void sendEntityData(EntityHandler.ComponentMap map){
        if (networkHandler == null){
            return;
        }
        Packet packet = new Packet();
        packet.addToPayLoad(map);
       if (isHost){
            networkHandler.getRemote("EntityComponents").fireAllClients(packet);
       }else{
            networkHandler.getRemote("EntityComponents").fireServer(packet);
       }
    }   

    /**
     * Sends an entity to all clients.
     * @param entity The entity to send.
     */
    protected void sendEntity(Entity  entity){
        if (networkHandler == null){
            return;
        }
        Packet packet = new Packet();
        ComponentData data = entity.getReplicationData();
        packet.addToPayLoad(data);
        networkHandler.getRemote("EntityAdded").fireAllClients(packet);
    }

    /**
     * Removes an entity from all clients.
     * @param e The entity to remove.
     */
    protected void removeEntity(Entity e){
        if (networkHandler == null){
            return;
        }
        Packet packet = new Packet();
        packet.addToPayLoad(e.ID);
        networkHandler.getRemote("EntityRemoved").fireAllClients(packet);
    }

    /**
     * Destroys the replicator object.
     */
    protected void destroy(){
        connectionContainer.disconnectAll();
    }
}
