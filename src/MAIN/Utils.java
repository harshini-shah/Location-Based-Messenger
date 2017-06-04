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
import MAIN.QueueObject.STATUS;
import MAIN.Location.Distance;

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
		if(loc == null)
			obj.updateStatus(QueueObject.STATUS.ACK);

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

	public static ArrayList<Integer> getAllDeliverableMessages(String userEmail) {
		return deliverAllPossibleMessages(userEmail, false, null, null, null);
	}

	public static ArrayList<Integer> deliverAllPossibleMessages(String userEmail, boolean shouldIDeliver,
			ArrayList<Integer> deliveredList, ArrayList<Integer> unDeliveredList, Location.Distance distance) {
		boolean delivered = false;
		ArrayList<QueueObject> messageQueue = getQueueForUser(userEmail);
		Object messageQueueMutex = getMutexForUser(userEmail);
		Location currentLocation = getCurrentLocationForUser(userEmail);
		ArrayList<Integer> messageIdList = new ArrayList<Integer>();
		ArrayList<Integer> ackMessages = new ArrayList<Integer>();

		synchronized (messageQueueMutex) {
			for (int i = 0; i < messageQueue.size();) {
				QueueObject obj = messageQueue.get(i);

				if (obj.getStatus() == STATUS.ACK) {
					messageQueue.remove(obj);
					ackMessages.add(obj.getMessageID());
				} else if (deliveredList != null && obj.getStatus() == STATUS.WAITING) {
					synchronized (deliveredList) {
						if (deliveredList.contains(obj.getMessageID())) {
							messageQueue.remove(obj);
							deliveredList.remove((Integer) obj.getMessageID());
							deliveredList.notifyAll();
						} else
							i++;
					}
				} else if (unDeliveredList != null && obj.getStatus() == STATUS.WAITING) {
					synchronized (unDeliveredList) {
						if (unDeliveredList.contains(obj.getMessageID())) {
							obj.updateStatus(STATUS.TOBESENT);
							unDeliveredList.remove((Integer) obj.getMessageID());
							unDeliveredList.notifyAll();
						} else
							i++;
					}
				} else if (obj.getStatus() == STATUS.TOBESENT && obj.getLocation().equals(currentLocation)) {
					messageIdList.add(obj.getMessageID());
					obj.updateStatus(STATUS.WAITING);
					delivered = true;
				} else if (!shouldIDeliver && obj.getStatus() == STATUS.TOBESENT && !obj.decrementProbe()) {
					/* Probe Count is over, need to drop the message; probably
					 * need to send Message to Original Sender that this message
					 * hasn't been delivered */
					messageQueue.remove(obj);
				} else {
					if (!shouldIDeliver && distance != null)
						Utils.updateDistance(distance, obj.getLocation().getDistance(currentLocation));

					i++;
				}

				messageQueueMutex.notifyAll();
			}
		}

		if (delivered) {
			Message msg = DBUtils.getMessagesFromDB(ackMessages, userEmail); 
			Mercury.addRequest(messageIdList, msg);
			
			if(!shouldIDeliver)
				return messageIdList;

			Message msg1 = DBUtils.getMessagesFromDB(messageIdList, userEmail); 
			Mercury.addRequest(messageIdList, msg1);
		}
		return null;
	}

	private static void updateDistance(Distance finalDistance, Distance currDistance) {
        if (finalDistance.compareTo(currDistance) < 0) {
            finalDistance = currDistance;
        }
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
	 * The acknowledgement is a Message object with field 2 set to true or false depending on 
	 * whether the message was received or not.
	 */
	public static boolean sendMessage(Message message) {
		Socket socket = null;
		try {
			socket = new Socket(onlineUsers.get(message.field1).ipAddress, CLIENT_PORT_NUMBER);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

			out.writeObject(message);
			out.flush();
		} catch (ConnectException e) {
			System.out.println("ConnectException: Send message failed");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("IOException: Send message failed");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.out.println("Exception: Send message failed");
			e.printStackTrace();
			return false;
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
							list += " | "+token;
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

	/*
	 * Provides a mapping of the distance metric to the sleep time.
	 */
    public static long getSleepTime(Distance distance) {
        switch(distance) {
        case VERY_NEAR:
            return 150L;
        case NEAR:
            return 150L;
        case FAR:
            return 150L;
        case VERY_FAR:
            return 150L;
        }
        
        return 150L;
    }
}