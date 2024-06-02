package Engine.Tile;

import MathLib.Vector2;

public class WaterTile extends Tile{
    public WaterTile() {
        super(true);
    }

    public WaterTile(Vector2 Position) {
        super(Position,true);
    }

    @Override
    public String getImage() {
        return "Images/Tiles/Water.png";
    }
}
