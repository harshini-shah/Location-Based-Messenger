package ClientCode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import MAIN.Message;
import MAIN.Message.MsgType;

public class Client {

	private static Socket test_socket;
	private static ObjectInputStream inputStream = null;
	private static ObjectOutputStream outputStream = null;

	public static void main(String args[]){

		try{
			String servername = "192.168.0.7";
			int port = 6066;
			test_socket = new Socket(servername,port);
			outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			inputStream = new ObjectInputStream(test_socket.getInputStream());
			
			System.out.println("Enter the user id");
			Scanner scanner = new Scanner(System.in);
			String user_id = scanner.nextLine();
			
			System.out.println("Enter the Password");
			//Scanner scanner = new Scanner(System.in);
			String password = scanner.nextLine();
			
			Message msg = null;
			msg.msgType = MsgType.LOGIN_MSG;
			msg.field1 = user_id;
			msg.field2 = password;
			outputStream.writeObject(msg);
			
			Message fromServer = (Message)inputStream.readObject();
			if(msg.field2.compareTo("FAILURE") == 0){
				System.out.println("You are not registered to the System");
				System.exit(0);
			}
			
		//Receive a msg from client to confirm registered user and then proceed
			ClientReceive clientReceive = new ClientReceive(test_socket);
			//test_socket.close();
			//System.out.println("Your user id is:  " + user_id);

			System.out.println("Enter your choice 1. Send a message 2. Logout");
			int choice = scanner.nextInt();
			String slash_n = scanner.nextLine();
			//System.out.println("Your choice is :" + choice);
			//int choice = 1;
			while(choice != 2)
			{
				System.out.println("Enter the recipient user_id");
				String recipient_id = scanner.nextLine();
				//System.out.println("Recipient user_id is "+ recipient_id);
				System.out.println("Enter your message");
				String text_msg = scanner.nextLine();
				System.out.println("Enter the location");
				String location = scanner.nextLine();
				
				System.out.println("Your user id is:  " + recipient_id);
				System.out.println("Your msg is :  " + text_msg);
				System.out.println("Your location is :  " + location);
				//test_socket = new Socket(servername,port);
				msg.msgType = MsgType.SEND_MSG;
				msg.field2 = recipient_id;
				msg.field3 = text_msg;
				msg.field4 = location;// Convert it to location object
				outputStream.writeObject(msg);
				//test_socket.close();
				
				System.out.println("Enter your choice 1. Send a message 2. Logout ");
				choice = scanner.nextInt();
				slash_n = scanner.nextLine();

			}
			msg.msgType = MsgType.LOGOFF_MSG;
			msg.field1 = user_id;
			outputStream.writeObject(msg);
			System.out.println("Thanks for your Interaction ");
			//clientreceive.stop // Kill the thread here
			clientReceive.join();
			scanner.close();		
		}
		catch (SocketException se) {
			se.printStackTrace();
			// System.exit(0);
			} 
		catch (IOException e) {
			e.printStackTrace();
			} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}