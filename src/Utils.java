import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

	private static HashMap<String, ArrayList<QueueObject>> messageQueueBank = null;
	// private static Lock messageQueueRead = null, messageQueueWrite = null;

	public static void queueMessage(String userEmail, String message, Location loc) {
		if (messageQueueBank == null) {
			messageQueueBank = new HashMap<String, ArrayList<QueueObject>>();
		}

		ArrayList<QueueObject> queue = null;
		if (messageQueueBank.get(userEmail) == null)
			queue = new ArrayList<QueueObject>();
		else
			queue = messageQueueBank.get(userEmail);

		/*
		 * Need to implement Read/Write locks for this Reference -
		 * https://www.javacodegeeks.com/2012/04/java-concurrency-with-readwritelock.html
		 */
		QueueObject obj = new QueueObject(getMessageIdFor(message), loc);
		queue.add(obj);
	}

	public static ArrayList<QueueObject> getQueueForUser(String userEmail) {
		/*
		 * Need to implement Read/Write locks for this Reference -
		 * https://www.javacodegeeks.com/2012/04/java-concurrency-with-readwritelock.html
		 */
		if (messageQueueBank.get(userEmail) == null)
			return null;

		return messageQueueBank.get(userEmail);
	}

	public static Location getCurrentLocationForUser(String userEmail) {
		/* This is dummy, we need to get location from TIPPERS at this point */
		String abc = "XYZ";
		Location loc = new Location(abc);
		return loc;
	}

	public static int getMessageIdFor(String message) {
		/* To implement Message bank here */
		return -1;
	}

	public static String getMessageForId(int id) {
		return "dummy";
	}
}