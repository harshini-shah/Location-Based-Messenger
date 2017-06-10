package com.example.sandesha;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import MAIN.Message;
import MAIN.Message.MsgType;
import MAIN.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MessageBoard extends Activity {

	private NotificationManager nManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_board);

		final TextView displayMessageLabel = (TextView) findViewById(R.id.inputMessageLabel);
		Handler mHandler = new Handler(Looper.getMainLooper()) {

			@Override
			public void handleMessage(android.os.Message inputMessage) {
				if (inputMessage.what != 1)
					return;

				String text = displayMessageLabel.getText().toString().trim();
				text += "\n" + (String) inputMessage.obj;
				displayMessageLabel.setText(text);
			}
		};

		nManager = new NotificationManager(ClientUtils.userName, mHandler);
		nManager.start();
		Button send = (Button) findViewById(R.id.sendMessageButton);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MessageSender.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.log_out:

			Message msg = new Message();
			msg.msgType = MsgType.LOGOFF_MSG;
			msg.field1 = ClientUtils.userName.trim();
			Socket test_socket;
			try {
				test_socket = new Socket(ClientUtils.SERVER_IP, Utils.SERVER_PORT_NUMBER);
				ObjectOutputStream outputStream = new ObjectOutputStream(test_socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(test_socket.getInputStream());
				outputStream.writeObject(msg);
				outputStream.flush();
				msg = (Message) inputStream.readObject();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			/* Wait for the server to ACK and ONLY then die */
			Toast.makeText(getApplicationContext(), "Thanks for your Interaction", Toast.LENGTH_LONG).show();
			System.out.println("Thanks for your Interaction");
			nManager.down();
			ClientUtils.shutDown();
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
