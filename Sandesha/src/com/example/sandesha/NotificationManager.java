package com.example.sandesha;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import MAIN.Message;
import MAIN.Utils;
import android.os.Handler;

public class NotificationManager extends Thread {

	private String user_id;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private boolean keepAlive;
	private Handler mHandler;

	public NotificationManager(String user_id, Handler h) {
		this.user_id = user_id;
		keepAlive = true;
		mHandler = h;
	}

	public void down() {
		try {
			wait.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		keepAlive = false;
	}

	private ServerSocket wait = null;

	@Override
	public void run() {

		System.out.println("Client Receive is ONLINE WAITING");
		try {
			wait = new ServerSocket(Utils.CLIENT_PORT_NUMBER);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (keepAlive) {
			try {
				Socket test_socket = wait.accept();
				android.os.Message androidMsg = android.os.Message.obtain();
				System.out.println("Client Receiver got request from Server Yayy ~!!");
				outputStream = new ObjectOutputStream(test_socket.getOutputStream());
				inputStream = new ObjectInputStream(test_socket.getInputStream());
				Message fromServer = (Message) inputStream.readObject();

				if (fromServer.msgType == Message.MsgType.NOTIFICATION && fromServer.field1.equals(user_id)) {
					String[] msg_split = fromServer.field3.split("\\|");
					String[] sender_split = fromServer.field4.split("\\|");
					String fS ="";
					for (int i = 0; i < msg_split.length; i++) {
						fS += "MESSAGE = " + msg_split[i]+"\n"+"FROM = " + sender_split[i];
						System.out.println("MESSAGE = " + msg_split[i]);
						System.out.println("FROM = " + sender_split[i]);
					}
					androidMsg.obj = fS;
					androidMsg.what = 1;
				} else if (fromServer.msgType == Message.MsgType.ACK && fromServer.field1.equals(user_id)) {
					androidMsg.obj = "\nACK = " + fromServer.field3;
					androidMsg.what = 1;
					System.out.println("ACK = " + fromServer.field3);
				} else
					System.out.println("We have an unrecognized message from Server " + fromServer);
				mHandler.sendMessage(androidMsg);
			} catch (SocketException se) {
				System.out.println("Asked to Kill Client Recieve !");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				outputStream = null;
				inputStream = null;
			}
		}
	}
}