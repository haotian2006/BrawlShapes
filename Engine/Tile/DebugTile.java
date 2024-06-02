package Engine.Tile;

import MathLib.Vector2;

public class DebugTile extends Tile{
    public DebugTile() {
        super(false);
    }

    public DebugTile(Vector2 Position) {
        super(Position,false);
    }

    @Override
    public boolean getTransparency(){
        return true;
    }
    @Override
    public String getImage() {
        return "Images/Tiles/Debug.png";
    }
}
