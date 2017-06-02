
package MAIN;

import java.util.ArrayList;
import java.util.HashMap;

public class ProbeManager {

	private static HashMap<String, ArrayList<Integer>> mReserveDelivered = null;
	private static HashMap<String, ArrayList<Integer>> mReserveUnDelivered = null;
	private static HashMap<String, ProbeThread> mThreadMapping = null;

	private static void nullCheck() {
		if (mReserveDelivered == null)
			mReserveDelivered = new HashMap<String, ArrayList<Integer>>();

		if (mReserveUnDelivered == null)
			mReserveUnDelivered = new HashMap<String, ArrayList<Integer>>();

		if (mThreadMapping == null)
			mThreadMapping = new HashMap<String, ProbeThread>();
	}

	public static void startProbeFor(String email) {
		nullCheck();

		if (!mThreadMapping.containsKey(email) && Utils.isUserOnline(email)) {
			ProbeThread mThread = new ProbeThread(email);
			mThreadMapping.put(email, mThread);
			mThread.start();
		}
	}

	public static void closeProbeForUser(String email) {
		if (mThreadMapping == null || !mThreadMapping.containsKey(email))
			return;

		ProbeThread probe = mThreadMapping.get(email);
		probe.interrupt();
		try {
			probe.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void addUndeliveredNotice(String email, int id) {
		nullCheck();

		if(!mThreadMapping.containsKey(email)) {
			ArrayList<Integer> undelivered = null;
			if(!mReserveUnDelivered.containsKey(email)) {
				undelivered = new ArrayList<Integer>();
				undelivered.add(id);
				mReserveUnDelivered.put(email,undelivered);
			} else {
				undelivered = mReserveUnDelivered.get(email);
				synchronized (undelivered) {
					undelivered.add(id);
					undelivered.notifyAll();
				}
			}
			return;
		}

		mThreadMapping.get(email).addUndeliveredNotice(id);
	}

	public static void addDeliveryNotice(String email, int id) {
		nullCheck();

		if (!mThreadMapping.containsKey(email)) {
			ArrayList<Integer> delivered = null;
			if (!mReserveDelivered.containsKey(email)) {
				delivered = new ArrayList<Integer>();
				delivered.add(id);
				mReserveDelivered.put(email, delivered);
			} else {
				delivered = mReserveDelivered.get(email);
				synchronized (delivered) {
					delivered.add(id);
					delivered.notifyAll();
				}
			}
			return;
		}

		mThreadMapping.get(email).addDeliveryNotice(id);
	}

	private static class ProbeThread extends Thread {
		private String email = null;
		private ArrayList<Integer> deliveredMessageList = null, unDeliveredMessageList = null;

		private ProbeThread(String eMail) {
			email = eMail;

			if (mReserveUnDelivered.containsKey(email))
				unDeliveredMessageList = mReserveUnDelivered.get(email);
			else
				unDeliveredMessageList = new ArrayList<Integer>();

			if (mReserveDelivered.containsKey(email))
				deliveredMessageList = mReserveDelivered.remove(email);
			else
				deliveredMessageList = new ArrayList<Integer>();
		}

		public void addDeliveryNotice(int id) {
			synchronized (deliveredMessageList) {
				deliveredMessageList.add(id);
				deliveredMessageList.notifyAll();
			}
		}

		public void addUndeliveredNotice(int id) {
			synchronized (unDeliveredMessageList) {
				unDeliveredMessageList.add(id);
				unDeliveredMessageList.notifyAll();
			}
		}

		@Override
		public void run() {
			System.out.println("Probe running for " + email);
			while (Utils.messageQueueForUserExists(email)) {
				try {
					Utils.deliverAllPossibleMessages(email, true, deliveredMessageList, unDeliveredMessageList);
					Thread.sleep(150);
				} catch (InterruptedException E) {
					if (!Utils.isUserOnline(email))
						break;
					else
						System.out.print(E);
				}
			}
			System.out.println("Probe Closing for " + email);
			/* Closing Probe */
			mThreadMapping.remove(email);
		}
	}
}
