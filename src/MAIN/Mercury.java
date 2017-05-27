package MAIN;

import java.util.ArrayList;

public class Mercury {

	private static ArrayList<Message> mRequestQueue = new ArrayList<Message>();
	private static Object mutex = new Object();
	private static MercuryThread mBarry = null;

	public static void addRequest(Message request) {
		synchronized (mutex) {
			mRequestQueue.add(request);
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

				Message msg = null;
				synchronized (mutex) {
					/* Read next message from the Queue */
					if (!mRequestQueue.isEmpty())
						msg = mRequestQueue.remove(0);

					/* Release the lock */
					mutex.notifyAll();
				}

				if (msg != null) {
				    Utils.sendMessage(msg);
				}
			}
		}

	}
}