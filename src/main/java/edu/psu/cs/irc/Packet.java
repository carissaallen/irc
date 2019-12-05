package edu.psu.cs.irc;

import java.io.Serializable;

class Packet implements Serializable {
  String command;
  int targetid;
  String message;

  private void clear() {
    command = null;
    targetid = -1;
    message = null;
  }

  void joinServer(String message) {
    clear();
    command = "joinServer";
    this.message = message;
  }

  void leaveServer() {
    clear();
    command = "leaveServer";
  }

  void sendMessageAll(String message) {
    clear();
    command = "sendMessageAll";
    this.message = message;
  }

  void sendMessageUser(int targetid, String message) {
    clear();
    command = "sendMessageUser";
    this.targetid = targetid;
    this.message = message;
  }

  void sendMessageRoom(int targetid, String message) {
    clear();
    command = "sendMessageRoom";
    this.targetid = targetid;
    this.message = message;
  }

  void createRoom(String message) {
    clear();
    command = "createRoom";
    this.message = message;
  }

  void joinRoom(int targetid) {
    clear();
    command = "joinRoom";
    this.targetid = targetid;
  }

  void leaveRoom(int targetid) {
    clear();
    command = "leaveRoom";
    this.targetid = targetid;
  }

  void displayRoom(int targetid) {
    clear();
    command = "displayRoom";
    this.targetid = targetid;
  }

  void displayToUser(String message) {
    clear();
    command = "displayToUser";
    this.message = message;
  }

  void serverShutdown() {
    clear();
    command = "serverShutdown";
  }
}
