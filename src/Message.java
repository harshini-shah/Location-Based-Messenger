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
    
    private static final long serialVersionUID = 42L;
    
    public enum MsgType {LOGIN_MSG, LOGOFF_MSG, SEND_MSG};
    
    MsgType msgType;
    String userEmail, password, messageText, receiverEmail;
    Location location;

    Message(MsgType msgType, String userEmail, String password, String receiverEmail, String messageText, Location loc) {
        this.msgType = msgType;
        this.userEmail = userEmail;
        this.password = password;
        this.receiverEmail = receiverEmail;
        this.messageText = messageText;
        this.location = loc;
    }
}
