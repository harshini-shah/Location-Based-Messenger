package MAIN;
import java.util.HashMap;

public class ProbeManager {

	private static HashMap<String, ProbeThread> mThreadMapping = null;

	public static void startProbeFor(String email) {
		if (mThreadMapping == null)
			mThreadMapping = new HashMap<String, ProbeThread>();

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

	private static class ProbeThread extends Thread {
		private String email = null;

		private ProbeThread(String eMail) {
			email = eMail;
		}

		@Override
		public void run() {
			while (Utils.messageQueueForUserExists(email)) {
				try {
					Utils.deliverAllPossibleMessages(email);
					Thread.sleep(150);
				} catch (InterruptedException E) {
					if (!Utils.isUserOnline(email))
						break;
					else
						System.out.print(E);
				}
			}

			/* Closing Probe */
			mThreadMapping.remove(email);
		}
	}
}
