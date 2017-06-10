package MAIN;
/*
 * Each user has a queue of pending messages that have to be delivered - each object in the queue
 * is an instance of this class. It contains the ID associated with the message (using which the contents
 * of the message can be retrieved from the database - this is only done if the location constraint is 
 * satisfied), a counter of the number of times the object has been pinged by the Probe Thread (initially 
 * set to 100 and decremented with each ping) and the location (which is checked and compared with the 
 * user's current location). 
 */
public class QueueObject {
    private int messageID;
    private Location location;
    private int probePings;
    
    public QueueObject(int messageID, Location location) {
        this.messageID = messageID;
        this.location = location;
        this.probePings = 100;
    }
    
    public int getMessageID() {
        return messageID;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public int getProbePings() {
        return probePings;
    }
}
