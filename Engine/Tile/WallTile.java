package Engine.Tile;

import MathLib.Vector2;

public class WallTile extends Tile {
     public WallTile() {
        super(true);
    }

    public WallTile(Vector2 Position) {
        super(Position,true);
    }

    @Override
    public String getImage() {
        return "Images/Tiles/WallTiles.png";

    }
}
