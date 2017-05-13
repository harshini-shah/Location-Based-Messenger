/*
 * Each user has a queue of pending messages that have to be delivered - each object in the queue
 * is an instance of this class. It contains the ID associated with the message (using which the contents
 * of the message can be retrieved from the database - this is only done if the location constraint is 
 * satisfied) and the location (which is checked and compared with the user's current location). 
 */
public class QueueObject {
    private int messageID;
    private Location location;
    
    public QueueObject(int messageID, Location location) {
        this.messageID = messageID;
        this.location = location;
    }
    
    public int getMessageID() {
        return messageID;
    }
    
    public Location getLocation() {
        return location;
    }
}
