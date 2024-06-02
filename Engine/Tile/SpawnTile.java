package Engine.Tile;

import MathLib.Vector2;
public class SpawnTile extends Tile {

    public SpawnTile() {
        super(false);
    }

    public SpawnTile(Vector2 Position) {
        super(Position,false);
    }

    @Override
    public String getImage() {
        return "Images/Tiles/FloorTile.png";
    }
}
