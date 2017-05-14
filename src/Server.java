import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Starts the server on the specified port number. It listens on the port for connection requests (log in) from 
 * clients, and accepts them. It then prints the client address with whom it is connected. Next, it asks for the 
 * username and password, verifies that username is in the database, and that the username and passwords match.
 * It then spawns a thread (instance of the ClientThread class) to handle it. 
 */
public class Server {
	public Server(int port) throws IOException {
		ServerSocket server = new ServerSocket(port); // Creates a listening
														// port
		while (true) {
			Socket client = server.accept(); // Accepts connection request from
												// client
			System.out.println("Accepted connection request from " + client.getInetAddress()); // Prints

			// Get the input and output streams for the socket
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			 
			Utils.Message abc = null; /*Recieve from the Client*/
			
			if(abc.what == 1) {
				/*Handle a Login Req*/
				// TODO : Check that the user id and passwords match (Database)
				// Spawn a thread to check for pending requests
				/* No need to spawn a new thread specifically, I believe we can
				 * simply run a small search here; and then spawn a Probe threa */
				String userEmail = abc.field1;
				ClientThread clientThread = new ClientThread(client, userEmail);
			} else if (abc.what == 2) {
				/*handle sending a new message Req*/
			} else if (abc.what == 3) {
				/*handle log out Req*/
			}

			// TODO : figure out when to shut the server
		}
	}
}
