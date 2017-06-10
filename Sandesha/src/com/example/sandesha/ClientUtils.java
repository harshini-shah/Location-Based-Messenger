package com.example.sandesha;

import java.util.ArrayList;

public class ClientUtils {
	public static ArrayList<String> mRoomNos;
	public static String userName;
	public static String SERVER_IP = "192.168.0.7";

	public static void shutDown() {
		if (mRoomNos != null)
			mRoomNos.clear();
		mRoomNos = null;
		userName = null;
	}
}
