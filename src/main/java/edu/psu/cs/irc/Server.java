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
  private int port;
  private int threadLimit;
  private ServerSocket serverSocket;
  private boolean shutdown;
  private int threadCount;
  private int roomCount;
  private Map<Integer, ServerThread> threadMap; // mapping of user id #s to ServerThreads
  private Map<Integer, ServerRoom> roomMap; // mapping of room id #s to ServerRooms

  /**
   * GUI Data Members
   */
  private String hostname = "HOST";
  private JTextArea chatDisplay;
  private JTextField textInput;
  private JTextArea userDisplay;
  private JTextArea roomDisplay;
  private String onlineUserList;
  private String activeRoomList;

  /**
   * Parameterized Constructor - initializes title and GUI setup
   *
   * @param port:        port number to start the server listening on
   * @param threadLimit: max number of concurrently running threads allowed
   */
  private Server(int port, int threadLimit) {
    super("IRC Server");
    serverGUISetup();
    this.port = port;
    this.threadLimit = threadLimit;
  }

  // SERVER METHODS

  /**
   * Initializes server to clean state and calls runServer.
   */
  private void startServer() {
    System.out.println("Starting up server...");
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
      System.exit(1);
    }
    setVisible(true);
    runServer();
  }

  /**
   * Initializes thread pool and runs infinite loop to listen for incoming connection requests.
   * Exits the loop upon call of stopServer, which sets shutdown to true, thus exiting the loop and cleaning up.
   */
  private void runServer() {
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
    System.exit(0);
  }

  /**
   * Sets shutdown to true, thus exiting the infinite incoming connection loop and closing the server.
   */
  private void stopServer() {
    System.out.println("Shutting server down...");
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
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        stopServer();
      }
    });

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

  public static void main(String[] args) {
    Server chatServer = new Server(666, 20);
    chatServer.startServer();
  }
}
