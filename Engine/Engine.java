package Engine;

import Engine.Collisions.CollisionHandler;
import Engine.Entities.*;
import Engine.Entities.Brawlers.*;
import Engine.EntityComponents.Components.Ammo;
import Engine.EntityComponents.Components.SuperValue;
import Engine.Enums.CameraMode;
import Engine.Session.*;
import InputManager.InputManager;
import Log.LogManager;
import MathLib.Vector2;
import Networking.NetworkHandler;
import Networking.Packet;
import Resources.ResourceManager;

import javax.swing.*;

import MathLib.Vector2;
import SimpleUI.UiTween;
import SimpleUI.Wrappers.ImageLabel;
import SimpleUI.Wrappers.TextLabel;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import Signal.*;
import Signal.Event;

import java.util.*;
import Engine.Controllers.*;

/**
 * The main engine class that handles the game logic and rendering.
 * @author haotian
 * @see JPanel
 */
public class Engine extends JPanel {
    //CONFIG
    public static final boolean DEBUGGING = false;
    public static final int GRID_SCALE = 42;
    public static final int FPS = 60;
    
    public boolean allowInputs = false;

    public final boolean isHost;
    public final Camera camera = new Camera(this);
    public Vector2 scaledCameraLoc = camera.getPosition();
    public boolean spectating = false;
    private final int NeededLoading = 130;
    public int Loading = NeededLoading;

    private JTextPane fpsLabel;
    private double phyTime = 0;
    private double renderTime = 0;
    

    protected UUID primaryEntity = null;


    //HANDLERS
    /**
     * The tile handler for the game.
     *
     * @see TileHandler
     *
     */
    public final TileHandler tileHandler;
    public final TaskScheduler taskScheduler;;
    public final CollisionHandler collisionHandler;
    public final EntityHandler entityHandler;
    public final Replicator replicator;
    public final SessionManager session;
    public final LogManager logger;
    public final InputManager inputManager;

    /**
     * Constructs an Engine object with the given session manager.
     * @param session The session manager for the game.
     */
    public Engine(SessionManager session){
        this.session = session;
        isHost = session.isHost;
        if (isHost){
            logger = new LogManager(false);
        }else{
            logger = new LogManager(false);
        }


        fpsLabel = new JTextPane();
        
        fpsLabel.setOpaque(false);
        fpsLabel.setLocation(0, 0);
        
        taskScheduler = new TaskScheduler(this,FPS);

        setSize( 800, 600);
        setBackground(new Color(252,193,100));

       
        inputManager = new InputManager(this);


        entityHandler = new EntityHandler(this);

        
        tileHandler = new TileHandler(this,100, 100);

        collisionHandler = new CollisionHandler(this);

        replicator = new Replicator(this);

        entityHandler.EntityRemoved.connect(entity -> {
            if (entity == null){
                return;
            }
            if (entity == camera.getEntity()){
                camera.nextBrawler();
            }
            if (entity.ID.equals(primaryEntity)){
                primaryEntity = null;
                camera.Mode = CameraMode.Spectating;
                camera.nextBrawler();
            }
            if (entityHandler.numOfBrawlers() <=1){
                try{
                      Brawler winner = (Brawler)entityHandler.getAllBrawlers().get(0);
                    endGame(winner);
                }catch (Exception e){
                    endGame("No One Wins");
                }

            }
        });

        inputManager.InputBegan.connect(input -> {
            if (input.getKeyCode() == KeyEvent.VK_Q){
                camera.lastBrawler();
            }else if (input.getKeyCode() == KeyEvent.VK_E){
                camera.nextBrawler();
            }
        });

        if (DEBUGGING)
            add(fpsLabel);
        

    }

    /**
     * Calculates the center position of the engine panel.
     * @return The center position as a Vector2 object.
     */
    public Vector2 calculateCenter (){
        int x = (getWidth()) / 2;
        int y = (getHeight()) / 2;

        return new Vector2(x, y);
    }

    /**
     * Called when the engine needs to render the game.
     * @param dt The time since the last frame in seconds.
     */
    protected void onRender(double dt)  {

        fpsLabel.setText(String.format("Render TIME: %.3f (ms)  \nPhysics TIME: %.3f (ms)", renderTime, phyTime));

        repaint();

        setFocusable(true);
        requestFocusInWindow();
        
    }

    /**
     * Called when the engine needs to update the game logic.
     * @param dt The time since the last frame in seconds.
     */
    protected void onUpdate(double dt) {
        if (Loading < 110){
            return;
        }
        long start = System.nanoTime();

       try {
        entityHandler.update(dt);
        scaledCameraLoc = camera.getPosition().mul(GRID_SCALE);
       } catch (Exception e) {
           e.printStackTrace();
       }
       phyTime = (System.nanoTime() - start) / 1000000.0;

    }

    /**
     * Sets the owner of a brawler entity.
     * @param p The session player who owns the brawler.
     * @param e The brawler entity.
     */
    public void setOwner(SessionPlayer p, UUID id){  
        Brawler e = (Brawler)entityHandler.get(id);
        if (e == null){
            Connection[] x = new Connection[1];
            x[0] =  entityHandler.EntityAdded.connect(entity -> {
                if (entity.ID.equals(id)){
                    setOwner(p, id);
                    x[0].disconnect();
                }
            });
            return;
        }  
        e.setOwner(p);
       if (p.isLocal()){
            setEntity(e.ID);
        }else if(isHost && !p.isBot){
            replicator.setOwnerForEntity(e, p);
        }else{
            e.setController(new AIController(e));
        }

    }

    public void setOwner(SessionPlayer p, Entity e){
        setOwner(p, e.ID);
    }

    /**
     * Sets the primary entity for the camera to follow.
     * @param id The ID of the primary entity.
     */
    public void setEntity(UUID id){
        primaryEntity = id;
        Brawler e = (Brawler)entityHandler.get(id);
        camera.setEntity(e);
        e.setController(new PlayerController(e));
    }

    Class<?>[] brawlerClasses = {Circle.class, Pentagon.class, Square.class, Triangle.class, Kite.class};
    Vector2[] spawnLocations = {new Vector2(2,2), new Vector2(17,8), new Vector2(6,17), new Vector2(2,18), new Vector2(17,18),new Vector2(2,2)};
    /**
     * Loads data from the session manager into the game engine.
     * Loads map and brawlers
     */
    public void startGame(){
        Loading = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Vector2> spawns = new ArrayList<>(Arrays.asList(spawnLocations));
                    Collections.shuffle(spawns);
                    HashMap<SessionPlayer, Brawler> brawlersT = new HashMap<>();
                for (int i = 0; i < NeededLoading; i++) {
                    if (isHost && i == 75){
                        int idx = 0;
            
                        for (SessionPlayer p : session.players) {
    
                            try {
                                Class<?> c = brawlerClasses[p.selectedBrawler];
                                Entity b = (Entity) c.getConstructor(Engine.class).newInstance(Engine.this);
                                b.Name = p.name;
                                b.setCenter(spawns.get(idx++));
                                entityHandler.add(b);
                                brawlersT .put(p, (Brawler)b);


                            } catch (Exception e) {
                                System.out.println("Failed to create brawler " + p.selectedBrawler +" | "+ brawlerClasses[p.selectedBrawler].getName() + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }else if (isHost && i == 65){
                        tileHandler.loadMap(new Map(ResourceManager.getResource("Maps/BaseMap.txt")));
                        spawns = (ArrayList<Vector2>)tileHandler.spawnLocations.clone();
                        Collections.shuffle(spawns);
                    }else if (isHost && i == 90){
                        for (HashMap.Entry<SessionPlayer, Brawler> entry : brawlersT.entrySet()) {
                            SessionPlayer p = entry.getKey();
                            Brawler b = entry.getValue();
                            setOwner(p, b.ID);
                        }
                    }
                        Thread.sleep(60);
                    Loading++;
                }
                } catch (Exception e) {
                    e.printStackTrace();
            }
        }
        }).start();
    }

    String endGameString = null;

    public void endGame(Brawler winner){
        if (isHost){
            endGame(winner.Name + " Wins!");
        }
    }

    boolean gameOver = false;
    public void endGame(String txt){
        if (gameOver)
            return;
        gameOver = true;
        endGameString = txt;
        if (isHost && session.networkHandler != null){
            Packet packet = new Packet();
            packet.addToPayLoad(txt);
            session.networkHandler.getRemote("EndGame").fireAllClients(packet);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    if (isHost){
    
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                session.endGame();
                destroy();
            }
        });
        t.start();

    }

    /**
     * Cleans up the engine and prepares for destruction.
     */
    public void destroy(){
        taskScheduler.stop();
        entityHandler.destroy();
        replicator.destroy();
    }

    /**
     * Gets the primary entity.
     * @return The primary entity.
     */
    public Entity getPrimary(){
        return entityHandler.get(primaryEntity);
    }

    /**
     * Paints the game components on the panel.
     * @param g The graphics object to paint on.
     */
    public void paintComponent(Graphics g){

        long start = System.nanoTime();
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;


        if (Loading < NeededLoading-20){
            drawLoadingScreen(g2);
            g2.dispose();
            return;
        }
        

        Vector2 center = calculateCenter();
        AffineTransform originalTransform = g2.getTransform();

        if (  tileHandler != null) {
            tileHandler.draw(g2,center);
        }

        g2.setTransform(originalTransform);
      

        if (  entityHandler != null) {
            entityHandler.draw(g2,center);
        }

        if (camera.Mode == CameraMode.Spectating) {
            displaySpectating(g);
        }
        renderTime = (System.nanoTime() - start) / 1000000.0;
        
        
        if (endGameString != null){
            
            g2.setColor(Color.BLACK);

  
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 40));

            FontMetrics metrics = g2.getFontMetrics();
            int width = metrics.stringWidth(endGameString);
            int height = metrics.getHeight();
            int x = (getWidth()+10) / 2-width/2;
            int y = (getHeight() - height) / 2;
            g2.drawString(endGameString, x, y-80);

   


        }
        drawInfo(g2);
        fpsLabel.paint(g);
        g2.dispose();

    }

    /**
     * Displays the spectating information on the screen.
     * @param g The graphics object to paint on.
     */
    public void displaySpectating(Graphics g) {
        // Obtain the entity's name
        Entity e = camera.getEntity();
        String name = e == null ? "" : e.Name;
        
        String spectatingText = "You Are Spectating";
        String entitySwitchText = "Press Q for previous entity | Press E for next entity";
        String currentEntityText = "Current Entity: " + name + "";
    
        Font spectatingFont = new Font("Arial", Font.BOLD, 24); 
        Font entityFont = new Font("Arial", Font.BOLD, 17); 
        
        g.setFont(spectatingFont);
        FontMetrics spectatingMetrics = g.getFontMetrics();
        int spectatingWidth = spectatingMetrics.stringWidth(spectatingText);
        int spectatingHeight = spectatingMetrics.getHeight();
        
        g.setFont(entityFont);
        FontMetrics entitySwitchMetrics = g.getFontMetrics();
        int entitySwitchWidth = entitySwitchMetrics.stringWidth(entitySwitchText);
        int entitySwitchHeight = entitySwitchMetrics.getHeight();
        
        FontMetrics currentEntityMetrics = g.getFontMetrics();
        int currentEntityWidth = currentEntityMetrics.stringWidth(currentEntityText);
        int currentEntityHeight = currentEntityMetrics.getHeight();
        
        int totalHeight = spectatingHeight + entitySwitchHeight + currentEntityHeight;
        
        int centerX = getWidth() / 2;
        
        int bottomMargin = 50; 
        int startY = getHeight() - bottomMargin - totalHeight;
    

        int spectatingY = startY + spectatingMetrics.getAscent();
        int entitySwitchY = spectatingY + spectatingHeight;
        int currentEntityY = entitySwitchY + entitySwitchHeight;
    
        g.setFont(spectatingFont);
        g.setColor(Color.RED);
        g.drawString(spectatingText, centerX - spectatingWidth / 2, spectatingY);
        
        g.setFont(entityFont);
        g.setColor(Color.BLACK);
        g.drawString(entitySwitchText, centerX - entitySwitchWidth / 2, entitySwitchY);
        
        g.setColor(Color.BLACK);
        //g.drawString(currentEntityText, centerX - currentEntityWidth / 2, currentEntityY);
        
    }

    int barThingyIDK = 0;

    /**
     * Draws the loading screen on the panel.
     * @param g The graphics object to paint on.
     */
    public void drawLoadingScreen(Graphics2D g){
    
        BufferedImage img = ResourceManager.getImage("Images/Lobby/FrontScreen.png", 800, 600);
        g.drawImage(img, 0, 0, null);
        int barWidth = 400; 
        int barHeight = 20;
        int barX = (getWidth() - barWidth) / 2;
        int barY = getHeight() - 120;
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3)); 
        g.drawRect(barX, barY, barWidth, barHeight);
        
        int progressWidth = (int) (barWidth * (Math.min(Loading, 100) / 100.0));
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY+1, progressWidth, barHeight-1);

    
        String loadingText = "Loading Map" + new String(new char[barThingyIDK/24]).replace("\0", ".");
        if (Loading > 100){
            loadingText = "Finished Loading!";
        }
        Font loadingFont = new Font("Arial", Font.BOLD, 20);
        g.setFont(loadingFont);
        FontMetrics loadingMetrics = g.getFontMetrics();
        int loadingWidth = loadingMetrics.stringWidth(loadingText);
        int loadingHeight = loadingMetrics.getHeight();
        int loadingX = (getWidth() - loadingWidth) / 2;
        int loadingY = barY - loadingHeight ;
        g.setColor(Color.BLACK);
        g.drawString(loadingText, loadingX, loadingY);
        barThingyIDK++;
        barThingyIDK%=64;
    }

    public void drawInfo(Graphics2D g){
        Brawler spectating = (Brawler)camera.getEntity();
        if (spectating == null){
            return;
        }

        int time = 0;
        int numBrawlersAlive = entityHandler.numOfBrawlers();
        String info = "Players Alive: " + numBrawlersAlive;

        SuperValue superComponent = spectating.getComponent(SuperValue.class);
        Ammo ammoComponent = spectating.getComponent(Ammo.class);

        float superPercentage = superComponent.superAmt / (float)superComponent.totalNeeded;
        float ammoPercentage = ammoComponent.current / (float)ammoComponent.max;

        int barWidth = 200;
        int barHeight = 40;
        int barSpacing = 10;
        int barX = (getWidth() - (2 * barWidth + barSpacing)) / 2;
        int barY = getHeight() - 60;
        
        g.setColor(Color.YELLOW);
        g.fillRect(barX, barY, (int)(barWidth * ammoPercentage), barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);

        g.setColor(Color.CYAN);
        g.fillRect(barX + barWidth + barSpacing, barY, (int)(barWidth * superPercentage), barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(barX + barWidth + barSpacing, barY, barWidth, barHeight);

        // Draw text on top of the bars
        String ammoText = "Ammo: " + (int)(ammoPercentage * 100) + "%";
        String superText = "Super: " + (int)(superPercentage * 100) + "%";

        if (spectating.canUseSuper()) {
            superText = "Super Ready! Press [X] to use super!";
        }

        Font textFont = new Font("Arial", Font.BOLD, 16);
        FontMetrics textMetrics = g.getFontMetrics(textFont);

        int ammoTextWidth = textMetrics.stringWidth(ammoText);
        int ammoTextHeight = textMetrics.getHeight();
        int ammoTextX = barX + (barWidth - ammoTextWidth) / 2;
        int ammoTextY = barY + (barHeight + ammoTextHeight) / 2;

        int superTextWidth = textMetrics.stringWidth(superText);
        int superTextHeight = textMetrics.getHeight();
        int superTextX = barX + barWidth + barSpacing + (barWidth - superTextWidth) / 2;
        int superTextY = barY + (barHeight + superTextHeight) / 2;

        g.setFont(textFont);
        g.setColor(Color.BLACK);
        g.drawString(ammoText, ammoTextX, ammoTextY);
        g.drawString(superText, superTextX, superTextY);
        
        //draw Bars and times stuff
        // Draw text on top right corner
        Font infoFont = new Font("Arial", Font.BOLD, 16);
        FontMetrics infoMetrics = g.getFontMetrics(infoFont);

        int infoX = getWidth() - 150; // Adjust the X position as needed
        int infoY = 20; // Adjust the Y position as needed

        g.setFont(infoFont);
        g.setColor(Color.BLACK);
        g.drawString(info, infoX, infoY);
    }
}
 