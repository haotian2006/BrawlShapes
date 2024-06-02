package Engine;

import javax.swing.*;

import SimpleUI.UiTween;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import Signal.*;
import Signal.Event;

import MathLib.Vector2;
import Engine.Entities.Brawler;
import Engine.Entities.Entity;
import Engine.*;
import Engine.Enums.CameraMode;

/**
 * The Camera class represents a camera in the game engine.
 * It is responsible for controlling the view of the game world.
 * @author woojin
 */
public class Camera {
    public Vector2 Position = new Vector2(5,5);
    public Entity Subject;
    public CameraMode Mode = CameraMode.Normal;
    public final Engine engine;

    private Vector2 lastMousePos = new Vector2();

    /**
     * Constructs a new Camera object.
     * @param engine The game engine.
     */
    public Camera(Engine engine) {
        this.engine = engine;
    }

    /**
     * Sets the entity that the camera should follow.
     * @param subject The entity to follow.
     */
    public void setEntity(Entity subject) {
        this.Subject = subject;
        // You can add camera interpolation logic here
    }

    /**
     * Switches the camera to the next brawler in the spectating mode.
     */
    public void nextBrawler(){
        if(Mode != CameraMode.Spectating){
            return;
        }
        ArrayList<Brawler> brawlers = new ArrayList<Brawler>( engine.entityHandler.getAllBrawlers());
        int idx = brawlers.indexOf(Subject);
        if (brawlers.size() == 0){
            return;
        }
        if (idx < brawlers.size() - 1) {
            Subject = brawlers.toArray(new Brawler[brawlers.size()])[idx + 1];
        } else {
            Subject = brawlers.toArray(new Brawler[brawlers.size()])[0];
        }  
    }

    /**
     * Switches the camera to the previous brawler in the spectating mode.
     */
    public void lastBrawler(){
        if(Mode != CameraMode.Spectating){
            return;
        }
        ArrayList<Brawler> brawlers = new ArrayList<Brawler>( engine.entityHandler.getAllBrawlers());
        int idx = brawlers.indexOf(Subject);
        if (brawlers.size() == 0){
            return;
        }
        if (idx > 0) {
            Subject = brawlers.toArray(new Brawler[brawlers.size()])[idx - 1];
        } else {
            Subject = brawlers.toArray(new Brawler[brawlers.size()])[brawlers.size() - 1];
        }
    }

    /**
     * Returns the entity that the camera is currently following.
     * @return The entity that the camera is following.
     */
    public Entity getEntity() {
        return Subject;
    }
    
    /**
     * Returns the relative mouse location within the game world.
     * @return The relative mouse location.
     */
    public Vector2 getRelativeMouseLoc() {
        Point mousePoint = engine.getMousePosition();
        if (mousePoint == null) {
            return lastMousePos;
        }
        Vector2 center = engine.calculateCenter();
        Vector2 mouseLoc = Vector2.fromPoint(mousePoint);
        lastMousePos = (mouseLoc.sub(center).div(Engine.GRID_SCALE)).add(getPosition().sub(new Vector2(.5f,.5f)));
        return lastMousePos;
    }

    /**
     * Returns the position of the camera.
     * If the camera mode is set to Custom and there is no subject, the position is returned.
     * If the camera mode is not Custom and there is a subject, the center of the subject is returned.
     * @return The position of the camera.
     */
    public Vector2 getPosition() {
        if (Mode != CameraMode.Custom && Subject != null) {
            return Subject.getCenter();
        }
        else {
            return Position;
        }
    }
}