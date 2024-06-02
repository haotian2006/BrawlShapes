package Engine;

import Engine.Entities.Entity;
import Engine.EntityComponents.AbstractComponent;
import Engine.Tile.*;
import MathLib.AABB;
import MathLib.Vector2;
import Resources.ResourceManager;

import javax.imageio.*; 

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.PublicKey;
import java.util.*;

class VoidTile extends Tile{
    public VoidTile() {
        super(true);
    }

    public VoidTile(Vector2 Position) {
        super(Position,true);
    }
    
}

class EmptyTile extends Tile{
    public EmptyTile() {
        super(false);
    }
}

class TileMap{
    public static final char EMPTY_TILE = 'X';
    public final  HashMap<Character,Class <? extends Tile>> CharTilePair = new HashMap<Character,Class <? extends Tile>> ();
    public final  HashMap<String,Character> TileCharPair = new HashMap<String,Character> (); 

    public void put(Character key,Class <? extends Tile> value){
        CharTilePair.put(key, value);
        TileCharPair.put(value.getName(), key);
    }
    
    public char getChar(Tile t){
        return TileCharPair.get(t.getClass().getName());
    }

    public Class <? extends Tile> getTileClass(char c){
        if (c == EMPTY_TILE) return  null;
        return CharTilePair.get(c);
    }


    
}

/**
 * The TileHandler class is responsible for managing the tiles in the game.
 * It provides methods to load and manipulate the tiles on the game map.
 * @author haotian
 */

public class TileHandler {

    /**
     * The scale of the grid used in the engine.
     */
    public static final int Grid_Scale = Engine.GRID_SCALE;

    /**
     * Represents a map of tiles in the game.
     */
    public static final TileMap tileMap = new TileMap() {
        {
            put('A', Tile.class);
            put('B', PowerBoxTile.class);
            put('C', FloorTile.class);
            put('D', WallTile.class);
            put('W', WaterTile.class);
            put('G', GrassTile.class);
            put('Z', DebugTile.class);
            put('S', FloorTile.class);
        }
    };

    /**
     * The default tile used in the game.
     */
    private Tile DefaultTile = new FloorTile();
    
    private Engine engine;

    public ArrayList<Vector2> spawnLocations = new ArrayList<Vector2>();

    protected Tile[][] tiles;
    private int SizeX;
    private int SizeY;

    private boolean IsMapCreator = false;

    public final PathFinder pathFinder;

    /**
     * The TileHandler class represents a handler for managing tiles in the game engine.
     * It provides methods for creating and manipulating tiles within the game world.
     *
     * @param engine The engine instance.
     * @param SizeX  The width of the tile handler.
     * @param SizeY  The height of the tile handler.
     */
    protected TileHandler(Engine engine, int SizeX, int SizeY) {
        tiles = new Tile[SizeX][SizeY];
        pathFinder = new PathFinder(this);
        this.SizeX = SizeX;
        this.SizeY = SizeY;
        this.engine = engine;
    }

    /**
     * Loads a map into the TileHandler.
     * If the engine is the host, it sends the map data to the replicator.
     * Sets the SizeX and SizeY variables based on the map's width and height.
     * Initializes the tiles array with the specified size.
     * Iterates through each position in the map and adds a tile if the corresponding character has a valid tile class.
     *
     * @param map The map to be loaded.
     */
    public void loadMap(Map map) {
        if (engine.isHost) {
            engine.replicator.sendMapData(map.charMap);
        }
        this.SizeX = map.width();
        this.SizeY = map.height();
        tiles = new Tile[SizeX][SizeY];

        for (int y = 0; y < SizeY; y++) {
            for (int x = 0; x < SizeX; x++) {
                char c = map.getChar(x, y);
                if (tileMap.getTileClass(c) != null) {
                    addTile(x, y, c);
                }
            }
        }
    }

    /**
     * Returns the default tile.
     *
     * @return the default tile
     */
    public Tile getDefaultTile() {
        return DefaultTile;
    }

    /**
     * Returns the map as a string representation.
     *
     * @return the map as a string
     */
    public String getMapAsString() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < SizeY; y++) {
            for (int x = 0; x < SizeX; x++) {
                if (tiles[x][y] == null) {
                    sb.append(TileMap.EMPTY_TILE);
                } else {
                    sb.append(tileMap.getChar(tiles[x][y]));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Sets the tile at the specified coordinates to the given tile.
     * If the engine is the host, it also adds the tile to the replicator.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param t The tile to set.
     */
    public void setTile(int x, int y, Tile t) {
        if (engine.isHost) {
            engine.replicator.addTile(tileMap.getChar(t), (short) x, (short) y);
        }
        tiles[x][y] = t;
        t.setPosition(new Vector2(x, y));
        t.setEngine(engine);
        t.onAdd();
    }

    /**
     * Returns the size of the tile handler.
     *
     * @return The size of the tile handler as a Vector2.
     */
    public Vector2 getSize() {
        return new Vector2(SizeX, SizeY);
    }

    /**
     * Adds a tile at the specified coordinates based on the given character.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param t The character representing the tile.
     */
    public void addTile(int x, int y, char t) {
        if (t == TileMap.EMPTY_TILE) {
            return;
        }
        Tile tile;
        if (t == 'S') {
            spawnLocations.add(new Vector2(x, y).add(new Vector2(.5f, .5f)));
            return;
        }
        try {
            tile = tileMap.getTileClass(t).getDeclaredConstructor().newInstance();
            tiles[x][y] = tile;
            tile.setPosition(new Vector2(x, y));
            tile.setEngine(engine);
            tile.onAdd();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Removes the tile at the specified coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The removed tile, or null if there was no tile at the specified coordinates.
     */
    public Tile removeTile(int x, int y) {
        Tile t = tiles[x][y];
        if (t == null || t.getClass() == VoidTile.class) {
            return null;
        }
        t.onDestroy();
        tiles[x][y] = null;
        if (engine.isHost) {
            engine.replicator.removeTile((short) x, (short) y);
        }
        return t;
    }

    /**
     * Returns the tile at the specified coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The tile at the specified coordinates.
     */
    public Tile getTile(int x, int y) {
        if (x < 1 || x >= SizeX - 1 || y < 1 || y >= SizeY - 1) {
            return new VoidTile(new Vector2(x, y));
        }
        return tiles[x][y];
    }

    /**
     * Updates the tile handler.
     *
     * @param dt The time elapsed since the last update.
     */
    protected void update(double dt) {

    }

    /**
     * Draws the tiles on the graphics context.
     *
     * @param g      The graphics context to draw on.
     * @param center The center position of the drawing area.
     */
    protected void draw(Graphics2D g, Vector2 center) {
        Vector2 cameraPos = engine.scaledCameraLoc;
        int offset = Grid_Scale / 2;

        int width = (int) engine.getSize().getWidth();
        int height = (int) engine.getSize().getHeight();

        // These params clamps the rendering to what the user can see
        int startX = Math.max(0, (int) (cameraPos.X - width / 2) / Grid_Scale - offset);
        int startY = Math.max(0, (int) (cameraPos.Y - height / 2) / Grid_Scale - offset);
        int endX = Math.min(tiles.length, (int) (cameraPos.X + width / 2) / Grid_Scale + offset);
        int endY = Math.min(tiles[0].length, (int) (cameraPos.Y + height / 2) / Grid_Scale + offset);

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                Tile at = tiles[x][y];
                Vector2 centerPos = new Vector2(x + .5f, y + .5f).mul(Grid_Scale).relativeTo(cameraPos).add(center);
                if (at == null) {
                    g.drawImage(DefaultTile.Image, (int) centerPos.X, (int) centerPos.Y, null);
                    continue;
                }

                if (at.IsTransparent) {
                    g.drawImage(DefaultTile.Image, (int) centerPos.X, (int) centerPos.Y, null);
                }

                g.drawImage(at.Image, (int) centerPos.X, (int) centerPos.Y, null);
            }
        }
    }
}
