package edu.psu.cs.irc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server extends JFrame implements ActionListener {
  /**
   * Server Data Members
   */
  private int threadLimit = 20;
  private ConnectionListener connectionListener;
  private ServerSocket serverSocket;
  ExecutorService pool;
  private boolean serverHosted;
  private boolean shutdown;
  private int threadCount;
  private int roomCount;
  private Map<Integer, ServerThread> threadMap; // mapping of user id #s to ServerThreads
  private Map<Integer, ServerRoom> roomMap; // mapping of room id #s to ServerRooms

  /**
   * GUI Data Members
   */
  private LoginMenu loginMenu;
  private String hostname;
  private JTextArea chatDisplay;
  private JTextField textInput;
  private JTextArea userDisplay;
  private JTextArea roomDisplay;
  private String onlineUserList;
  private String activeRoomList;

  /**
   * Constructor
   */
  private Server() {
    super("IRC Server");
    System.out.println("Starting up server application...");
    serverGUISetup();
    loginMenu = new LoginMenu();
    loginMenu.setVisible(true);
  }

  /**
   *
   */
  private class ConnectionListener implements Runnable {
    /**
     * Initializes thread pool and runs infinite loop to listen for incoming connection requests.
     * Exits the loop upon call of stopServer, which sets shutdown to true, thus exiting the loop and cleaning up.
     */
    @Override
    public void run() {
      ExecutorService pool = Executors.newFixedThreadPool(threadLimit);
      // loop for accepting client connection requests
      while (!shutdown) {
        try {
          Socket clientSocket = serverSocket.accept();
          ++threadCount;
          System.out.println("System: New client connected - id # " + threadCount);
          ServerThread serverThread = new ServerThread(clientSocket, threadCount);
          pool.execute(serverThread);
          threadMap.put(threadCount, serverThread);
        } catch (Exception e) {
          if(e instanceof SocketTimeoutException)
            continue;
          e.printStackTrace();
          if (!(e instanceof SocketException)) {
            System.exit(1);
          }
        }
      }
      // shutdown sequence once loop breaks
      try {
        // TODO - clear internal data and close client socket connections
        pool.shutdown();
        serverSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      setVisible(false);
      loginMenu.setVisible(true);
    }
  }

  // SERVER METHODS

  /**
   * Closes the server application
   */
  private void closeServer() {
    System.out.println("Closing server application...");
    System.exit(0);
  }

  /**
   * Initializes server to clean state and calls runServer.
   */
  private boolean startServer(int port, String username) {
    System.out.println("Starting up server...");
    hostname = username;
    shutdown = false;
    threadCount = 0;
    roomCount = 0;
    threadMap = new HashMap<>();
    roomMap = new HashMap<>();
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(1000);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    pool = Executors.newFixedThreadPool(threadLimit);
    System.out.println("Success! Server now hosted.");
    setVisible(true);
    loginMenu.setVisible(false);
    return true;
  }

  /**
   *
   */
  private void runConnectionListener() {
    connectionListener = new ConnectionListener();
    pool.execute(connectionListener);
  }

  /**
   * Sets shutdown to true, thus exiting the infinite incoming connection loop and stopping the server.
   */
  private void stopServer() {
    System.out.println("Stopping server...");
    shutdown = true;
  }

  /**
   * Takes a packet from a specified user and determines what action to take given the packet's command value.
   *
   * @param packet   - packet containing data from the user
   * @param senderid - id # of the user that sent the packet
   */
  private void packetHandler(Packet packet, int senderid) {
    String command = packet.command;
    switch (command) {
      case "joinServer":
        joinServer(senderid, packet.message);
        break;
      case "sendMessageAll":
        sendMessageAll(senderid, packet.message);
        break;
      case "sendMessageUser":
        sendMessageUser(senderid, packet.targetid, packet.message);
        break;
      case "sendMessageRoom":
        sendMessageRoom(senderid, packet.targetid, packet.message);
        break;
      case "createRoom":
        createRoom(senderid, packet.message);
        break;
      case "joinRoom":
        joinRoom(senderid, packet.targetid);
        break;
      case "leaveRoom":
        leaveRoom(senderid, packet.targetid);
        break;
      case "displayRoom":
        displayRoom(senderid, packet.targetid);
        break;
      default:
        //TODO - error handling
    }
  }

  private void joinServer(int senderid, String username) {

  }

  private void sendMessageAll(int senderid, String messsage) {

  }

  private void sendMessageUser(int senderid, int targetid, String message) {

  }

  private void sendMessageRoom(int senderid, int targetid, String message) {

  }

  private void createRoom(int senderid, String roomName) {

  }

  private void joinRoom(int senderid, int targetid) {

  }

  private void leaveRoom(int senderid, int targetid) {

  }

  private void displayRoom(int senderid, int targetid) {

  }

  // GUI METHODS

  /**
   * Initializes the GUI for the server
   */
  private void serverGUISetup() {
    setSize(900, 500);
    setResizable(false);
    // call stopServer function on close
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        super.windowClosing(event);
        stopServer();
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
  public void actionPerformed(ActionEvent event) {
    String userInput = textInput.getText();
    if(userInput.equals("")) return;
    textInput.setText("");
    // TODO - implement action listener
    System.out.println("A thing!");
  }


  /**
   *
   */
  class ServerThread implements Runnable {
    Socket clientSocket;
    int id;
    String username;
    ObjectInputStream in;
    ObjectOutputStream out;
    boolean shutdownThread;

    ServerThread(Socket clientSocket, int id) {
      this.clientSocket = clientSocket;
      this.id = id;
      try {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      while (!shutdownThread) {
        try {
          Packet packet = (Packet) in.readObject();
          packetHandler(packet, id);
        } catch (Exception e) {
          if (e instanceof EOFException)
            shutdownThread = true;
          else
            e.printStackTrace();
        }
      }
      try {
        out.close();
        in.close();
        clientSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private void sendPacket(Packet packet) {
      try {
        out.writeObject(packet);
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Object holding user id #s of users that are members of a given room
   */
  class ServerRoom {
    String roomName;
    Vector<Integer> members;

    ServerRoom(String roomName, int initialMember) {
      this.roomName = roomName;
      members = new Vector<>();
      members.add(initialMember);
    }
  }

  /**
   *
   */
  private class LoginMenu extends JFrame implements ActionListener {
    // Data Members
    JTextArea feedback;
    JTextField portField;
    JTextField usernameField;
    JButton startButton;
    JButton clearButton;

    /**
     * Constructor
     */
    LoginMenu() {
      super("IRC Server");
      loginGUISetup();
    }

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
          closeServer();
        }
      });

      JPanel panel = new JPanel();
      add(panel);

      // initialize feedback display
      feedback = new JTextArea(5,40);
      feedback.setText("Welcome to the IRC Server!");
      feedback.setLineWrap(true);
      feedback.setEditable(false);
      panel.add(new JScrollPane(feedback));

      // initialize port number field
      panel.add(new JLabel("Port Number"));
      portField = new JTextField();
      portField.setColumns(33);
      panel.add(portField);

      // initialize username field
      panel.add(new JLabel("Username"));
      usernameField = new JTextField();
      usernameField.setColumns(33);
      panel.add(usernameField);

      // initialize connect button
      startButton = new JButton("Start Server");
      startButton.addActionListener(this);
      panel.add(startButton);

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
      // start server button pressed
      if (event.getSource() == startButton) {
        String portString = portField.getText();
        String username = usernameField.getText();
        if (portString.equals("") || username.equals("")) {
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
        displayFeedback("Attempting to host server on port " + portString + "...");
        if (!startServer(port, username)) {
          displayFeedback("Unable to host server.");
          return;
        }
        displayFeedback("Success! Server hosted on port " + portString + ".");
        runConnectionListener();
      }
    }
  }

  public static void main(String[] args) {
    Server server = new Server();
  }
}
