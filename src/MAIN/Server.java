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

			try {
				msg = (Message) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (msg.msgType == Message.MsgType.LOGIN_MSG) {
				/*
				 * Handle a Login Required
				 * TODO : make sure that the user is "disconnected" after you close the connection
				 */

				userEmail = msg.field1;
				// Check that the user email is there in the DB
				if (!registeredUser(userEmail)) {
					/**
					 * CREATE MSG OBJ
					 * msgType = same as before
					 * f1 - harshini@uci.edu
					 * f2 - "FALSE"
					 * f4 - "ERROR: Email ID Not Registered on System"
					 */
				    Message message = new Message();
				    message.msgType = msg.msgType;
				    message.field1 = userEmail;
				    message.field2 = "FALSE";
					String invalidUserMsg = "ERROR: Email ID Not Registered on System";
					message.field4 = invalidUserMsg;
					out.writeObject(message);
					client.close();
					
					// Disconnect user
					continue;
				}

				String password = msg.field2;
				// Check that the passwords match
				if (!checkPassword(userEmail, password)) {
					/**
					 * CREATE MSG OBJ
					 * msgType = same as before
					 * f1 - harshini@uci.edu
					 * f2 - "FALSE"
					 * f4 - "ERROR: Wrong Password"
					 */
					Message message = new Message();
                    message.msgType = msg.msgType;
                    message.field1 = userEmail;
                    message.field2 = "FALSE";
                    String wrongPasswordMsg = "ERROR: Wrong Password";
                    message.field4 = wrongPasswordMsg;
                    out.writeObject(message);
					client.close();
					
					// Disconnect the user
					continue;
				}

				Utils.logUserOn(userEmail, new User(userEmail,client.getRemoteSocketAddress()));

				if (Utils.messageQueueForUserExists(userEmail)) {
					/**
					 * CREATE MSG OBJ
					 * msgType = same as before
					 * f1 - harshini@uci.edu
					 * f2 - "TRUE"
					 * f3 - "THIS IS MADHUR, HOW ARE YOU | THIS iS SHARAD, HOW ARE YOU"
					 * f4 - "madhur@uci.edu | sharad@uci.edu"
					 */
					ClientThread clientThread = new ClientThread(client, userEmail);
				} else {
					/**
					 * CREATE MSG OBJ
					 * msgType = same as before
					 * f1 - harshini@uci.edu
					 * f2 - "TRUE"
					 * f3 - null
					 * f4 - null
					 */
				    Message message = new Message();
                    message.msgType = msg.msgType;
                    message.field1 = userEmail;
                    message.field2 = "TRUE";
                    out.writeObject(message);
                    
                    // Disconnect the user
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
				    Utils.sendMessage(msg);
				    
				    // Disconnect user
				    client.close();
				} else {
				    int messageID = DatabaseInitialization.addTransaction(msg);
					Utils.queueMessage(msg.field2, msg.field3, new Location (msg.field4), messageID);
					ProbeManager.startProbeFor(msg.field2);
					
					// Disconnect user
					client.close();
				}
			} else if (msg.msgType == Message.MsgType.LOGOFF_MSG) {
				/* handle log out */
				userEmail = msg.field1;
				if (!Utils.isUserOnline(userEmail)) {
					System.out.println("ERROR: You are not logged on yet so cannot log off");
				} else
					Utils.logUserOff(userEmail);
				
				    // Disconnect user 
				    client.close();
			}

			// TODO : figure out when to shut the server
		}
	}

	private boolean registeredUser(String userEmail) {
	    return DatabaseInitialization.checkUser(userEmail);
	}

	private boolean checkPassword(String userEmail, String password) {
	    return DatabaseInitialization.checkPassword(userEmail);
	}

}
