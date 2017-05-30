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

	private Socket test_socket;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private ClientReceive clientReceive;
	private Scanner scanner;

	public static void main(String args[]) {
		new Client();
	}

	Client() {
		try {
			String servername = "192.168.0.7";
			int port = 6066;
			System.out.println("Enter the user id");
			scanner = new Scanner(System.in);
			String user_id = scanner.nextLine();

			System.out.println("Enter the Password");
			String password = scanner.nextLine();

			Message msg = new Message();
			msg.msgType = MsgType.LOGIN_MSG;
			msg.field1 = user_id;
			msg.field2 = password;
			test_socket = new Socket(servername, port);
			outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			inputStream = new ObjectInputStream(test_socket.getInputStream());
			outputStream.writeObject(msg);
			outputStream.flush();

			Message fromServer = (Message) inputStream.readObject();
			System.out.println(fromServer);
			if ((fromServer.msgType == Message.MsgType.LOGIN_MSG) && ((fromServer.field1.compareTo(user_id) == 0))
					&& ((fromServer.field2.compareTo("FALSE")) == 0)) {
				System.out.println("MESSAGE = " + fromServer.field3);
				System.out.println("FROM = " + fromServer.field4);
				System.exit(0);
			}

			if ((fromServer.msgType == Message.MsgType.LOGIN_MSG) && ((fromServer.field1.compareTo(user_id) == 0))
					&& ((fromServer.field2.compareTo("TRUE")) == 0) && (fromServer.field3 != null)
					&& (fromServer.field4 != null)) {
				System.out.println("MESSAGE = " + fromServer.field3);
				System.out.println("FROM = " + fromServer.field4);
			}

			/*
			 * Receive a msg from client to confirm registered user and then
			 * proceed
			 */
			clientReceive = new ClientReceive(user_id);
			clientReceive.start();

			System.out.println("Enter your choice 1. Send a message 2. Logout");
			int choice = scanner.nextInt();
			scanner.nextLine();

			while (choice != 2) {
				System.out.println("Enter the recipient user_id");
				String recipient_id = scanner.nextLine();

				System.out.println("Enter your message");
				String text_msg = scanner.nextLine();
				System.out.println("Enter the location");
				String location = scanner.nextLine();

				System.out.println("You are sending the message to:  " + recipient_id);
				System.out.println("The message is :  " + text_msg);
				System.out.println("The Location is :  " + location);

				msg.msgType = MsgType.SEND_MSG;
				msg.field1 = user_id;
				msg.field2 = recipient_id;
				msg.field3 = text_msg;
				msg.field4 = location;

				test_socket = new Socket(servername, port);
				outputStream = new ObjectOutputStream(test_socket.getOutputStream());
				inputStream = new ObjectInputStream(test_socket.getInputStream());
				outputStream.writeObject(msg);
				outputStream.flush();

				System.out.println("Enter your choice 1. Send a message 2. Logout ");
				choice = scanner.nextInt();
				scanner.nextLine();
			}

			msg = new Message();
			msg.msgType = MsgType.LOGOFF_MSG;
			msg.field1 = user_id;

			test_socket = new Socket(servername, port);
			outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			inputStream = new ObjectInputStream(test_socket.getInputStream());
			outputStream.writeObject(msg);
			outputStream.flush();

			/* Wait for the server to ACK and ONLY then die */
			msg = (Message) inputStream.readObject();
			System.out.println("Thanks for your Interaction ");

		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			clientReceive.down();
			scanner.close();
		}

	}
}