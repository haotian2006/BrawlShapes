package Client;

import java.io.InputStream;

import javax.swing.*;

import Engine.Session.SessionManager;
import Engine.Session.SessionPlayer;
import Resources.ResourceManager;
import Signal.ConnectionContainer;
import SimpleUI.Wrappers.ImageLabel;

import java.awt.event.*;


import java.awt.*;

/**
 * Represents a button that represents a player in the lobby.
 * Extends the JButton class.
 * Contains a reference to the lobby manager, the player, and an image label.
 * Provides functionality for selecting and deselecting the player button.
 * @see JButton
 * @author woojin haotian 
 */

class PlayerButton extends JButton {
    LobbyManager lobby;
    SessionPlayer player;
    /**
     * Represents whether the lobby is selected or not.
     */
    protected boolean selected = false;
    /**
     * Represents an image label that can be displayed in the lobby manager.
     */
    protected ImageLabel imageLabel;

    /**
     * Represents a button for a player in the lobby.
     * This button allows the host to add a bot to the session.
     */
    public PlayerButton(LobbyManager lobby) {
        this.lobby = lobby;
        if (lobby.sessionManager != null && lobby.sessionManager.isHost) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        lobby.sessionManager.addBot();
                    }
                }
            });
        }
    }

    /**
     * Deselects the lobby.
     * Sets the selected flag to false, clears the selected lobby reference, and sets the background color to cyan.
     */
    public void deselect() {
        selected = false;
        lobby.selected  = null;
        setBackground(Color.cyan);
    }

    /**
     * Selects the current player button.
     * If there is already a selected button, it will be deselected.
     * The current button will be set as the selected button and its background color will be set to green.
     */
    public void select(){
        if (lobby.selected!= null){
            lobby.selected.deselect();
        }
        lobby.selected = PlayerButton.this;
        selected = true;
        setBackground(Color.green);
    }

    /**
     * Sets the image of the lobby player panel based on the selected brawler.
     * The image is retrieved from the resource manager using the path specified in the LobbyManager class.
     * After setting the image, the player panel is repainted to reflect the changes.
     */
    public void setImage(){
        imageLabel.setImage(ResourceManager.getResource(LobbyManager.brawlerImages[player.selectedBrawler]));
        //System.out.println(player.selectedBrawler);
        lobby.playerPanel.repaint();
    }


    /**
     * Constructs a new PlayerButton object.
     * 
     * @param lobby The LobbyManager instance associated with the button.
     * @param player The SessionPlayer object associated with the button.
     */
    public PlayerButton(LobbyManager lobby, SessionPlayer player) {
        this.lobby = lobby;
        setBackground(Color.cyan);
        this.player = player;
        setLayout(null);
        imageLabel = new ImageLabel();
        imageLabel.setBounds(10, 15, 70, 70);

   
        imageLabel.setImageSize(70, 70);
        setImage();
        player.temp = this;
        JLabel playerNameLabel = new JLabel(player.name);
        playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerNameLabel.setBounds(0, 4, 90, 16);
        playerNameLabel.setForeground(Color.BLACK);

        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        add(imageLabel);
        add(playerNameLabel);

        if (lobby.sessionManager.isHost) {
            JButton closeButton = new JButton("Remove");
            closeButton.setFont(new Font("Arial", Font.BOLD, 12));
            closeButton.setBounds(0, 75, 95, 20);
            closeButton.setBackground(Color.RED);
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   lobby.sessionManager.removePlayer(player);
                }
            });
            if (!player.name.equals("Host") && !player.name.equals("Local Player")) {
                add(closeButton);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (lobby.selected!= null){
                            lobby.selected.deselect();
                        }
                        lobby.selected = PlayerButton.this;
                        PlayerButton.this.select();

                    }
                }
            });
        }
        if (player.isLocal()){
            select();
        }
    }



    public void destroy(){

    }
}

/**
 * The `LobbyManager` class represents a panel that manages the lobby for the game.
 * It extends the `JPanel` class and provides functionality for displaying players,
 * selecting brawlers, and starting the game.
 * @author woojin
 */
public class LobbyManager  extends JPanel {
    
    /**
     * An array of strings representing the file paths of the brawler images.
     */
    public static String[] brawlerImages = {
        "Images/Entities/Circle.png",
        "Images/Entities/Pentagon.png",
        "Images/Entities/SquareBrawler.png",
        "Images/Entities/Triangle.png",
        "Images/Entities/Kite.png"
    };

    /**
     * An array of PlayerButton objects representing the buttons in the lobby.
     * The length of the array is 2 times the number of players in the lobby.
     */
    PlayerButton[] buttons = new PlayerButton[2*4];
    /**
     * A JPanel is a container that can be used to group and organize other components.
     * It provides a way to group components together and manage their layout.
     */
    JPanel playerPanel;

    /**
     * Manages the sessions for the lobby.
     */
    protected SessionManager sessionManager;
    /**
     * Represents a button that represents a player in the lobby.
     */
    protected PlayerButton selected;
    /**
     * Represents a container for managing connections.
     * This class provides methods for adding, removing, and accessing connections.
     */
    protected ConnectionContainer container = new ConnectionContainer();

    /**
     * The `LobbyManager` class represents a manager for the lobby screen in a game.
     * It handles the functionality related to the lobby, such as adding and removing players,
     * updating player information, selecting brawlers, and starting the game.
     * 
     * This class extends the `JPanel` class and implements various event listeners to handle user interactions.
     * 
     * @param sessionManager1 The session manager responsible for managing the game session.
     * @param mainPanel The main panel of the game.
     * @param cardLayout The card layout used to switch between different screens in the game.
     */
    public LobbyManager(SessionManager sessionManager1, JPanel mainPanel, CardLayout cardLayout) {
        this.sessionManager = sessionManager1;
        JPanel panel = this;
        panel.setLayout(null);
        //panel.setBackground(new Color(248,166,166)); 
        ImageLabel createLobbyImage = new ImageLabel();
        createLobbyImage.setImage(ResourceManager.getResource("Images/Lobby/bg2.png"));
         createLobbyImage.setBounds(0,0,800,600);
        // createLobbyImage.setImageSize(200,100);


        
        ImageLabel lobbyBackGroundImage = new ImageLabel();
        lobbyBackGroundImage.setImage(ResourceManager.getResource("Images/Lobby/lobbySecond.png"));
        lobbyBackGroundImage.setSize(800, 600);
        lobbyBackGroundImage.setLocation(0,-10);
        lobbyBackGroundImage.setImageSize(800, 600);


        JButton leaveLobbyButton = new JButton("Leave Lobby");
        leaveLobbyButton.setBounds(650, 20, 120, 50);
        leaveLobbyButton.setBackground(Color.RED);
        leaveLobbyButton.setForeground(Color.WHITE);
        leaveLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sessionManager != null) {
                    sessionManager.stopNetwork();
                    sessionManager.destroy();
                    sessionManager = null;
    
                }
                cardLayout.show(mainPanel, "PlayScreen");
            }});


        panel.add(leaveLobbyButton);
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(2, 3, 10, 10));
        playerPanel.setBounds(200, 150, 400, 200);
        
        playerPanel.setBackground(new Color(35, 35, 35));

        sessionManager.playerAdded.connect(player -> {
            updateArray();
            paintAgain();
        },container);

        sessionManager.playerRemoved.connect(player -> {
            updateArray();
            paintAgain();
        },container);
        
        sessionManager.changed.connect(player -> {
            if (player.temp != null){
                PlayerButton button = (PlayerButton) player.temp;
                button.setImage();
            }
        },container);

        updateArray();
        
        panel.add(playerPanel);        
        JPanel brawlerPanel = new JPanel();
        brawlerPanel.setLayout(new GridLayout(1, 10, 10, 10));
        brawlerPanel.setBounds(100, 400, 600, 50);
        brawlerPanel.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            InputStream input = ResourceManager.getResource(brawlerImages[i]);
            JButton brawlerButton;
            final int num = i;
            try {
                brawlerButton = new JButton();
                ImageLabel label = new ImageLabel();
                label.setImage(input);
                label.setSize(20, 20);
                label.setImageSize(75,80);

                brawlerButton.add(label);
               // brawlerButton.setBackground(Color.black);
                brawlerPanel.add(brawlerButton);
                brawlerButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (selected != null){
                            selected.player.setBrawler(num);

                        }
                    }
                });
            } 
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        panel.add(brawlerPanel);

        JButton start = new JButton("Start");
        start.setBounds(350, 500, 100, 50);
        start.setBackground(Color.GREEN);
        start.setForeground(Color.BLACK);
        start.setFont(new Font("Arial", Font.BOLD, 22));
        
        if (sessionManager.isHost) {
            panel.add(start);
        }

        sessionManager.endgame.connect(engine -> {
            //System.out.println(engine);
            //System.out.println("End Game Connected");
            mainPanel.remove(engine);
            
            cardLayout.show(mainPanel, "LobbyScreen");
        },container);
 
        sessionManager.startingGame.connect(engine -> {
            mainPanel.add(engine,"Engine");
            cardLayout.show(mainPanel, "Engine");
            
        },container);

        start.addActionListener(new ActionListener() {
            private boolean isRunning = false;
            
            @Override
            public void actionPerformed(ActionEvent e) {
            if (isRunning) {
                return;
            }
            
            isRunning = true;
            
            if (sessionManager != null) {
                if (sessionManager.players.size() < 2) {
                JOptionPane.showMessageDialog(mainPanel, "You need at least 2 players to start the game.");
                isRunning = false;
                return;
                }
                sessionManager.startGame();
            }
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isRunning = false;
                }
            }).start();
            }
        });
        panel.add(createLobbyImage);
        paintAgain();
       // panel.add(lobbyBackGroundImage);


    }

    /**
     * Represents a button for a player in the lobby.
     */
    public PlayerButton createButton(){
        PlayerButton button = new PlayerButton(this);
        Font font = button.getFont();
        Font biggerFont = new Font(font.getName(), Font.BOLD, 20);
        button.setFont(biggerFont);
        button.setText("+");

        return button;
    }

   /**
    * The `destroy` method disconnects all elements in the container and destroys any existing buttons.
    */
    public void destroy(){
        container.disconnectAll();
        for (int i = 0; i < 2*4; i++) {
            if (buttons[i] != null){
                buttons[i].destroy();
            }
        }
    }

    /**
     * The `updateArray` function synchronously updates an array of buttons based on player
     * information.
     */
    synchronized  public void updateArray(){
        for (int i = 0; i < 2*4; i++) {
            if (buttons[i] != null){
                buttons[i].destroy();
            }

            buttons[i] = null;

            try {
                SessionPlayer p = sessionManager.players.get(i);
                buttons[i] = new PlayerButton(this, p);

            }catch (Exception e){
                buttons[i] = createButton();
            }
        }
        playerPanel.repaint();  
    }
/**
 * The `paintAgain` function removes all components from `playerPanel`, revalidates and repaints it,
 * then adds buttons to the panel.
 */

    synchronized public void paintAgain(){
        playerPanel.removeAll();
        playerPanel.revalidate();
        playerPanel.repaint();  

        for (int i = 0; i < 2*4; i++) {
            if(buttons[i] != null)
                playerPanel.add(buttons[i]);
        }
    }




}
