package ClientCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import MAIN.Message;
import MAIN.Utils;

public class ClientReceive extends Thread {

	private String user_id;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private boolean keepAlive;

	public ClientReceive(String user_id) {
		this.user_id = user_id;
		keepAlive = true;
	}

	public void down() {
		try {
			wait.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		keepAlive = false;
	}

	private ServerSocket wait = null;

	@Override
	public void run() {

		System.out.println("Client Receive is ONLINE WAITING");
		try {
			wait = new ServerSocket(Utils.CLIENT_PORT_NUMBER);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (keepAlive) {
			try {
				Socket test_socket = wait.accept();
				System.out.println("Client Receiver got request from Server Yayy ~!!");
				outputStream = new ObjectOutputStream(test_socket.getOutputStream());
				inputStream = new ObjectInputStream(test_socket.getInputStream());
				Message fromServer = (Message) inputStream.readObject();

				if (fromServer.msgType == Message.MsgType.NOTIFICATION && fromServer.field1.equals(user_id)) {
					System.out.println("MESSAGE = " + fromServer.field3);
					System.out.println("FROM = " + fromServer.field4);
				} else if (fromServer.msgType == Message.MsgType.ACK && fromServer.field1.equals(user_id)) {
					System.out.println("ACK = " + fromServer.field3);
				} else
					System.out.println("We have an unrecognized message from Server " + fromServer);

			} catch (SocketException se) {
				System.out.println("Asked to Kill Client Recieve !");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				outputStream = null;
				inputStream = null;
			}
		}
	}
}