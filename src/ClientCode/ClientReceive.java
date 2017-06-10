package ClientCode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import MAIN.Message;


public class ClientReceive extends Thread {

	private Socket test_socket;
	private static ObjectInputStream inputStream = null;

	public ClientReceive(Socket test_socket){
		this.test_socket = test_socket;
	}
	@Override
	public void run(){
		while(true){ //confirm this
			try{

				inputStream = new ObjectInputStream(test_socket.getInputStream());
				Message fromServer = (Message)inputStream.readObject();
				//for(int i = 0; i < fromServer.count; i++) // For counting the number of messages in the queue to run the loop
				//{
					fromServer = (Message)inputStream.readObject();
					System.out.println(fromServer.field3);
				//}
			}
			
			//if(shouldStop)
				//break;
			
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
