package InputManager;

import Signal.*;
import javax.swing.*;

import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;
import java.awt.event.*;

/**
 * Handles all the input events for the game.
 * @author Haotian
 */
public class InputManager {

   private Signal<KeyEvent> inputBeganSignal = new Signal<KeyEvent>();
   /**
    * Fires when a key is pressed
    */
   public final Event<KeyEvent> InputBegan = inputBeganSignal.event;

   private Signal<KeyEvent> InputEndedSignal = new Signal<KeyEvent>();
   /**
    * Fires when a key is released
    */
   public final Event<KeyEvent> InputEnded = InputEndedSignal.event;


   private Signal<MouseEvent> mouseClickSignal = new Signal<MouseEvent>();
   /**
    * Fires when a mouse button is clicked and released
    */
   public final Event<MouseEvent> MouseClick = mouseClickSignal.event;

   private Signal<MouseEvent> mouseBeganSignal = new Signal<MouseEvent>();
   /**
    * Fires when a mouse button is clicked
    */
   public final Event<MouseEvent> MouseBegan = mouseBeganSignal.event;

   private Signal<MouseEvent> mouseEndedSignal = new Signal<MouseEvent>();
   /**
    * Fires when a mouse button is released
    */
   public final Event<MouseEvent> MouseEnded = mouseEndedSignal.event;

   private Signal<MouseEvent> mouseChangeSignal = new Signal<MouseEvent>();
   /**
    * Fires when a mouse moves
    */
   public final Event<MouseEvent> MouseChange = mouseChangeSignal.event;

   private Signal<MouseWheelEvent> mouseWheelChangeSignal = new Signal<MouseWheelEvent>();
   /**
    * Fires when a mouse wheel is scrolled
    */
   public final Event<MouseWheelEvent> MouseWheelChange = mouseWheelChangeSignal.event;


   private HashSet<Integer> pressedKeys = new HashSet<Integer>();
   private boolean init = false;

   private HashSet<Integer> pressedButtons = new HashSet<Integer>(); 


   public InputManager(JPanel frame) {
      if (init) {
         return;
      }
      init = true;
      frame.addKeyListener(new KeyAdapter() {

         @Override
         public void keyPressed(KeyEvent e) {
            if (pressedKeys.contains(e.getKeyCode())) {
               return;
            }
            pressedKeys.add(e.getKeyCode());
            inputBeganSignal.fire(e);
         }

         @Override
         public void keyReleased(KeyEvent e) {
            pressedKeys.remove(e.getKeyCode());
            InputEndedSignal.fire(e);
         }
      });

      frame.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent  e) {
            mouseClickSignal.fire(e);
         }

         @Override
         public void mousePressed(MouseEvent e) {
            mouseBeganSignal.fire(e);
            pressedButtons.add(e.getButton());
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            mouseEndedSignal.fire(e);
            pressedButtons.remove(e.getButton());
         }
     });

     // Add mouse motion listener
     frame.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
            mouseChangeSignal.fire(e);
         }
     });

     frame.addMouseWheelListener(new MouseWheelListener() {
         @Override
         public void mouseWheelMoved(MouseWheelEvent e) {
            mouseWheelChangeSignal.fire(e);
         }
      });

      frame.addFocusListener(new FocusListener() {
           public void focusGained(java.awt.event.FocusEvent focusEvent) {
          }

         public void focusLost(java.awt.event.FocusEvent focusEvent) {
           // System.out.println("focus lost");
            pressedKeys.clear();
            pressedButtons.clear();
         }
         });
   }

   /**
    * Checks if the given key is pressed.
    * @param key the key to check
    * @return true if the key is pressed, false otherwise
    */
   public  boolean isKeyDown(KeyEvent key) {
      return pressedKeys.contains(key.getKeyCode());
   }

   /**
    * Checks if the given key is pressed.
    * @param keyCode the key code to check
    * @return true if the key is pressed, false otherwise
    */
   public  boolean isKeyDown(int keyCode) {
      return pressedKeys.contains(keyCode);
   }
   
   /**
    * Checks if the given button is pressed.
    * @param button the button to check
    * @return true if the button is pressed, false otherwise
    */
   public boolean isButtonDown(MouseEvent button) {
      return pressedButtons.contains(button.getButton());
   }

   /**
    * Checks if the given button is pressed.
    * @param buttonId the button id to check
    * @return true if the button is pressed, false otherwise
    */
   public boolean isButtonDown(int buttonId) {
      return pressedButtons.contains(buttonId);
   }
}

