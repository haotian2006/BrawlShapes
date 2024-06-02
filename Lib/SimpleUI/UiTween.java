package SimpleUI;

import Signal.*;
import Signal.Event;
import SimpleUI.Wrappers.*;

import javax.swing.*;

import java.util.*;
import java.util.List;
import java.awt.*;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.text.html.HTMLDocument.Iterator;

import EasingLib.*;

/**
 * A class that represents a tween animation for a UI component.
 * A tween animation is an animation that interpolates between two values over a period of time.
 * This class can be used to animate the size, position, and color of a UI component.
 * @author haotian
 */

public class UiTween {
    private JComponent component;
    private UiTweenInfo info;
    private HashMap<UiTweenMethods,Object> goal;
    private boolean isPlaying = false;
    private Tween tween;

    private Signal<Boolean> completedSignal = new Signal<Boolean>();
    public final Event<Boolean> completed = completedSignal.event;

    private Signal<Void> cancelledSignal = new Signal<Void>();
    private final Event<Void> cancelled = cancelledSignal.event;


    private void parseGoal(HashMap<UiTweenMethods,Object> goal){
        for (HashMap.Entry<UiTweenMethods,Object> entry : goal.entrySet()) {
            UiTweenMethods method = entry.getKey();
            Object value = entry.getValue();
            if (value.getClass().isArray()){
                continue;
            }
            Object[] data = new  Object[2];
            switch (method) {
                case SizeFromCenter:
                    Dimension dimension = component.getSize();
                    if(!(value instanceof Dimension)){
                        System.out.println(value.toString() +" Is Not A Dimension");
                        value = dimension;
                    }
                    data[0] = dimension;
                    break;
                case Size:
                    Dimension d = component.getSize();
                    if(!(value instanceof Dimension)){
                        System.out.println(value.toString() +" Is Not A Dimension");
                        value = d;
                    }
                    data[0] = d;
                    break;
                case Position:
                    Point p = component.getLocation();
                    if (!(value instanceof Point)){
                        System.out.println(value.toString() +" Is Not A point");
                        value = p;
                    }
                    data[0] = p;
                    break;
                case Color:
                    Color c = component.getBackground();
                    if (!(value instanceof Color)){
                        System.out.println(value.toString() +" Is Not A Color");
                        value = c;
                    }
                    data[0] = c;
                    break;
                default:
                    break;
            }
            data[1] = value;
            goal.put(method, data);
            
        }
    }

    public UiTween(JComponent component,UiTweenInfo info,HashMap<UiTweenMethods,Object> goal ){ 
        this.component = component;
        this.info = info;
        parseGoal(goal);
        this.goal = goal;
        tween = new Tween(info.func,info.type);
    }


    public void stop(){
        isPlaying = false;
        cancelledSignal.fire(null);
    }
    public void play(){
        isPlaying = true;
        int time =  (int)(info.duration*1000+100);
        UiTween uiTween = this;
        Long startTime = (Long)(System.currentTimeMillis());
        Event<Void> cal = cancelled;
        Thread t = new Thread(new Runnable(){
            @Override
            public void run(){
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                cal.once(callBack -> {
                    executor.shutdown();
                    completedSignal.fire(false);
                });
                final float[] i = {0};
                long interval = 10;
                executor.scheduleAtFixedRate(() -> {
                    i[0] += 0.01/info.duration;
                    if (i[0] >= 1) {
                        i[0] = 1;
                    }
                    for (HashMap.Entry<UiTweenMethods,Object> entry : goal.entrySet()) {
                        UiTweenMethods method = entry.getKey();
                        Object[] value = (Object[])entry.getValue();
                        switch (method) {
                            case Size:
                                uiTween.TweenSize((Dimension)value[0],(Dimension)value[1], i[0]);
                                break;
                            case Position:
                                uiTween.TweenLocation((Point)value[0], (Point)value[1], i[0]);
                                break;
                            case Color:
                                uiTween.TweenColor((Color)value[0],(Color)value[1], i[0]);
                                break;
                            case SizeFromCenter:
                                uiTween.TweenSizeFromCenter((Dimension)value[0],(Dimension)value[1], i[0]);
                                break;
                            default:
                                break;
                        }
                    }
                    if (i[0] >= 1 ) {

                        completedSignal.fire(true);
                        isPlaying = false;
                        executor.shutdown();
                    }
                }, 
                0, interval, TimeUnit.MILLISECONDS);
                try {
                    executor.awaitTermination((long)(time), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {

                }
                executor.shutdown();
            }
        });
        t.start();
    }
    

    public Point TweenLocation(Point Start,Point goal,float time){
        int x = (int)tween.getValue((float)Start.x, (float)goal.x, time);
        int y = (int)tween.getValue((float)Start.y, (float)goal.y, time);
        component.setLocation(x,y);
        return new Point(x, y);
    } 

    public Dimension TweenSize(Dimension Start, Dimension Goal,float time){
        int w = (int)tween.getValue((float)Start.width, (float)Goal.width, time);
        int h = (int)tween.getValue((float)Start.height, (float)Goal.height, time);
        component.setSize(w,h);
        return new Dimension(w, h);
    }

    public Dimension TweenSizeFromCenter(Dimension Start, Dimension Goal,float time){
        int w = (int)tween.getValue((float)Start.width, (float)Goal.width, time);
        int h = (int)tween.getValue((float)Start.height, (float)Goal.height, time);

        Dimension size = component.getSize();
        Point loc = component.getLocation();
        Point center = new Point(size.width/2+loc.x, size.height/2+loc.y);

        component.setSize(w,h);

        component.setLocation(center.x-w/2, center.y-h/2);

        return new Dimension(w, h);
    }

    public Color TweenColor(Color Start, Color Goal,float time){
        int r = (int)tween.getValue((float)Start.getRed(), (float)Goal.getRed(), time);
        int g = (int)tween.getValue((float)Start.getGreen(), (float)Goal.getGreen(), time);
        int b = (int)tween.getValue((float)Start.getBlue(), (float)Goal.getBlue(), time);
        int a = (int)tween.getValue((float)Start.getAlpha(), (float)Goal.getAlpha(), time);
        component.setBackground(new Color(r,g,b,a));
        return new Color(r,g,b,a);
    }
}
