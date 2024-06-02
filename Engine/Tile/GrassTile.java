package Engine.Tile;

import MathLib.Vector2;

public class GrassTile extends Tile{
    public GrassTile() {
        super(false);
    }

    public GrassTile(Vector2 Position) {
        super(Position,false);
    }

    @Override
    public String getImage() {
        return "Images/Tiles/Grass.png";
    }
}
