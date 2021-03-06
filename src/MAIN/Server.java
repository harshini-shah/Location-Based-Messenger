package MAIN;
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
 * 
 *  TODO : All synchronization is left
 */
public class Server {

	public Server(int port) throws IOException {
		/* Creates a listening */
		ServerSocket server = new ServerSocket(port);

		while (true) {
			Socket client = server.accept(); // Accepts connection request from
												// client

			System.out.println("Accepted connection request from " + client.getInetAddress()); // Prints

			// Get the input and output streams for the socket
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			Message msg = null;
			String userEmail = null;

			out.writeUTF("Please enter your username and password");
			try {
				msg = (Message) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (msg.msgType == Message.MsgType.LOGIN_MSG) {
				/*
				 * Handle a Login Req
				 * 
				 * TODO: Need to store info about client that can be used to
				 * connect with it later (if someone sends it a message)
				 */

				userEmail = msg.field1;
				// Check that the user email is there in the DB
				if (!registeredUser(userEmail)) {
					String invalidUserMsg = "ERROR: Email ID Not Registered on System";
					out.writeObject(invalidUserMsg);
					client.close();
					continue;
				}

				String password = msg.field2;
				// Check that the passwords match
				if (!checkPassword(userEmail, password)) {
					String wrongPasswordMsg = "ERROR: Wrong Password";
					out.writeObject(wrongPasswordMsg);
					client.close();
					continue;
				}

				// Add to the set of logged in users after setting the location
				// TODO : set location
				Utils.logUserOn(userEmail, new User(userEmail));

				// Spawn a thread to check the queue if it exists
				if (Utils.messageQueueForUserExists(userEmail)) {
					ClientThread clientThread = new ClientThread(client, userEmail);
				} else {
					String noNewMessagesMsg = "You do not have any pending messages at this time";
					out.writeObject(noNewMessagesMsg);
					client.close();
				}
			} else if (msg.msgType == Message.MsgType.SEND_MSG) {
				/* handle sending a new message */
				userEmail = msg.field1;
				if (!Utils.isUserOnline(userEmail)) {
					System.out.println("ERROR: You are not logged on yet so cannot send a message");
				}

				// Check if the user is online and the location is a match
				if (Utils.isUserOnline(userEmail) && Utils.getCurrentLocationForUser(userEmail).equals(new Location(msg.field3))) {
					// Deliver the message straight away

				} else {
					Utils.queueMessage(msg.field2, msg.field3, new Location (msg.field4));
					ProbeManager.startProbeFor(msg.field2);
				}
			} else if (msg.msgType == Message.MsgType.LOGOFF_MSG) {
				/* handle log out */
				userEmail = msg.field2;
				if (!Utils.isUserOnline(userEmail)) {
					System.out.println("ERROR: You are not logged on yet so cannot log off");
				} else
					Utils.logUserOff(userEmail);
			}

			// TODO : figure out when to shut the server
		}
	}

	private boolean registeredUser(String userEmail) {
		return true;
	}

	private boolean checkPassword(String userEmail, String password) {
		return true;
	}

}
