package ClientCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import MAIN.Message;

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

		try {
			wait = new ServerSocket(6068);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (keepAlive) {
			try {
				Socket test_socket = wait.accept();
				outputStream = new ObjectOutputStream(test_socket.getOutputStream());
				inputStream = new ObjectInputStream(test_socket.getInputStream());
				Message fromServer = (Message) inputStream.readObject();

				if (fromServer.msgType == Message.MsgType.NOTIFICATION && fromServer.field1.equals(user_id)) {
					if(!fromServer.field3.contains("|")){
						System.out.println("MESSAGE = " + fromServer.field3);
						System.out.println("FROM = " + fromServer.field4);
					}
					else{
						String[] msg_split = fromServer.field3.split("\\|");
						String[] sender_split = fromServer.field4.split("\\|");
						for (int i =0; i < msg_split.length; i++){
							System.out.println("MESSAGE = " + msg_split[i]);
							System.out.println("FROM = "+sender_split[i]);
						}
					}
				} else {
					System.out.println("We have an unrecognized message from Server " + fromServer);
				}
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