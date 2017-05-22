import java.util.HashMap;

public class ProbeThread extends Thread {

	private String email = null;
	private static HashMap<String, ProbeThread> mThreadMapping = null;

	private ProbeThread(String eMail) {
		email = eMail;
	}

	public static void startThreadFor(String email) {
		if (mThreadMapping == null)
			mThreadMapping = new HashMap<String, ProbeThread>();

		if (!mThreadMapping.containsKey(email)) {
			ProbeThread mThread = new ProbeThread(email);
			mThreadMapping.put(email, mThread);
			mThread.start();
		}
	}

	@Override
	public void run() {
		while(true) {
			
		}
	}
	
}
