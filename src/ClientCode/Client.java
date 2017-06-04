package ClientCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import MAIN.Message;
import MAIN.Message.MsgType;
import MAIN.Utils;

public class Client {

	private Socket test_socket;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private ClientReceive clientReceive;
	private IPObserver observer;
	private Scanner scanner;
	private String servername, user_id;
	private int port;

	public static void main(String args[]) {
		new Client();
	}

	Client() {
		try {
			servername = "192.168.0.7";
			port = Utils.SERVER_PORT_NUMBER;
			System.out.println("Enter the user id");
			scanner = new Scanner(System.in);
			user_id = scanner.nextLine();

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

			String[] roomNos = msg.field2.split("\\|");
			String auth = roomNos[0];

			if ((fromServer.msgType == Message.MsgType.LOGIN_MSG) && fromServer.field1.equals(user_id)
					&& auth.equals("FALSE")) {
				System.out.println("MESSAGE = " + fromServer.field3);
				System.out.println("FROM = " + fromServer.field4);
				System.exit(0);
			}

			if ((fromServer.msgType == Message.MsgType.LOGIN_MSG) && ((fromServer.field1.compareTo(user_id) == 0))
					&& ((fromServer.field2.compareTo("TRUE")) == 0) && (fromServer.field3 != null)
					&& (fromServer.field4 != null)) {
					String[] msg_split = fromServer.field3.split("\\|");
					String[] sender_split = fromServer.field4.split("\\|");
					for (int i =0; i < msg_split.length; i++){
						System.out.println("MESSAGE = " + msg_split[i]);
						System.out.println("FROM = "+sender_split[i]);
					}
				}

			/*
			 * Receive a msg from client to confirm registered user and then
			 * proceed
			 */
			clientReceive = new ClientReceive(user_id);
			clientReceive.start();
			
			observer = new IPObserver(this, InetAddress.getLocalHost());
			observer.start();

			System.out.println("Enter your choice 1. Send a message 2. Logout");
			int choice = scanner.nextInt();
			scanner.nextLine();

			while (choice != 2) {
				//System.out.println("Enter the recipient user_id");
				String recipient_id = null;

				System.out.println("Enter your message");
				String text_msg = scanner.nextLine();
				System.out.println("Enter the location");
				String location = scanner.nextLine();

				String r_id = null;
				System.out.println("Enter the number of recipients ");
				int count = scanner.nextInt();
				scanner.nextLine();
				for(int i = 0; i < count; i++){
					System.out.println("Enter the recipient user_id");
					recipient_id = scanner.nextLine();
					if(r_id == null) r_id = recipient_id;
					else{
						r_id += "|" + recipient_id;
					}
				}
				//System.out.println("The R_list is " + r_id);

				System.out.println("You are sending the message to:  " + r_id);
				System.out.println("The message is :  " + text_msg);
				System.out.println("The Location is :  " + location);

				msg.msgType = MsgType.SEND_MSG;
				msg.field1 = user_id;
				msg.field2 = r_id;
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
			observer.down();
			clientReceive.down();
			scanner.close();
		}

	}

	public void ipUpdated() {
		try {
			Message msg = new Message();
			msg.msgType = Message.MsgType.IP_UPDATE;
			msg.field1 = user_id;

			test_socket = new Socket(servername, port);
			outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			inputStream = new ObjectInputStream(test_socket.getInputStream());
			outputStream.writeObject(msg);
			outputStream.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}