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
 *  TODO : All synchronization is left
 *  TODO : Flush everytime you write
 */
public class Server {

	public Server(int port) throws Exception {
		/* Creates a listening */
	    //DBUtils.createConnection();
	    DBUtils.cleanup();
	    DBUtils.createDatabase();
	    DBUtils.populateUsersTable("src/Test/DummyUsers.csv");
        DBUtils.populateDummyUsersTable("src/Test/Dummy_users.csv");
        DBUtils.createTransactionsTable();
 
        String roomNos = "TRUE|"+Utils.getRoomNos();

		ServerSocket server = new ServerSocket(port);
		//server.setSoTimeout(700000);

		while (true) {
			Socket client = server.accept(); // Accepts connection request from
												// client

			System.out.println("Accepted connection request from " + client.getInetAddress()); // Prints

			// Get the input and output streams for the socket
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
				if(Utils.isUserOnline(msg.field1))
					Utils.logUserOn(userEmail, new User(userEmail,client.getInetAddress(), Utils.CLIENT_PORT_NUMBER));
			} else if (msg.msgType == Message.MsgType.LOGIN_MSG) {
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
					out.flush();
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
                    out.flush();
					client.close();
					
					continue;
				}

				Utils.logUserOn(userEmail, new User(userEmail,client.getInetAddress(), Utils.CLIENT_PORT_NUMBER));

				boolean sendEmptyAffirmation = false;
				if (Utils.messageQueueForUserExists(userEmail)) {
					/**
					 * CREATE MSG OBJ
					 * msgType = same as before
					 * f1 - harshini@uci.edu
					 * f2 - "TRUE | room nos "
					 * f3 - "THIS IS MADHUR, HOW ARE YOU | THIS iS SHARAD, HOW ARE YOU"
					 * f4 - "madhur@uci.edu | sharad@uci.edu"
					 */
					ArrayList<Integer> messageIdList = Utils.getAllDeliverableMessages(userEmail);
					if (messageIdList == null || messageIdList.isEmpty())
						sendEmptyAffirmation = true;
					else {
						Message reply = DBUtils.getMessagesFromDB(messageIdList,userEmail);
						
						reply.msgType = Message.MsgType.LOGIN_MSG;
						reply.field2 = roomNos;
						out.writeObject(reply);
						out.flush();
						client.close();
					}
				} else
					sendEmptyAffirmation = true;

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
			} else if (msg.msgType == Message.MsgType.SEND_MSG) {
				/* handle sending a new message */
				userEmail = msg.field1;
				if (!Utils.isUserOnline(userEmail)) {
					System.out.println("ERROR: You are not logged on yet so cannot send a message");
				}
				
				String recipientEmail = msg.field2;
				
				// Check if the user is online and the location is a match
				if (Utils.isUserOnline(recipientEmail) && Utils.getCurrentLocationForUser(recipientEmail).equals(new Location(msg.field4))) {
				    msg.msgType = Message.MsgType.NOTIFICATION;
			        msg.field4 = userEmail;
			        msg.field1 = recipientEmail;
			        msg.field2 = null;
				    Utils.sendMessage(msg);
				    client.close();
				} else {
				    int messageID = DBUtils.addTransaction(msg);
					Utils.queueMessage(msg.field2, new Location (msg.field4), messageID);
					ProbeManager.startProbeFor(msg.field2);
					client.close();
				}
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

	private boolean registeredUser(String userEmail) {
	    return DBUtils.checkUser(userEmail);
	}

	private boolean checkPassword(String userEmail, String password) {
	    return DBUtils.checkPassword(userEmail, password);
	}
	
	public static void main(String[] args) throws Exception {
	    Server server = new Server(Utils.SERVER_PORT_NUMBER);
	}

}
