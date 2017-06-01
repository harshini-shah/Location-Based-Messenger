package MAIN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static int SERVER_PORT_NUMBER = 6066;
	public static int CLIENT_PORT_NUMBER = 6068;
	private static String prefix = "\"name\":\"";
	private static char termination = '\"';
	private static ArrayList<String> roomLinks = new ArrayList<String>(Arrays.asList(
			"http://sensoria.ics.uci.edu:8001/infrastructure/get?floor=1",
			"http://sensoria.ics.uci.edu:8001/infrastructure/get?floor=2",
			"http://sensoria.ics.uci.edu:8001/infrastructure/get?floor=3",
			"http://sensoria.ics.uci.edu:8001/infrastructure/get?floor=4",
			"http://sensoria.ics.uci.edu:8001/infrastructure/get?floor=5",
			"http://sensoria.ics.uci.edu:8001/infrastructure/get?floor=6"));
	private static Map<String, User> onlineUsers;
	private static HashMap<String, Object> mutexBank = null;
	private static HashMap<String, ArrayList<QueueObject>> messageQueueBank = null;

	public static void nullCheck() {
		if (onlineUsers == null)
			onlineUsers = new HashMap<String, User>();

		if (messageQueueBank == null)
			messageQueueBank = new HashMap<String, ArrayList<QueueObject>>();

		if (mutexBank == null)
			mutexBank = new HashMap<String, Object>();
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

	public static void queueMessage(String userEmail, Location loc, int messageID) {

		QueueObject obj = new QueueObject(messageID, loc);
		ArrayList<QueueObject> queue = null;
		Object messageQueueMutex = null;
		if (messageQueueBank.get(userEmail) == null) {
			queue = new ArrayList<QueueObject>();
			messageQueueBank.put(userEmail, queue);
			messageQueueMutex = new Object();
			mutexBank.put(userEmail, messageQueueMutex);
		} else {
			queue = getQueueForUser(userEmail);
			messageQueueMutex = getMutexForUser(userEmail);
		}

		synchronized (messageQueueMutex) {
			queue.add(obj);
			messageQueueMutex.notifyAll();
		}
	}

	public static boolean messageQueueForUserExists(String userEmail) {
		return messageQueueBank.get(userEmail) != null && !messageQueueBank.get(userEmail).isEmpty();
	}
	
	public static Location getCurrentLocationForUser(String userEmail) {
	    return DBUtils.getCurrentLocationForUser(userEmail);
	}

	public static ArrayList<Integer> deliverAllPossibleMessages(String userEmail, boolean shouldIDeliver) {
		boolean delivered = false;
		ArrayList<QueueObject> messageQueue = getQueueForUser(userEmail);
		Object messageQueueMutex = getMutexForUser(userEmail);
		Location currentLocation = getCurrentLocationForUser(userEmail);
		ArrayList<Integer> messageIdList = new ArrayList<Integer>();

		synchronized (messageQueueMutex) {
			for (int i = 0; i < messageQueue.size();) {
				QueueObject obj = messageQueue.get(i);
				if (obj.getLocation().equals(currentLocation)) {
					messageIdList.add(messageQueue.remove(i).getMessageID());
					delivered = true;
				} else
					i++;
			}

			messageQueueMutex.notifyAll();
		}

		if (delivered) {
			if(!shouldIDeliver)
				return messageIdList;

			Message msg = DBUtils.getMessagesFromDB(messageIdList, userEmail); 
			Mercury.addRequest(msg);
		}
		return null;
	}

	private static Object getMutexForUser(String userEmail) {
		return mutexBank.get(userEmail);
	}

	private static ArrayList<QueueObject> getQueueForUser(String userEmail) {
		/*
		 * Need to implement Read/Write locks for this Reference -
		 * https://www.javacodegeeks.com/2012/04/java-concurrency-with-
		 * readwritelock.html
		 */
		return messageQueueBank.get(userEmail);
	}
	
	/*
	 * Can be called from two places:
	 * - From the server, if A sends a message to B, and B is online and the location matches. In
	 * this case, the message has to be formatted accordingly and sent
	 * - From the mercury thread which in turn is called by the probe thread. In this case, the
	 * message is got from the database and already properly formatted.
	 * 
	 */
	public static boolean sendMessage(Message message) {
		try {
			Socket socket = new Socket(onlineUsers.get(message.field1).ipAddress, CLIENT_PORT_NUMBER);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

			out.writeObject(message);
			out.flush();
			out.close();
		} catch (ConnectException e) {
			logUserOff(message.field1);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String getRoomNos() throws Exception {
		String list = "";
		for (String link : roomLinks)
			list = Utils.extractTokensFromUrl(link, prefix, termination, list);
		return list;
	}

	private static String extractTokensFromUrl(String url, String prefix, char termination, String list)
			throws IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String response = rd.readLine();
			int state = 0;
			String token = "";
			for (int i = 0; i < response.length(); i++) {
				if (state == prefix.length()) {
					if (response.charAt(i) == termination) {
						if(list.length() == 0)
							list = token;
						else 
							list += "|"+token;
						state = 0;
						token = "";
					} else
						token += response.charAt(i);
				} else if (response.charAt(i) == prefix.charAt(state))
					state++;
				else {
					state = 0;
					token = "";
				}
			}
		} finally {
			is.close();
		}
		return list;
	}
}