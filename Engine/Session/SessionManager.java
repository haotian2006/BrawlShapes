package Engine.Session;

import java.io.IOException;

import java.util.ArrayList;

import Engine.Engine;
import Engine.Entities.Entity;
import Networking.*;
import validator.InetAddressValidator;
import Signal.*;

/**
 * The SessionManager class manages the session for the game. It handles player management, network communication, and session events.
 * @author haotian
 */
public class SessionManager {

    private static final int MAX_PLAYERS = 8;

    public final NetworkHandler networkHandler;
    public final boolean isHost;
    public final boolean isOffline;

    public SessionPlayer localPlayer;
    
    public final ArrayList<SessionPlayer> players = new ArrayList<SessionPlayer>();


    protected Signal <SessionPlayer> changedSignal = new Signal<SessionPlayer>();
    public final Event <SessionPlayer> changed = changedSignal.event;

    
    private Signal <SessionPlayer> playerAddedSignal = new Signal<SessionPlayer>();
    public final Event <SessionPlayer> playerAdded = playerAddedSignal.event;

    private Signal <SessionPlayer> playerRemovedSignal = new Signal<SessionPlayer>();
    public final Event <SessionPlayer> playerRemoved = playerRemovedSignal.event;

    private Signal <Engine> startingGameSignal = new Signal<Engine>();
    public final Event <Engine> startingGame = startingGameSignal.event;

    private Signal <Engine> endingGameSignal = new Signal<Engine>();
    public final Event <Engine> endgame = endingGameSignal.event;

    private ConnectionContainer connectionContainer = new ConnectionContainer();

    public Engine engine;
    

    public SessionManager(boolean isHost){ 
        networkHandler = new NetworkHandler(new String[]{
            "EntityComponents","EntityAdded","EntityRemoved",//ENTITIES
            "AddSessionPlayer","RemoveSessionPlayer", "ReplicateAllSessionPlayers","ChangeBrawler",//SESSION
            "StartGame","EndGame",
            "ReplicateMap", "AddTile","RemoveTile",//TILES
            "SetOwner", "UseSuper","Fire"
        });

        Remote AddSessionPlayer = networkHandler.getRemote("AddSessionPlayer");
        Remote RemoveSessionPlayer = networkHandler.getRemote("RemoveSessionPlayer");
        Remote ReplicateAllSessionPlayers = networkHandler.getRemote("ReplicateAllSessionPlayers");

        this.isHost = isHost;
        isOffline = false;
        SessionManager manger = this;
        if (isHost){
            networkHandler.ClientAdded.connect(User -> {
                if (User.isLocal)
                    return;
                if (players.size() >= MAX_PLAYERS){
                    networkHandler.removeClient(User);
                    return;
                }

                SessionPlayer Player = new SessionPlayer(manger,User);
                Player.name = User.getName();
                addPlayer(Player);
                ArrayList<byte[]> data = new ArrayList<byte[]>();
                for (SessionPlayer p : players){
                    data.add(p.getData());
                }

                Packet p = new Packet();
                p.addToPayLoad(data);
                
                ReplicateAllSessionPlayers.fireClient(User , p);
       

                
            },connectionContainer);

            networkHandler.ClientRemoved.connect(User -> {
                SessionPlayer Player = getPlayer(User.getId());
                if (Player != null){
                    removePlayer(Player);
                }
            },connectionContainer);

            networkHandler.getRemote("ChangeBrawler").onMessage.connect(packet -> {
                int data = (int)packet.getPayLoad();
                SessionPlayer player = (SessionPlayer)getPlayer(packet.getClientID());
                player.setBrawler(data);
            }, connectionContainer);
        
        }else{
            networkHandler.getRemote("ChangeBrawler").onMessage.connect(packet -> {
                String[] data = (String[])packet.getPayLoad();
                int brawler = Integer.parseInt(data[0]);
                String clientName =  (String)(data[1]);
                //System.out.println("Change Brawler");
                SessionPlayer player = (SessionPlayer)getPlayer(clientName);

                player.setBrawler(brawler);
            }, connectionContainer);
            AddSessionPlayer.onMessage.connect(Packet -> {
                byte[] data = (byte[])Packet.getPayLoad();
                SessionPlayer Player = new SessionPlayer(manger,data);
                addPlayer(Player);
            },connectionContainer);
            RemoveSessionPlayer.onMessage.connect(data -> {
                SessionPlayer Player = getPlayer((String)data.getPayLoad());
                removePlayer(Player);
            },connectionContainer);
 
            ReplicateAllSessionPlayers.onMessage.connect(Packet -> {
             //   System.out.println("ReplicateAllSessionPlayers");
                ArrayList<byte[]> data = (ArrayList)Packet.getPayLoad();
                ArrayList<SessionPlayer> playersTemp = (ArrayList<SessionPlayer>)players.clone();
                players.clear();
                for (byte[] d : data){
                    SessionPlayer Player = new SessionPlayer(manger,d);
                    addPlayer(Player);
                }

                for (SessionPlayer p : playersTemp){
                   addPlayer(p);
                }


            },connectionContainer);

            networkHandler.getRemote("StartGame").onMessage.connect(packet -> {
                startGame();
            }, connectionContainer);
            networkHandler.getRemote("EndGame").onMessage.connect(packet -> {
                if (engine != null){
                    engine.endGame((String)packet.getPayLoad());
                }
            }, connectionContainer);
        }

    }

    public void endGame(){
        endingGameSignal.fire(engine);
        //engine = null;
    } 

    public SessionPlayer getPlayer(Short id){
        for (SessionPlayer p : players){
            if (p.clientID != null &&  p.clientID.shortValue() == id){
                return p;
            }
        }
        return null;
    }

    public SessionPlayer getPlayer(String name){
        for (SessionPlayer p : players){
            if (p.name.equals(name)){
                return p;
            }
        }
        return null;
    }
    
    public boolean addPlayer(SessionPlayer p){
        if ( players.size() >= MAX_PLAYERS ){
            return false;
        }

        if (p.clientID != null && getPlayer(p.clientID) != null){
            return false;
        }

        players.add(p);
        if (!isOffline  && !p.isBot && p.clientID.equals(networkHandler.getLocal().getId())){
            localPlayer = p;
        }
        playerAddedSignal.fire(p);

        if (!isHost || isOffline){
            return true;
        }


        if (MAX_PLAYERS == players.size() && networkHandler != null){

            networkHandler.allowJoin(false);
        
        }
        Packet packet = new Packet();
        packet.addToPayLoad(p.getData());
        networkHandler.getRemote("AddSessionPlayer").fireAllClients(packet);
        return true;
    }

    int id = 0;

    public SessionPlayer addBot(){
        SessionPlayer p = new SessionPlayer(this,true);
        p.name = "Bot" + (++id);
        addPlayer(p);
        return p;
    }



    public void removePlayer(SessionPlayer p){
        players.remove(p);
        playerRemovedSignal.fire(p);
        if (players.size() <  MAX_PLAYERS && networkHandler != null && isHost){
            networkHandler.allowJoin( true);
        }
        if (!isOffline && isHost){
            Packet packet = new Packet();
            packet.addToPayLoad(p.name);
            networkHandler.getRemote("RemoveSessionPlayer").fireAllClients(packet);
        }
        if (isHost && p.getClient() != null){
            networkHandler.removeClient(p.getClient());
        }

    }

    public SessionManager(){
        isOffline = true;
        isHost = true;
        networkHandler = null;
        localPlayer = new SessionPlayer(this);
        localPlayer.name = "Local Player";
        players.add(localPlayer);
    }

    public void changeBrawler(SessionPlayer p, int brawler){
        if (isOffline || p == null){
            return;
        }
        Packet packet = new Packet();
        if (!isHost && p.isLocal()){
            packet.addToPayLoad(brawler);
            networkHandler.getRemote("ChangeBrawler").fireServer(packet);
            return;
        }
        if (!isHost){
            return;
        }
        packet.addToPayLoad(new String[]{brawler+"",p.name});
        networkHandler.getRemote("ChangeBrawler").fireAllClients(packet);
    }

    public void startNetwork() throws IOException {
        if (isOffline){
            return;
        }
        if (isHost){
            networkHandler.StartServer();
            SessionPlayer host = new SessionPlayer(this,networkHandler.getLocal());
            host.name = "Host";
            localPlayer = host;
            addPlayer(host);
        } else {
            networkHandler.StartClient();
        }
    }

    public void stopNetwork(){
        if (networkHandler != null){
            networkHandler.Stop();
        }
    }

    public boolean setPort(int port){
        // if (port < 0 || port > 65535) {
        //     return false;
        // }
        networkHandler.setPort(port);
        return true;
    }

    public int getPort(){
        return networkHandler.getPort();
    }

    public String getIpAddress(){
        return networkHandler.getTargetIpAddress();
    }

    public boolean setTargetIpAddress(String ipAddress){
        // if (ipAddress == null || InetAddressValidator.getInstance().isValid(ipAddress) == false){
        //     return false;
        // }
        networkHandler.setTargetIpAddress(ipAddress);
        return true;
    }

    public void destroy(){
        stopNetwork();
        changedSignal.disconnectAll();
        playerAddedSignal.disconnectAll();
        playerRemovedSignal.disconnectAll();
    }

    public Engine startGame(){
        if (engine != null){
            engine.destroy();
        }
        engine = new Engine(this);
        startingGameSignal.fire(engine);
        if (isHost && !isOffline){
            networkHandler.allowJoin(false);
            Packet packet = new Packet();
            packet.addToPayLoad("StartGame");
            networkHandler.getRemote("StartGame").fireAllClients(packet);
        }
        engine.startGame();
        return engine;
    }

    public static void main(String[] args) throws InterruptedException {
        ArrayList players;
       {
        SessionManager manger = new SessionManager(true);

        try {
            manger.startNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }

        players = manger.players;
        System.out.println(manger.players.toString());
       }

        
       {
        SessionManager manger = new SessionManager(false);

        try {
            manger.startNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);
        System.out.println(manger.players.toString());
        System.out.println(players.toString()+" SEVER");
        Thread.sleep(300000);
    
       }



        
    }
}
