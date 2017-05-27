package ClientCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import MAIN.Message;

public class ClientReceive extends Thread {

	private String user_id;
	private static ObjectInputStream inputStream = null;
	boolean shouldIAllow;
	public ClientReceive(String user_id) {
		this.user_id = user_id;
		shouldIAllow = true;
	}

	public void down() {
		try {
			wait.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shouldIAllow = false;
	}
	ServerSocket wait = null;
	@Override
	public void run() {

		
		try {
			wait = new ServerSocket(6068);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (shouldIAllow) { // confirm this
			try {

				Socket test_socket = wait.accept();
				inputStream = new ObjectInputStream(test_socket.getInputStream());
				Message fromServer = (Message) inputStream.readObject();

				if ((fromServer.msgType == Message.MsgType.NOTIFICATION)
						&& ((fromServer.field1.compareTo(user_id)) == 0)) {
					System.out.println("MESSAGE = " + fromServer.field3);
					System.out.println("FROM = " + fromServer.field4);
				}

				// if(shouldStop)
				// break;
			} catch (SocketException se) {
				System.out.println("IZZAT SE CLOSE KIA");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
