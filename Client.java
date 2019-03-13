package edu.mco364;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends NetworkApp {
   private final String chatServer;

   // initialize chatServer and set up GUI
   public Client(String host) {
      super("Client");
      chatServer = host; // set server to which this client connects
      setLocation(0,0);
   }

   @Override
   protected boolean isLeftPaddleActive() {
      return false;
   }

   // connect to server
   protected void connect() {
      System.out.println("Attempting connection\n");

      do {

         try {
            // create Socket to make connection to server
            connection = new Socket(InetAddress.getByName(chatServer), 12345);
         } catch (UnknownHostException e) {
         } catch (IOException e) {
         }
         System.out.println("Trying to connect");
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      while (connection == null);

      System.out.println("Connected to: " +
              connection.getInetAddress().getHostName());
   }
}

