package MAIN;

import java.util.ArrayList;

public class Mercury {

	private static ArrayList<MercuryQueueObject> mRequestQueue = new ArrayList<MercuryQueueObject>();
	private static Object mutex = new Object();
	private static MercuryThread mBarry = null;

	private static class MercuryQueueObject {
		ArrayList<Integer> msgIds;
		Message msg;

		public MercuryQueueObject(ArrayList<Integer> ids, Message m) {
			msgIds = ids;
			msg = m;
		}
	}

	public static void addRequest(ArrayList<Integer> msgIdList, Message request) {

		synchronized (mutex) {
			mRequestQueue.add(new MercuryQueueObject(msgIdList, request));
			mutex.notifyAll();
		}

		if (mBarry == null) {
			mBarry = new MercuryThread();
			mBarry.start();
		}
	}

	private static class MercuryThread extends Thread {

		@Override
		public void run() {
			while (true) {
				synchronized (mutex) {
					while (mRequestQueue.isEmpty()) {
						try {
							mutex.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				MercuryQueueObject mMercuryObj = null;
				synchronized (mutex) {
					/* Read next message from the Queue */
					if (!mRequestQueue.isEmpty())
						mMercuryObj = mRequestQueue.remove(0);

					/* Release the lock */
					mutex.notifyAll();
				}

				if (mMercuryObj != null) {
					boolean delivered = Utils.sendMessage(mMercuryObj.msg);
					for (int i : mMercuryObj.msgIds)
						if (delivered && mMercuryObj.msg.msgType != Message.MsgType.ACK) {
							ProbeManager.addDeliveryNotice(mMercuryObj.msg.field1, i);
							String users[] = mMercuryObj.msg.field4.split("\\|");
							for (int x = 0; x < users.length; x++) {
								Message msg = new Message();
								msg.msgType = Message.MsgType.ACK;
								msg.field1 = "SERVER";
								msg.field2 = users[x].trim();
								msg.field3 = "Message " + mMercuryObj.msg.field3 + " delivered to "
										+ mMercuryObj.msg.field1;
								Utils.queueMessage(msg.field2, null, DBUtils.addTransaction(msg));
								ProbeManager.startProbeFor(msg.field2);
							}
						} else if (mMercuryObj.msg.msgType != Message.MsgType.ACK)
							ProbeManager.addUndeliveredNotice(mMercuryObj.msg.field1, i);
				}
			}
		}
	}
}