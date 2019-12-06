package edu.psu.cs.irc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

/**
 *
 */
public class Client extends JFrame implements ActionListener {
  /**
   * Client Data Members
   */
  private Socket socket;
  private boolean shutdown;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private ExecutorService pool;
  private PacketListener packetListener;

  /**
   * GUI Data Members
   */
  private LoginMenu loginMenu;
  private JTextArea chatDisplay;
  private JTextField textInput;
  private JTextArea userDisplay;
  private JTextArea roomDisplay;
  private String onlineUserList;
  private String activeRoomList;

  // CLIENT METHODS

  /**
   * Constructor
   * Initializes the client object by running the GUI setup functions and
   */
  Client() {
    super("IRC Client");
    System.out.println("Starting up client application...");
    clientGUISetup();
    loginMenu = new LoginMenu();
    loginMenu.setVisible(true);
    System.out.println("Success! Client application started.");
  }

  /**
   *
   */
  private void closeClientApplication() {
    System.out.println("Closing client application...");
    System.exit(0);
  }

  /**
   *
   * @param ip
   * @param port
   * @return a boolean representing whether successful connection to server was made
   */
  private boolean connectToServer(String ip, int port) {
    System.out.println("Connecting to server...");
    shutdown = false;
    try {
      socket = new Socket(ip, port);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
      pool = Executors.newFixedThreadPool(1);
      packetListener = new PacketListener();
      pool.execute(packetListener);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    System.out.println("Success! Connected to server.");
    return true;
  }

  /**
   * Sets shutdown to true, thus exiting the infinite incoming connection loop and closing the server.
   */
  private void disconnectFromServer() {
    System.out.println("Disconnecting from server...");
    shutdown = true;
    Packet packet = new Packet();
    packet.leaveServer();
    sendPacket(packet);
  }

  /**
   *
   */
  private void serverDisconnectCleanup() {
    // disconnect sequence
    System.out.println("Closing connections...");
    try {
      in.close();
      in = null;
      out.close();
      out = null;
      socket.close();
      socket = null;
      pool.shutdown();
      pool = null;
      packetListener = null;
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Success! Connections closed.");
    setVisible(false);
    loginMenu.displayFeedback("Disconnected from server.");
    loginMenu.setVisible(true);
  }

  /**
   *
   * @param packet
   */
  private void sendPacket(Packet packet) {
    try {
      out.writeObject(packet);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(packet.command + " packet sent to server.");
  }

  /**
   *
   * @param packet
   */
  private void packetHandler(Packet packet) {
    String command = packet.command;
    switch (command) {
      case "joinServer": // username logged by server, ready to start chatting
        startChatGUI(packet.message);
        break;
      case "joinRoom":
        //TODO
      case "leaveRoom":
        //TODO
      case "displayToUser":
        //TODO
      case "userListUpdate":
        //TODO
      case "roomListUpdate":
        //TODO
      case "error":
        //TODO
      case "close":
        //TODO
      default:
        //TODO - error handling
    }
  }

  // GUI Methods

  /**
   * Initializes the GUI for the client
   */
  private void clientGUISetup() {
    setSize(900, 500);
    setResizable(false);
    // call stopClient function on close
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        disconnectFromServer();
      }
    });

    // panel holding all individual displays in grid bag layout
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    add(panel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(3, 3, 3, 3);
    gbc.fill = GridBagConstraints.VERTICAL;

    // initialize active user list display
    userDisplay = new JTextArea(25, 15);
    userDisplay.setLineWrap(false);
    userDisplay.setEditable(false);
    JScrollPane userDisplayScroll = new JScrollPane(userDisplay);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 3;
    panel.add(userDisplayScroll, gbc);

    // initializes active room list display
    roomDisplay = new JTextArea(25,15);
    roomDisplay.setLineWrap(false);
    roomDisplay.setEditable(false);
    JScrollPane roomDisplayScroll = new JScrollPane(roomDisplay);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 3;
    panel.add(roomDisplayScroll, gbc);

    // initialize chat dialogue display
    chatDisplay = new JTextArea(25, 40);
    chatDisplay.setText("System: Welcome to the Chat Server!");
    chatDisplay.setLineWrap(true);
    chatDisplay.setEditable(false);
    JScrollPane chatDisplayScroll = new JScrollPane(chatDisplay);
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    panel.add(chatDisplayScroll, gbc);

    // initialize text input field
    textInput = new JTextField(33);
    textInput.addActionListener(this);
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    panel.add(textInput, gbc);

    // initialize send button
    JButton sendButton = new JButton("Send");
    sendButton.addActionListener(this);
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    panel.add(sendButton, gbc);
  }

  /**
   *
   */
  private void startChatGUI(String message) {
    loginMenu.setVisible(false);
    setVisible(true);
    System.out.println("Ready to start chatting!");
    chatDisplay.setText(message);

  }

  /**
   *
   * @param message
   */
  private void displayToUser(String message) {
    chatDisplay.append("\n" + message);
    chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
  }

  /**
   *
   */
  public void actionPerformed(ActionEvent event) {
    String userInput = textInput.getText();
    if(userInput.equals("")) return;
    textInput.setText("");
    // TODO - implement action listener
  }

  /**
   *
   */
  private class PacketListener implements Runnable {
    @Override
    public void run() {
      // packet listening loop
      System.out.println("Listening for packets...");
      while (!shutdown) {
        try {
          Packet packet = (Packet) in.readObject();
          System.out.println(packet.command + " packet received from server.");
          packetHandler(packet);
        } catch (Exception e) {
          if (e instanceof EOFException)
            disconnectFromServer();
          else
            e.printStackTrace();
        }
      }
      serverDisconnectCleanup();
    }
  }

  /**
   *
   */
  private class LoginMenu extends JFrame implements ActionListener {
    // Data Members
    JTextArea feedback;
    JTextField ipField;
    JTextField portField;
    JTextField usernameField;
    JButton connectButton;
    JButton clearButton;

    /**
     * Constructor
     */
    LoginMenu() {
      super("IRC Client");
      loginGUISetup();
    }

    // Methods

    /**
     * initializes the GUI for the login menu
     */
    private void loginGUISetup() {
      setSize(550,250);
      setResizable(false);
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent event) {
          super.windowClosing(event);
          closeClientApplication();
        }
      });

      JPanel panel = new JPanel();
      add(panel);

      // initialize feedback display
      feedback = new JTextArea(5, 40);
      feedback.setText("Welcome to the IRC Client!");
      feedback.setLineWrap(true);
      feedback.setEditable(false);
      panel.add(new JScrollPane(feedback));

      // initialize ip address field
      panel.add(new JLabel("IP Address"));
      ipField = new JTextField();
      ipField.setColumns(33);
      ipField.setText("localhost");
      panel.add(ipField);

      // initialize port number field
      panel.add(new JLabel("Port Number"));
      portField = new JTextField();
      portField.setColumns(33);
      portField.setText("666");
      panel.add(portField);

      // initialize username field
      panel.add(new JLabel("Username"));
      usernameField = new JTextField();
      usernameField.setColumns(33);
      usernameField.setText("anon");
      panel.add(usernameField);

      // initialize connect button
      connectButton = new JButton("Connect to Server");
      connectButton.addActionListener(this);
      panel.add(connectButton);

      // initialize clear button
      clearButton = new JButton("Clear");
      clearButton.addActionListener(this);
      panel.add(clearButton);
    }

    /**
     * appends feedback to the feedback display in the login menu
     * @param message - String of feedback to be appended to display
     */
    private void displayFeedback(String message) {
      feedback.append("\n" + message);
      feedback.setCaretPosition(feedback.getDocument().getLength());
    }

    /**
     * on an action event occuring in the login menu, this function is called
     * @param event - ActionEvent object representing an event that has occurred in the login menu
     */
    public void actionPerformed(ActionEvent event) {
      // connect button pressed
      if (event.getSource() == connectButton) {
        String ip = ipField.getText();
        String portString = portField.getText();
        String username = usernameField.getText();
        if(ip.equals("") || portString.equals("") || username.equals("")) {
          displayFeedback("Please fill out the necessary fields to connect.");
          return;
        }
        int port;
        try {
          port = Integer.parseInt(portString);
        } catch (Exception e) {
          displayFeedback(portString + " is not a valid port number.");
          return;
        }
        displayFeedback("Attempting to connect to server " + ip + ":" + portString + "...");
        if(!connectToServer(ip, port)) {
          displayFeedback("Connection failed.");
          return;
        }
        displayFeedback("Success! Connected to server " + ip + ":" + portString);
        Packet packet = new Packet();
        packet.joinServer(username);
        sendPacket(packet);
      }
      // clear button pressed
      if (event.getSource() == clearButton) {
        ipField.setText("");
        portField.setText("");
        usernameField.setText("");
      }
    }
  }

  public static void main(String[] args) {
    Client client = new Client();
  }
}
