package Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TestSocketServer extends Thread {
	private ServerSocket serverSocket;

	public TestSocketServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}

	public void run() {
		boolean flag = true;
		while (flag) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket client = serverSocket.accept();

				System.out.println("I am server; Just connected to " + client.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(client.getInputStream());

				System.out.println("Iam server, I got this message; \n" + in.readUTF());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				out.writeUTF(
						"I am server; Thank you for connecting to " + client.getLocalSocketAddress() + "\nGoodbye!");
				System.out.println("Iam server, I got this message2; \n" + in.readUTF());
				client.close();

				flag = false;

			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		int port = 6066;// Integer.parseInt(args[0]);
		try {
			Thread t = new TestSocketServer(port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}