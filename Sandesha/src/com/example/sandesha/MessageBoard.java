package com.example.sandesha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MessageBoard extends Activity {

	private NotificationManager nManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_board);

		TextView displayMessageLabel = (TextView) findViewById(R.id.inputMessageLabel);
		nManager = new NotificationManager(getIntent().getStringExtra(ClientUtils.USER_NAME), displayMessageLabel);
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
			/* Log OFF Module */
			nManager.down();
			ClientUtils.shutDown();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
