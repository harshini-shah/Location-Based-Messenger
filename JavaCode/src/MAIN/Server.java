package MAIN;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Starts the server on the specified port number. It listens on the port for connection requests (log in) from 
 * clients, and accepts them. It then prints the client address with whom it is connected. Next, it asks for the 
 * username and password, verifies that username is in the database, and that the username and passwords match.
 * It then spawns a thread (instance of the ClientThread class) to handle it.
 * 
 */
public class Server {

	public Server(int port) throws Exception {
		/* Creates a listening */

	    DBUtils.cleanup();
	    DBUtils.createDatabase();
	    DBUtils.populateUsersTable("src/Test/DummyUsers.csv");
        DBUtils.populateDummyUsersTable("src/Test/Dummy_users.csv");
        DBUtils.createTransactionsTable();

		String roomNos = "TRUE | " + Utils.getRoomNos();

		ServerSocket server = new ServerSocket(port);

		while (true) {
			Socket client = server.accept();
			System.out.println("Accepted connection request from " + client.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());			
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			
			Message msg = null;
			String userEmail = null;
			
			try {
				msg = (Message) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (msg.msgType == Message.MsgType.IP_UPDATE) {
				client.close();

				/* Case to update the IP of the User */
				userEmail = msg.field1;
				if (Utils.isUserOnline(msg.field1))
					Utils.updateIPForUser(userEmail, client.getInetAddress());
				else
					System.out.println("ERROR: Offline User trying to UPDATE IP");

			} else if (msg.msgType == Message.MsgType.LOGIN_MSG) {
				/*
				 * Handle a Login Required
				 * TODO : make sure that the user is "disconnected" after you close the connection
				 */
			    userEmail = msg.field1;
			    checkUser(msg);
			    if (msg.field2.equals("FALSE")) {
			        out.writeObject(msg);
                    out.flush();
                    client.close();
				} else {
					Utils.logUserOn(userEmail, new User(userEmail, client.getInetAddress()));
					boolean sendEmptyAffirmation = true;
	                if (sendEmptyAffirmation) {
	                    /**
	                     * CREATE MSG OBJ msgType = same as before
	                     * f1 - harshini@uci.edu
	                     * f2 - "TRUE | room nos" 
	                     * f3 - null 
	                     * f4 - null
	                     */
	                    msg.field2 = roomNos;
	                    out.writeObject(msg);
	                    out.flush();
	                    client.close();
	                }
	                ProbeManager.startProbeFor(userEmail);
			    }
			} else if (msg.msgType == Message.MsgType.SEND_MSG) {
				/* handle sending a new message */
				userEmail = msg.field1;
				if (!Utils.isUserOnline(userEmail)) {
					System.out.println("ERROR: You are not logged on yet so cannot send a message");
				}
				
				// Add to database
				int messageID = DBUtils.addTransaction(msg);
				
				// Field 2 has the receiver email IDs - Check if there are multiple ones
			    String[] recipients = msg.field2.split("\\|");
			    for (int i = 0; i < recipients.length; i++) {
			        Utils.queueMessage(recipients[i].trim(), new Location(msg.field4), messageID);
			        ProbeManager.startProbeFor(recipients[i].trim());
			    }
				client.close();

			} else if (msg.msgType == Message.MsgType.LOGOFF_MSG) {
				/* handle log out */
				userEmail = msg.field1;
				
				if (!Utils.isUserOnline(userEmail)) {
					System.out.println("ERROR: You are not logged on yet so cannot log off");
				} else {
					Utils.logUserOff(userEmail);
					Message message = new Message();
                    message.msgType = msg.msgType;
                    message.field1 = userEmail;
                    message.field2 = "TRUE";
                    String logoffMsg = "Goodbye! Have a nice day.";
                    message.field4 = logoffMsg;
                    out.writeObject(message);
                    out.flush();
				    client.close();
				    ProbeManager.closeProbeForUser(userEmail);
				}
			}
			// TODO : figure out when to shut the server
		}
	}

	private Message checkUser(Message message) {
	    String userEmail = message.field1;
	    String password = message.field2;
	    
	    String correctPassword = DBUtils.checkUser(userEmail);	    
	    if (correctPassword == null) {
	        message.field2 = "FALSE";
	        message.field4 = "ERROR: Email ID Not Registered on System";
	    } else if (!correctPassword.equals(password)) {
	        message.field2 = "FALSE";
	        message.field4 = "ERROR: Wrong Password";
	    } else {
	        message.field2 = "TRUE";
	    }
	    
	    return message;
	}

	public static void main(String[] args) throws Exception {
	    Server server = new Server(Utils.SERVER_PORT_NUMBER);
	}

}
