package Engine.Tile;

import MathLib.Vector2;
public class FloorTile extends Tile {

    public FloorTile() {
        super(false);
    }

    public FloorTile(Vector2 Position) {
        super(Position,false);
    }

    @Override
    public String getImage() {
        return "Images/Tiles/FloorTile.png";
    }
}
