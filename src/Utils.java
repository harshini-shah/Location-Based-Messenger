import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {

	private static Map<String, User> onlineUsers;
	private static HashMap<String, ArrayList<QueueObject>> messageQueueBank = null;
	// private static Lock messageQueueRead = null, messageQueueWrite = null;

	public static void nullCheck() {
		if (onlineUsers == null)
			onlineUsers = new HashMap<String, User>();

		if(messageQueueBank == null)
			messageQueueBank = new HashMap<String, ArrayList<QueueObject>>();
	}

	public static boolean isUserOnline(String email) {
		nullCheck();
		return onlineUsers.containsKey(email);
	}

	public static void logUserOff(String email) {
		nullCheck();
		ProbeManager.closeProbeForUser(email);
		onlineUsers.remove(email);
	}

	public static void logUserOn(String email, User userObj) {
		nullCheck();
		onlineUsers.put(email, userObj);
	}

	public static int queueMessage(String userEmail, String message, Location loc) {
	    int id = getMessageIdFor(message);
	    queueMessage(userEmail, id, loc);
	    return id;
	}
	
	public static void queueMessage(String userEmail, int id, Location loc) {
	    
		ArrayList<QueueObject> queue = null;
		if (messageQueueBank.get(userEmail) == null)
			queue = new ArrayList<QueueObject>();
		else
			queue = messageQueueBank.get(userEmail);

		/*
		 * Need to implement Read/Write locks for this Reference -
		 * https://www.javacodegeeks.com/2012/04/java-concurrency-with-readwritelock.html
		 */
		QueueObject obj = new QueueObject(id, loc);
		queue.add(obj);
	}

	public static boolean messageQueueForUserExists(String userEmail) {
		return messageQueueBank.get(userEmail) != null && !messageQueueBank.get(userEmail).isEmpty();
	}

	public static boolean deliverAllPossibleMessages(String userEmail) {
		boolean delivered = false;
		ArrayList<QueueObject> messageQueue = getQueueForUser(userEmail);
		Location currentLocation = getCurrentLocationForUser(userEmail);
		for (int i = 0; i < messageQueue.size();) {
			QueueObject obj = messageQueue.get(i);
			if (obj.getLocation().equals(currentLocation)) {
				messageQueue.remove(i);
				delivered = true;
				/* Logic to deliver Message to client */
			} else
				i++;
		}
		return delivered;
	}

	private static ArrayList<QueueObject> getQueueForUser(String userEmail) {
		/*
		 * Need to implement Read/Write locks for this Reference -
		 * https://www.javacodegeeks.com/2012/04/java-concurrency-with-readwritelock.html
		 */
		return messageQueueBank.get(userEmail);
	}

	public static Location getCurrentLocationForUser(String userEmail) {
		/* This is dummy, we need to get location from TIPPERS at this point */
		nullCheck();
		return new Location("XYZ");
	}

	public static int getMessageIdFor(String message) {
		/* To implement Message bank here */
		return -1;
	}

	public static String getMessageForId(int id) {
		return "dummy";
	}
}