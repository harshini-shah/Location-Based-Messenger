package ClientCode;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPObserver extends Thread {

	private InetAddress mInetAddress = null;
	private Client mClient;
	private boolean isAlive;

	IPObserver(Client client, InetAddress ip) {
		mInetAddress = ip;
		mClient = client;
		isAlive = true;
	}

	public void down() {
		isAlive = false;
		interrupt();
	}

	@Override
	public void run() {
		try {
			while (isAlive) {
				if (!InetAddress.getLocalHost().equals(mInetAddress))
					mClient.ipUpdated();
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			isAlive = false;
			System.out.println("Its time to Die");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
