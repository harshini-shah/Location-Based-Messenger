package MAIN;
import java.io.Serializable;

/* 
 * An object of this class is used whenever a client wants to communicate with the server.
 * The client can send three types of messages:
 * 
 * - LOGIN_MSG : When the client wants to log on to the system. The userEmail and password 
 * have to be sent, and the MsgType set appropriately.
 * - LOGOFF_MSG : The userEmail is sent and the MsgType set appropriately.
 * - SEND_MSG : The userEmail, receiverEmail, message text and the location 
 * constraint are sent and the MsgType set appropriately.
 * 
 * TODO : Assuming single recipient - have to add support for multiple users.
 */
public class Message implements Serializable {

	/* From mobile application
	 * "Hello" from Harshini to Vignesh
	 * msgType = SEND_MSG
	 * f1 - harshini@uci.edu
	 * f2 - vignesh@uci.edu
	 * f3 - "Hello"
	 * f4 - DBH 2055
	 * 
	 * from mobile application (Harshini is trying TO LOG IN)
	 * msgType = LOGIN_MSG
	 * f1 - harshini@uci.edu
	 * f2 - myPassword
	 * 
	 * from mobile application Harshini trying to log off
	 * msgType = LOG_OFF
	 * f1 - harshini@uci.edu
	 * 
	 * 
	 * 
	 * 
	 * */
	private static final long serialVersionUID = 42L;

	public enum MsgType {
		LOGIN_MSG, LOGOFF_MSG, SEND_MSG, NOTIFICATION
	};

	public MsgType msgType;
	public String field1;
	public String field2;
	public String field3;
	public String field4;
}