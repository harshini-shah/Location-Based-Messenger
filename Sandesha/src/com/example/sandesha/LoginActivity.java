package com.example.sandesha;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import MAIN.Message;
import MAIN.Message.MsgType;
import MAIN.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		Button b = (Button) findViewById(R.id.login);
		final EditText usrName = (EditText) findViewById(R.id.username);
		final EditText passwrd = (EditText) findViewById(R.id.password);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (handleLogIn(usrName.getText().toString(), passwrd.getText().toString())) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), MessageBoard.class);
					startActivity(intent);
					finish();
				} else
					Toast.makeText(getApplicationContext(), "INVALID LOGIN CREDENTIALS", Toast.LENGTH_LONG).show();
			}
		});
	}

	private boolean handleLogIn(String user_id, String password) {
		System.out.println("Inside Handle Login");
		Message msg = new Message();
		Message fromServer = null;
		msg.msgType = MsgType.LOGIN_MSG;
		msg.field1 = user_id;
		msg.field2 = password;
		Socket test_socket;
		try {
			//System.out.println("Inside Try");
			test_socket = new Socket(ClientUtils.SERVER_IP, Utils.SERVER_PORT_NUMBER);
			ObjectOutputStream outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(test_socket.getInputStream());
			outputStream.writeObject(msg);
			outputStream.flush();
			fromServer = (Message) inputStream.readObject();
		} catch (UnknownHostException e) {
			//System.out.println("Inside Case 1");
			//Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			//System.out.println("Inside Case 2");
			e.printStackTrace();
			//Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			return false;
		} catch (ClassNotFoundException e) {
//			System.out.println("Inside Case 3");
//			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		}

		System.out.println("Inside Case 4");
		String[] roomNos = fromServer.field2.split("\\|");
		String auth = roomNos[0].trim();

		if (auth.equals("FALSE")) {
			System.out.println("Inside Case 5");
			return false;
		}

		ClientUtils.mRoomNos = new ArrayList<String>(Arrays.asList(roomNos));
		ClientUtils.mRoomNos.remove(0);
		Collections.sort(ClientUtils.mRoomNos);
		ClientUtils.userName = user_id.trim();
		return true;
	}
}