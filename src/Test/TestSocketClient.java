//package Test;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
////File Name GreetingClient.java
//import java.net.Socket;
//
//public class TestSocketClient {
//
//public static void main(String [] args) {
//   String serverName = "10.11.228.111";//args[0];
//   int port = 6066;//nteger.parseInt(args[1]);
//   try {
//      System.out.println("Connecting to " + serverName + " on port " + port);
//      Socket client = new Socket(serverName, port);
//      
//      System.out.println("Just connected to " + client.getRemoteSocketAddress());
//      OutputStream outToServer = client.getOutputStream();
//      DataOutputStream out = new DataOutputStream(outToServer);
//      
//      out.writeUTF("Hello from " + client.getLocalSocketAddress());
//      InputStream inFromServer = client.getInputStream();
//      DataInputStream in = new DataInputStream(inFromServer);
//      
//      System.out.println("Server says " + in.readUTF());
//      client.close();
//   }catch(IOException e) {
//      e.printStackTrace();
//   }
//}
//}
