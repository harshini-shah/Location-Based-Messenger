package com.example.sandesha;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import ClientCode.Client;
import MAIN.Message;
import MAIN.Message.MsgType;
import MAIN.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		Button b = (Button) findViewById(R.id.login);
		final EditText usrName = (EditText) findViewById(R.id.username);
		final EditText passwrd = (EditText) findViewById(R.id.password);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), usrName.getText().toString(), Toast.LENGTH_LONG).show();
				if (handleLogIn(usrName.getText().toString(), passwrd.getText().toString())) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), MessageBoard.class);
					intent.putExtra(ClientUtils.USER_NAME, usrName.getText().toString());
					startActivity(intent);
					finish();
				}
			}
		});
	}

	private boolean handleLogIn(String user_id, String password) {
		Message msg = new Message();
		Message fromServer = null;
		msg.msgType = MsgType.LOGIN_MSG;
		msg.field1 = user_id;
		msg.field2 = password;
		Socket test_socket;
		try {
			test_socket = new Socket(ClientUtils.SERVER_IP, Utils.SERVER_PORT_NUMBER);
			ObjectOutputStream outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(test_socket.getInputStream());
			outputStream.writeObject(msg);
			outputStream.flush();
			fromServer = (Message) inputStream.readObject();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		String[] roomNos = fromServer.field2.split("\\|");
		String auth = roomNos[0].trim();

		if (auth.equals("FALSE"))
			return false;

		ClientUtils.mRoomNos = new ArrayList<String>(Arrays.asList(roomNos));
		ClientUtils.mRoomNos.remove(0);
		ClientUtils.userName = user_id;
		return true;
	}
}