package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;
import java.net.*;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  ChatIF serverUI;
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF ui) 
  {
    super(port);
    serverUI = ui;
  }

  
  //Instance methods ************************************************

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
    try
    {
      sendToAllClients("SERVER MSG: " + message);
    }
    catch(Exception e)
    {
      serverUI.display
        ("Could not send message to clients.");
    }
  }

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    String id;
    try {
      
      if(msg.toString().startsWith("#login")) {
        if(client.getInfo("id") != null) {
          client.sendToClient("You are already logged in, closing connection.");
          client.close();
        }
        else{
          id = msg.toString().substring(7, msg.toString().indexOf('>')) + ">";
          client.setInfo("id", id);
          System.out.println(id + " has connected");
          client.sendToClient("Login successful");
          sendToAllClients("SERVER MSG: " + id + " has connected");
        }
      }
      else {
        id = client.getInfo("id").toString();
        System.out.println("Message received: " + msg + " from " + id);
        this.sendToAllClients(id + ": " + msg);
      }
    } catch (IOException e) {
      try {
        client.close();
      } catch (IOException ex) {
        System.out.println("Error closing client connection");
      }
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display("Server has stopped listening for connections.");
  }

  /**
	 * Hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
  @Override
	protected void clientConnected(ConnectionToClient client) {
    System.out.println(client.getInfo("id")+" client has connected.");
  }

  /**
	 * Hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
  @Override
	synchronized protected void clientDisconnected(
		ConnectionToClient client) {
      serverUI.display(client.getInfo("id") + " has disconnected");
      sendToAllClients("SERVER MSG: " + client.getInfo("id") + " has disconnected");
    }

  /**
	 * Hook method called each time a client encounters an error.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
  @Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
      serverUI.display(client.getInfo("id") + " client has encountered an error");
      serverUI.display("Now trying to close the client connection");
      try {
          client.close(); //close the client connection if there is an error
      } catch (IOException e) {
          System.out.println("Error closing client connection");
      }
  }

  /**
   * This methecks if the server is fully closed
   * @return true if the server has been closed with the close() method, false otherwise
   */
  public boolean isClosed() {
    try {
        // If we can create a new server socket, then the current one must be closed
        ServerSocket testSocket = new ServerSocket(getPort());
        testSocket.close();
        return true;
    } catch (IOException e) {
        // If we can't create a server socket, there must be one that already exists
        return false;
    }
  }
  
/**
	 * Hook method called when the server is clased.
	 * The default implementation does nothing. This method may be
	 * overriden by subclasses. When the server is closed while still
	 * listening, serverStopped() will also be called.
	 */
	protected void serverClosed() {
    serverUI.display("Server has been closed");
  }

}
  
  
  
  
  //Class methods ***************************************************
  
  /*
   * 
   */
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */

   /* 
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
  */
//End of EchoServer class
