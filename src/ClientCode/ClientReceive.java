package ClientCode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import MAIN.Message;


public class ClientReceive extends Thread {

	private Socket test_socket;
	private String user_id;
	private static ObjectInputStream inputStream = null;

	public ClientReceive(Socket test_socket, String user_id){
		this.test_socket = test_socket;
		this.user_id = user_id;
	}
	@Override
	public void run(){
		while(true){ //confirm this
			try{

				inputStream = new ObjectInputStream(test_socket.getInputStream());
				Message fromServer = (Message)inputStream.readObject();
				fromServer = (Message)inputStream.readObject();

				if( (fromServer.msgType == Message.MsgType.NOTIFICATION) &&
				  ( (fromServer.field1.compareTo(user_id)) == 0) ){
					System.out.println(fromServer.field3); 
					System.out.println(fromServer.field4);
				}

				//if(shouldStop)
				//break;
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
				}
		}
	}
}
