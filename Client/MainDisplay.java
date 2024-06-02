package Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Engine.Engine;
import Engine.Session.*;
import Resources.ResourceManager;
import SimpleUI.Wrappers.ImageLabel;
//testing


/**
 * The MainDisplay class represents the main display window of the application.
 * It extends the JFrame class and contains various panels for different screens.
 * The class also manages the card layout to switch between different screens.
 * @author woojin
 */
public class MainDisplay extends JFrame {
  /**
     * The main panel that holds all the other panels.
     */
    private JPanel mainPanel;

    /**
     * The panel for the play screen.
     */
    private JPanel playScreen;

    /**
     * The panel for the join lobby screen.
     */
    private JPanel joinLobbyScreen;

    /**
     * The panel for the create lobby screen.
     */
    private JPanel createLobbyScreen;

    /**
     * The panel for the lobby screen.
     */
    private JPanel lobbyScreen;

    /**
     * The panel for displaying the game.
     */
    private JPanel gameDisplay;

    /**
     * The CardLayout used to switch between different panels.
     */
    private CardLayout cardLayout;

    /**
     * The session manager to manage game sessions.
     */
    private SessionManager sessionManager;


    /**
     * The MainDisplay class represents the main display window of the Brawl Shapes game.
     * It extends the JFrame class and provides methods to create and manage different screens of the game.
     */
    public MainDisplay() {
        setTitle("Brawl Shapes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        playScreen = createPlayScreen();
        joinLobbyScreen = createJoinLobbyScreen();
        createLobbyScreen = createCreateLobbyScreen();
       // lobbyScreen = createLobbyScreen();

        mainPanel.add(createLobbyScreen, "CreateLobbyScreen");
        mainPanel.add(playScreen, "PlayScreen");
        mainPanel.add(joinLobbyScreen, "JoinLobbyScreen");
        // mainPanel.add(lobbyScreen, "LobbyScreen");
        add(mainPanel);
        cardLayout.show(mainPanel, "PlayScreen");}
        private JPanel createPlayScreen() {
            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(new Color(68, 76, 87)); // Background color

        JButton joinLobbyButton = new JButton();
        joinLobbyButton.setBounds(150, 200, 200, 100);
        joinLobbyButton.setBackground(new Color(173, 216, 230)); // Light blue background
        joinLobbyButton.setForeground(new Color(138, 43, 226)); // Blue-violet text
        //joinLobbyButton.setFont(new Font("Arial", Font.BOLD, 24));
        joinLobbyButton.setLayout(null);
        joinLobbyButton.setOpaque(false);

        ImageLabel joinLobbyImage = new ImageLabel();
        joinLobbyImage.setImage(ResourceManager.getResource("Images/Lobby/JoinLobby.png"));
        joinLobbyImage.setBounds(0,0,200,100);
        // createLobbyImage.setImageSize(200,100);
        joinLobbyButton.add(joinLobbyImage);


        joinLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "JoinLobbyScreen");
        }});
        
        JButton createLobbyButton = new JButton();
        createLobbyButton.setLayout(null);
        ImageLabel createLobbyImage = new ImageLabel();
        createLobbyImage.setImage(ResourceManager.getResource("Images/Lobby/CreateLobby.png"));
         createLobbyImage.setBounds(0,0,200,100);
        // createLobbyImage.setImageSize(200,100);
        createLobbyButton.add(createLobbyImage);

        createLobbyButton.setBounds(450, 200, 200, 100);
        createLobbyButton.setBackground(new Color(173, 255, 47)); 
        createLobbyButton.setForeground(new Color(138, 43, 226)); 
        createLobbyButton.setOpaque(false);
       // createLobbyButton.setFont(new Font("Arial", Font.BOLD, 24));

        createLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "CreateLobbyScreen");
        }});
        // JButton howToPlayButton = new JButton();
        // howToPlayButton.setLayout(null);
        // howToPlayButton.setBounds(20, 150, 127, 64);
        // howToPlayButton.setBackground(new Color(173, 216, 230)); // Light blue background
        // howToPlayButton.setFont(new Font("Arial", Font.BOLD, 14));
        // howToPlayButton.setOpaque(false);

        // ImageLabel HotToPlayImage = new ImageLabel();
        // HotToPlayImage.setImage(ResourceManager.getResource("Images/Lobby/HowToPlay.png"));
        // HotToPlayImage.setBounds(0,0,127,64);
       // HotToPlayImage.setImageSize(127,64);
        //howToPlayButton.add(HotToPlayImage);


        
        ImageLabel lobbyBackGroundImage = new ImageLabel();
        lobbyBackGroundImage.setImage(ResourceManager.getResource("Images/Lobby/FrontScreen.png"));
        lobbyBackGroundImage.setSize(800, 600);
        lobbyBackGroundImage.setLocation(0,-10);
        lobbyBackGroundImage.setImageSize(800, 600);

        panel.add(joinLobbyButton);
        panel.add(createLobbyButton);
        //panel.add(howToPlayButton);
        panel.add(lobbyBackGroundImage);
        createLobbyButton.setVisible(true);
        joinLobbyButton.setVisible(true);
        panel.setVisible(true);
        return panel;
        }

    /**
     * The joiningThread represents a thread that is used for joining other threads.
     */
    private Thread joiningThread;
    /**
     * The function creates a JPanel for a "Join Lobby" screen with input fields for IP and port,
     * connect and close buttons, and a background image.
     * 
     * @return The method `createJoinLobbyScreen()` is returning a `JPanel` that contains a user
     * interface for joining a lobby in a game. The panel includes components such as text fields for
     * entering IP and port, buttons for connecting and closing the lobby screen, and an image
     * background.
     */
    private JPanel createJoinLobbyScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(68, 76, 87)); 

        JPanel boxPanel = new JPanel();
        boxPanel.setBounds(250, 150, 300, 200);
        boxPanel.setBackground(new Color(35, 35, 35)); 
        boxPanel.setLayout(null);

        JLabel joinLobbyLabel = new JLabel("Join Lobby");
        joinLobbyLabel.setBounds(110, 10, 100, 30);
        joinLobbyLabel.setForeground(Color.WHITE);
        boxPanel.add(joinLobbyLabel);

        JTextField ipField = new JTextField("Enter IP");
        ipField.setBounds(50, 50, 200, 30);
        boxPanel.add(ipField);

        ipField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (ipField.getText().equals("Enter IP")) {
                    ipField.setText("");
                }
            }
        });

        JTextField portField = new JTextField("Enter Port");
        portField.setBounds(50, 90, 200, 30);
        boxPanel.add(portField);

        portField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (portField.getText().equals("Enter Port")) {
                    portField.setText("");
                }
            }
        });

        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(50, 130, 200, 30);
        connectButton.setBackground(new Color(144, 238, 144)); 
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try{
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());
                joinLobby(ip, port,connectButton);
               }    
               catch(Exception e1){
                JOptionPane.showMessageDialog(mainPanel, "Invalid IP or Port");
                   System.out.println("Invalid IP or Port");
               }
        }});
        boxPanel.add(connectButton);
        JButton closeButton = new JButton("X");
        closeButton.setBounds(270, 10, 30, 30);
        closeButton.setBackground(new Color(255, 0, 0)); 
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (joiningThread != null) {
                    return;
                }
                cardLayout.show(mainPanel, "PlayScreen");
                
        }});
        boxPanel.add(closeButton);

        ImageLabel lobbyBackGroundImage = new ImageLabel();
        lobbyBackGroundImage.setImage(ResourceManager.getResource("Images/Lobby/lobbySecond.png"));
        lobbyBackGroundImage.setSize(800, 600);
        lobbyBackGroundImage.setLocation(0,-10);
        lobbyBackGroundImage.setImageSize(800, 600);

        panel.add(boxPanel);
        panel.add(lobbyBackGroundImage);

        return panel;
    }
    /**
     * The function `createCreateLobbyScreen` creates a JPanel for a lobby creation screen with input
     * fields for port number and buttons for connecting and closing the lobby.
     * 
     * @return The method `createCreateLobbyScreen()` is returning a `JPanel` that contains a user
     * interface for creating a lobby in a graphical application. The panel includes components such as
     * labels, text fields, buttons for connecting and closing the lobby, and an image background.
     */
    private JPanel createCreateLobbyScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(68, 76, 87)); 

        JPanel boxPanel = new JPanel();
        boxPanel.setBounds(250, 150, 300, 200);
        boxPanel.setBackground(new Color(35, 35, 35)); 
        boxPanel.setLayout(null);

        JLabel createLobbyLabel = new JLabel("Create Lobby");
        createLobbyLabel.setBounds(100, 10, 100, 30);
        createLobbyLabel.setForeground(Color.WHITE);
        boxPanel.add(createLobbyLabel);

        JTextField portField = new JTextField("Enter Port");
        portField.setBounds(50, 70, 200, 30);
        boxPanel.add(portField);

        portField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (portField.getText().equals("Enter Port")) {
                    portField.setText("");
                }
            }
        });


        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(50, 110, 200, 30);
        connectButton.setBackground(new Color(144, 238, 144)); // Light green background
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try{
                int port = Integer.parseInt(portField.getText());
                createLobby(port,connectButton);
               }catch(Exception e1){
                JOptionPane.showMessageDialog(mainPanel, "Invalid Port");
                   System.out.println("Invalid Port");
               }
        }});
        boxPanel.add(connectButton);

        JButton connectOfflineButton = new JButton("Play Offline");
        connectOfflineButton.setBounds(70, 170, 150, 25);
        connectOfflineButton.setBackground( Color.GRAY); // Light green background
        connectOfflineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try{
                createLobby(-1,connectOfflineButton);
               }catch(Exception e1){

               }
        }});
        boxPanel.add(connectOfflineButton);

        JButton closeButton = new JButton("X");
        closeButton.setBounds(270, 10, 30, 30);
        closeButton.setBackground(new Color(255, 0, 0)); 
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                cardLayout.show(mainPanel, "PlayScreen");
        }});
        boxPanel.add(closeButton);
        ImageLabel lobbyBackGroundImage = new ImageLabel();
        lobbyBackGroundImage.setImage(ResourceManager.getResource("Images/Lobby/lobbySecond.png"));
        lobbyBackGroundImage.setSize(800, 600);
        lobbyBackGroundImage.setLocation(0,-10);
        lobbyBackGroundImage.setImageSize(800, 600);

        panel.add(boxPanel);
        panel.add(lobbyBackGroundImage);
        

        return panel;
    }

    /**
     * The function creates a JPanel for a lobby screen and adds it to the main panel using a
     * CardLayout.
     * 
     * @return A `JPanel` object is being returned from the `createLobby()` method.
     */
    private JPanel createLobby() {
       try {
            JPanel panel = new LobbyManager( sessionManager, mainPanel, cardLayout);
            lobbyScreen = panel;
            mainPanel.add(panel, "LobbyScreen");
            cardLayout.show(mainPanel, "LobbyScreen");
            return panel;
       } catch (Exception e) {
           // e.printStackTrace();
        // TODO: handle exception
       }

        return null;
    }
    /**
     * Indicates whether the user can click on the display.
     */
    private boolean canClick = true;

    /**
     * The `joinLobby` function attempts to connect to a lobby using the provided IP address and port,
     * updating the UI accordingly and handling connection failures.
     * 
     * @param ip The `ip` parameter in the `joinLobby` method represents the IP address of the lobby
     * server that you want to connect to. This is the network address that identifies the server on
     * the internet or a local network. It is used to establish a connection to the specific server
     * hosting the lobby for
     * @param port The `port` parameter in the `joinLobby` method refers to the port number on which
     * the server is running. This port number is used to establish a network connection between the
     * client (your application) and the server. It is essential for communication between the client
     * and the server to occur over
     * @param joinLobbyLabel The `joinLobbyLabel` parameter is a JButton object that represents the
     * button used to join a lobby in the user interface.
     */
    private void joinLobby(String ip, int port, JButton joinLobbyLabel) {
        if (!canClick) {
            return;
        }
        sessionManager = new SessionManager(false);
        sessionManager.networkHandler.OnServerClose.connect(reason -> {
            sessionManager.destroy();
            sessionManager = null;
            System.out.println("leave");
            cardLayout.show(mainPanel, "PlayScreen");
        });
        sessionManager.setTargetIpAddress(ip);
        sessionManager.setPort(port);
        joiningThread = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                canClick = false;
                joinLobbyLabel.setText("Attempting to Connect...");
                synchronized (this) {
                sessionManager.startNetwork();
                joinLobbyLabel.setText("Connect");
                createLobby();
                }
    
            } catch (Exception e) {
                if (sessionManager != null)
                    sessionManager.destroy();
                sessionManager = null;
                joinLobbyLabel.setText("Failed To Connect!!!");
                System.out.println("Unable To Join lobby");
                try {
                Thread.sleep(1250);
                } catch (InterruptedException e1) {
                // Handle interruption
                System.out.println("Thread interrupted");
                }
            }
            canClick = true;
            joinLobbyLabel.setText("Connect");
            joiningThread = null;
            }
        });
        joiningThread.start();

    }

    /**
     * The `createLobby` function in Java creates a lobby for a game, handles network connections, and
     * updates the UI accordingly.
     * 
     * @param port The `port` parameter in the `createLobby` method is used to specify the port number
     * on which the lobby will be created for network communication. This port number is essential for
     * establishing connections between clients and the server.
     * @param joinLobbyLabel The `joinLobbyLabel` parameter is a JButton object that represents a
     * button in the user interface. In the `createLobby` method, this button is used to display status
     * messages and change its text based on the progress of creating a lobby.
     */
    private void createLobby(int port , JButton joinLobbyLabel) {
        if (!canClick) {
            return;
        }
        if (port == -1){
            sessionManager = new SessionManager();
            createLobby();
            return;
        }else{
            sessionManager = new SessionManager(true);
        }
 
        sessionManager.networkHandler.OnServerClose.connect(reason -> {
            sessionManager.destroy();
            sessionManager = null;
            cardLayout.show(mainPanel, "PlayScreen");
        });
        sessionManager.setPort(port);
        
        joiningThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
            try {
                canClick = false;
                joinLobbyLabel.setText("Creating Lobby...");
                synchronized (this) {
                sessionManager.startNetwork();
                joinLobbyLabel.setText("Connect");
                }
            } catch (Exception e) {
                sessionManager.destroy();
                sessionManager = null;
                joinLobbyLabel.setText("Failed To Create!!!");
                try {
                Thread.sleep(1250);
                } catch (InterruptedException e1) {
                // Handle interruption
                System.out.println("Thread interrupted");
                }
            }
            createLobby();
            canClick = true;
            joinLobbyLabel.setText("Connect");
            joiningThread = null;

            }
        });
        joiningThread.start();
      
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainDisplay().setVisible(true);
            }
        });
     
    }
}
