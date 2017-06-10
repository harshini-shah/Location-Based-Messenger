package com.example.sandesha;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import MAIN.Message;
import MAIN.Utils;
import MAIN.Message.MsgType;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MessageSender extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_sender);

		Button send = (Button) findViewById(R.id.send);
		final EditText message = (EditText) findViewById(R.id.message);
		final Spinner mRoomNos = (Spinner) findViewById(R.id.locationSpinner);
		final EditText mReceiptentNames = (EditText) findViewById(R.id.toBeSentNames);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				ClientUtils.mRoomNos);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mRoomNos.setAdapter(adapter);

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String roomNo = mRoomNos.getSelectedItem().toString();
				String users = mReceiptentNames.getText().toString();
				System.out.println(roomNo + "  " + users);
				sendMessage(roomNo, users, message.getText().toString());
			}
		});
	}

	private void sendMessage(String roomNo, String users, String text_msg) {
		Message msg = new Message();
		String temp[] = users.split(",");
		String r_id = temp[0];

		for (int i = 1; i < temp.length; i++)
			r_id = " | " + temp[i];

		msg.msgType = MsgType.SEND_MSG;
		msg.field1 = ClientUtils.userName;
		msg.field2 = r_id;
		msg.field3 = text_msg;
		msg.field4 = roomNo;
		
		Socket test_socket;
		try {
			test_socket = new Socket(ClientUtils.SERVER_IP, Utils.SERVER_PORT_NUMBER);
			ObjectOutputStream outputStream = new ObjectOutputStream(test_socket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(test_socket.getInputStream());
			outputStream.writeObject(msg);
			outputStream.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}