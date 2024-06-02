package SimpleUI.Wrappers;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;


public interface BaseFrame {
    default JComponent getComponent(){
        return (JComponent)this;
    }



    default void setCenter(Point p){
        Dimension size = getComponent().getSize();
        int[] half = new int[]{size.width/2, size.height/2};
        getComponent().setLocation(p.x-half[0], p.y-half[1]);
        
    }
    default void setCenter(int x,int y){
        Dimension size = getComponent().getSize();
        int[] half = new int[]{size.width/2, size.height/2};
        getComponent().setLocation(x-half[0], y-half[1]);
        
    }
    default Point getCenter(){
        Dimension size = getComponent().getSize();
        Point loc = getComponent().getLocation();
        return new Point(size.width/2+loc.x, size.height/2+loc.y);
    }


    default Point GetPositionRelativeToPoint(Point p){
        Point location = getComponent().getLocation();
        return new Point(p.x+location.x,p.y+location.y);
    }

            
}
