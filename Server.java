package edu.mco364;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;

public class Server extends NetworkApp {
   private ServerSocket server; // server socket

   public Server() {
      super("Server");
      setLocation(WINDOW_WIDTH+10,0);
   }
   @Override
   protected boolean isLeftPaddleActive() {
      return true;
   }
   // set up and run server 
   public void connect() {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket(12345, 100);
         waitForConnection(); // wait for a connection
      } // end try
      catch (EOFException eofException) {
         System.out.println("\nServer terminated connection");
      } // end catch

      catch (IOException ioException) {
         ioException.printStackTrace();
      } // end catch
   }

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException {
      System.out.println("Waiting for connection\n");
      connection = server.accept(); // allow server to accept connection            
      System.out.println("Connection " + " received from: " +
              connection.getInetAddress().getHostName());
   }
}