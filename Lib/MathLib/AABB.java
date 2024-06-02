
package MathLib;

import java.io.Serializable;

/**
 * Class for representing an Axis-Aligned Bounding Box.
 * @author Haotian
 */
public class AABB  implements Serializable {

    public Vector2 Position, Size, Velocity;
    
    /**
     * Constructor for AABB object from a position and size vector.
     * @param position the position of the AABB
     * @param size the size of the AABB
     */
    public AABB(Vector2 position, Vector2 size){
        this.Position = position;
        this.Size = size;
        Velocity = new Vector2();
    }

    /**
     * Constructor for AABB object from an existing AABB object.
     * @param copy the AABB to copy from
     * @return a new AABB object with the same parameters as the original
     */
    public AABB copy(){
        AABB copy = new AABB(Position, Size);
        copy.Velocity = Velocity;
        return copy;
    }

    /**
     * Translate the AABB by a given vector.
     * @param v the vector to translate the AABB by
     */
    public void translate(Vector2 v){
        Position = Position.add(v);
    }

    /**
     * Resize the AABB by a given vector.
     * @param v the vector to resize the AABB by
     */
    public void resize(Vector2 v){
        float dX = v.X - Size.X;
        float dY = v.Y - Size.Y;
        Position = new Vector2(Position.X - dX/2, Position.Y - dY/2);
        Size = v;
    }
    
    /**
     * Set the center of the AABB to a given vector.
     * @param center the vector to set the center of the AABB to
     */
    public void setCenter(Vector2 center){
        float X = center.X - Size.X/2;
        float Y = center.Y - Size.Y/2;
        Position = new Vector2(X, Y);
    }
    
    /**
     * Get the center of the AABB.
     * @return the center of the AABB
     */
    public Vector2 getCenter(){
        float X = Position.X + Size.X/2;
        float Y = Position.Y + Size.Y/2;
        return new Vector2(X, Y);
    }
        
    /**
     * Check if the AABB overlaps with another AABB.
     * @param other the AABB to check for overlap with
     * @return true if the two AABBs overlap, false otherwise
     */
    public boolean overlaps(AABB other) {
        float minX1 = Position.X;
        float minY1 = Position.Y;
        float maXX1 = Position.X + Size.X;
        float maXY1 = Position.Y + Size.Y;

        float minX2 = other.Position.X;
        float minY2 = other.Position.Y;
        float maXX2 = other.Position.X + other.Size.X;
        float maXY2 = other.Position.Y + other.Size.Y;

        return maXX1 >= minX2 && maXY1 >= minY2 && minX1 <= maXX2 && minY1 <= maXY2;
    }
        
    /**
     * Check if the AABB contains a given point.
     * @param point the point to check if the AABB contains
     * @return true if the AABB contains the point, false otherwise
     */
    public boolean containsPoint(Vector2 point) {
        float minX = Position.X;
        float maXX = Position.X + Size.X;
        float minY = Position.Y;
        float maXY = Position.Y + Size.Y;

        return point.X >= minX && point.X <= maXX &&
            point.Y >= minY && point.Y <= maXY;
    }
    
    /**
     * Get the broadphase AABB for this AABB.
     * @return the broadphase AABB for this AABB
     */
    public AABB getBroadphase() {
        float minX = Velocity.X > 0 ? Position.X : Position.X + Velocity.X;
        float minY = Velocity.Y > 0 ? Position.Y : Position.Y + Velocity.Y;
        float w = Size.X + Math.abs(Velocity.X);
        float h = Size.Y + Math.abs(Velocity.Y);
    
        return new AABB(new Vector2(minX, minY), new Vector2(w, h));
    }

    /**
     * Get the swept AABB for this AABB and another AABB.
     * @param other the AABB to get the swept AABB for
     * @param minTime the minimum time to get the swept AABB for
     * @param normal the normal of the swept AABB
     * @return the swept AABB for this AABB and the given AABB
     */
    public float sweptAABB(AABB other, float minTime, int[] normal ) {

        float velocityX = Velocity.X;
        float velocityY = Velocity.Y;
        
        float xPos1 = Position.X;
        float yPos1 = Position.Y;
        
        float xPos2 = other.Position.X;
        float yPos2 = other.Position.Y;
        
        float xSize1 = Size.X;
        float ySize1 = Size.Y;
        
        float xSize2 = other.Size.X;
        float ySize2 = other.Size.Y;
        
        float invEntryX, invExitX, entryX, exitX;
        float invEntryY, invExitY, entryY, exitY;
        
        invEntryX = invExitX = entryX = exitX = 0;
        invEntryY = invExitY = entryY = exitY = 0;
        
        if (velocityX > 0) {
            invEntryX = xPos2 - (xPos1 + xSize1);
            invExitX = (xPos2 + xSize2) - xPos1;

            entryX = invEntryX / velocityX;
            exitX = invExitX / velocityX;
        } else if (velocityX < 0) {
            invEntryX = (xPos2 + xSize2) - xPos1;
            invExitX = xPos2 - (xPos1 + xSize1);
            entryX = invEntryX / velocityX;
            // System.err.println(xPos2);
            // System.err.println(xPos1);
            // System.err.println(xSize2);
            // System.err.println(invEntryX);
            // System.err.println(velocityX);
            exitX = invExitX / velocityX;
        } else {
            invEntryX = (xPos2+xSize2) - xPos1;
            invExitX = xPos2 - (xPos1 + xSize1);

            entryX = Float.NEGATIVE_INFINITY;
            exitX = Float.POSITIVE_INFINITY;
        }
        
        if (velocityY > 0) {
            invEntryY = yPos2 - (yPos1 + ySize1);
            invExitY = (yPos2 + ySize2) - yPos1;
            entryY = invEntryY / velocityY;
            exitY = invExitY / velocityY;
        } else if (velocityY < 0) {
            invEntryY = (yPos2 + ySize2) - yPos1;
            invExitY = yPos2 - (yPos1 + ySize1);
            entryY = invEntryY / velocityY;
            exitY = invExitY / velocityY;
        } else {
            invEntryY = (yPos2+ySize2) - yPos1;
            invExitY = yPos2 - (yPos1 + ySize1);

            entryY = Float.NEGATIVE_INFINITY;
            exitY = Float.POSITIVE_INFINITY;
        }
        
        float entryTime = Math.max(Math.max(entryX, entryY), 0);
        
        if (entryTime >= minTime) {
            return 1f;
        }
        if (entryTime < 0) {
            return 1f;
        }
        
        float exitTime = Math.min(exitX, exitY);
        if (entryTime > exitTime) {
            return 1f;
        }
        
        if (entryX > 1) {
            if (xPos2 + xSize2 < xPos1 || xPos1 + xSize1 > xPos2) {
                return 1f;
            }
        }
        if (entryY > 1) {
            if (yPos2 + ySize2 < yPos1 || yPos1 + ySize1 > yPos2) {
                return 1f;
            }
        }
        
        if (entryX > entryY) {
            normal[0] = (int) -Math.signum(velocityX);
            normal[1] = 0;
        } else {
            normal[0] = 0;
            normal[1] = (int) -Math.signum(velocityY);
        }
    
      return entryTime;
    }

}
