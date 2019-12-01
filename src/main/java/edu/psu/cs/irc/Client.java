package edu.psu.cs.irc;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class Client {
  /**
   * Data Members
   */
  private PrintStream sysout = System.out;
  private String ip;
  private int port;
  private Socket socket;
  private boolean loggedIn;
  private boolean shutdown;

  private ObjectInputStream in;
  private ObjectOutputStream out;

  /**
   * Parameterized Constructor
   */
  Client(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  private void startClient() {
    sysout.println("Starting up client...");
    shutdown = false;
    loggedIn = false;
    try {
      socket = new Socket(ip, port);
      in = new ObjectInputStream(socket.getInputStream());
      out = new ObjectOutputStream(socket.getOutputStream());
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    runClient();
  }

  private void runClient() {
    // packet listening loop
    while (!shutdown) {
      try {
        Packet packet = (Packet) in.readObject();
        packetHandler(packet);
      } catch (Exception e) {
        if (e instanceof EOFException)
          shutdown = true;
        else
          e.printStackTrace();
      }
    }
    // shutdown sequence
    try {
      in.close();
      out.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.exit(0);
  }

  private void sendPacket(Packet packet) {
    try {
      out.writeObject(packet);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void packetHandler(Packet packet) {
    String command = packet.command;
    switch (command) {
      case "joinServer":
        //TODO
      case "joinRoom":
        //TODO
      case "leaveRoom":
        //TODO
      case "displayToUser":
        //TODO
      case "userListUpdate":
        //TODO
      case "error":
        //TODO
      case "close":
        //TODO
      default:
        //TODO - error handling
    }
  }

  public static void main(String[] args) {
    Client chatClient = new Client("localhost", 666);
    chatClient.startClient();
  }
}
