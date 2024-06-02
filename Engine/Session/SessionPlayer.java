package Engine.Session;

import Networking.NetworkUser;
import java.util.*;
import java.io.*;
import java.io.*;
import java.util.HashMap;

/**
 * The SessionPlayer class represents a player in a session.
 * @author haotian
 */
public class SessionPlayer {

    /**
     * A map that stores the brawler images.
     */
    public static final HashMap<String, String> brawlerImages = new HashMap<String, String>(){{ 
        
    }};

    /**
     * A Files that stores the button.
     */
    public Object temp;
    /**
     * The session manager.
     */
    public final SessionManager manager;
    /**
     * Indicates if the player is a bot.
     */
    public final boolean isBot;
    /**
     * The client ID of the player.
     */
    public final Short clientID;
    /**
     * The name of the player.
     */
    public String name = "Host";
    /**
     * The selected brawler of the player.
     */
    public int selectedBrawler = 0;

    /**
     * Constructs a SessionPlayer object for a bot player.
     * @param s The session manager.
     * @param isBot Indicates if the player is a bot.
     */
    public SessionPlayer(SessionManager s, boolean isBot) {
        this.isBot = isBot;
        clientID = null;
        manager = s;
    }   

    /**
     * Constructs a SessionPlayer object for a network user client.
     * @param s The session manager.
     * @param client The network user client.
     */
    public SessionPlayer(SessionManager s, NetworkUser client) {
        this.isBot = false;
        this.clientID = client.getId();
        manager = s;
    }

    /**
     * Constructs a SessionPlayer object from serialized data.
     * @param s The session manager.
     * @param data The serialized data.
     */
    public SessionPlayer(SessionManager s, byte[] data) {
        this.manager = s;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            this.name = ois.readUTF();
            this.selectedBrawler = ois.readInt();
            short clientId = ois.readShort();
            this.isBot = ois.readBoolean();
            this.clientID = clientId == Short.MAX_VALUE ? null : clientId;
            ois.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize SessionPlayer", e);
        }
    }

    /**
     * Constructs a SessionPlayer object for a local player.
     * @param s The session manager.
     */
    public SessionPlayer(SessionManager s) {
        this.isBot = false;
        this.clientID = null;
        manager = s;
    }

    /**
     * Checks if the player is local.
     * @return true if the player is local, false otherwise.
     */
    public boolean isLocal() {
        if (manager.networkHandler == null && !isBot) {
            return true;
        }
        if (clientID != null) {
            NetworkUser user = manager.networkHandler.getClient(clientID);
            if (user != null && user.isLocal) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the network user client associated with the player.
     * @return The network user client, or null if not available.
     */
    public NetworkUser getClient() {
        if (clientID != null) {
            return manager.networkHandler.getClient(clientID);
        }
        return null;
    }

    /**
     * Sets the selected brawler for the player.
     * @param brawler The name of the selected brawler.
     */
    public void setBrawler(int brawler) {   
        if (selectedBrawler == (brawler)) return;
        selectedBrawler = brawler;
        manager.changedSignal.fire(this);
        if (isLocal() || manager.isHost ) {
            manager.changeBrawler(this, brawler);
        }
    }

    /**
     * Serializes the SessionPlayer object to a byte array.
     * @return The serialized data as a byte array.
     */
    public byte[] getData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeUTF(name);
            oos.writeInt(selectedBrawler);
            oos.writeShort(clientID == null ? Short.MAX_VALUE : clientID);
            oos.writeBoolean(isBot);
            oos.flush();
            data = baos.toByteArray();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Returns the name of the player.
     * @return The name of the player.
     */
    public String toString() {
        return name;
    }
}
