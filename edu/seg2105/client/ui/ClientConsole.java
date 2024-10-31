package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port, String id) 
  {
    try 
    {
      client= new ChatClient(host, port, this, id);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        if(message.startsWith("#")){
          switch(message){
            case "#quit": 
              client.quit();
              break;
            case "#logoff": 
              if (client.isConnected()) {
                client.disconnect();
              } 
              else {
                display("You are already logged off.");
              }
              break;
            case "#sethost":
              if(client.isConnected()){
                display("You cannot set host while still connected");
              }
              else{
                display("Enter a host name");
                try{client.setHost(fromConsole.nextLine());
                }
                catch(Exception e){
                  display("Problem occured when setting host");
                }
              }
              break;
            case "#setport":
            if(client.isConnected()){
              display("You cannot set port while still connected");
            }
            else{
              display("Enter a port number");
              try{
                client.setPort(Integer.parseInt(fromConsole.nextLine()));
              }
              catch(Exception e){
                display("Problem occured when setting port");
              }
            }
            break;
            case "#login":
              if(client.isConnected()){
                display("You are already logged in");
              }
              else{
                try{
                  client.openConnection();
                  display("Login successful");
                }
                catch(Exception e){
                  display("Login failed.");
                }
              }
              break;
            case "#gethost":
              display("Host name: " + client.getHost());
              break;
            case "#getport":
              display("Port number: " + client.getPort());
              break;
            default: 
              display("Invalid command");
              break;
          }
        }else{
          client.handleMessageFromClientUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = "", id = "";
    int port = DEFAULT_PORT;
    host = "localhost";

    try
    {
      id = args[0];
      if (args.length > 1) {
    	  host = args[1];
      }
      if (args.length > 2) {
        port = Integer.parseInt(args[2]);
      }
      
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      System.out.println("You cannot login without an ID");
      System.exit(0);
    }
    ClientConsole chat= new ClientConsole(host,port,id);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
