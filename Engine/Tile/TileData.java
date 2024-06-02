package Engine.Tile;

import java.io.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents the data of a tile Used for replicating tiles.
 * @author haotian
 */
public class TileData implements Serializable {

    public Character tile;
    public Short x;
    public Short y;

    /**
     * Constructs a new TileData object.
     *
     * @param tile the character representing the tile
     * @param x    the x-coordinate of the tile
     * @param y    the y-coordinate of the tile
     */
    public TileData(char tile, Short x, Short y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    /**
     * Writes the object to the specified ObjectOutputStream.
     *
     * @param out the ObjectOutputStream to write to
     * @throws IOException if an I/O error occurs while writing the object
     */
    public void writeObject(ObjectOutputStream out) throws IOException {
        out.writeChar(tile);
        out.writeShort(x);
        out.writeShort(y);
    }

    /**
     * Reads the object from the specified ObjectInputStream.
     *
     * @param in the ObjectInputStream to read from
     * @throws IOException            if an I/O error occurs while reading the object
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        tile = (Character) in.readChar();
        x = (Short) in.readShort();
        y = (Short) in.readShort();
    }

    /**
     * Returns a string representation of the TileData object.
     *
     * @return a string representation of the TileData object
     */
    public String toString() {
        return "TileData{" +
                "tile=" + tile +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}