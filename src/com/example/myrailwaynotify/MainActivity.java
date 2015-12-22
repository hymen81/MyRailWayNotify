package com.example.myrailwaynotify;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button startServiceBtn, endServiceBtn;
	private ListView listView;
	// private ArrayAdapter adapter;
	private TextView fromToText;
	public static Handler handler;
	private ArrayList<String[]> rwRes = null;
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startServiceBtn = (Button) findViewById(R.id.startService);
		endServiceBtn = (Button) findViewById(R.id.endService);
		listView = (ListView) findViewById(R.id.listView);
		fromToText = (TextView) findViewById(R.id.fromTo);
		startServiceBtn.setOnClickListener(startClickListener);
		endServiceBtn.setOnClickListener(stopClickListener);
		dialog = new ProgressDialog(MainActivity.this, R.style.ProgressBar);
		handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					rwRes = (ArrayList<String[]>) msg.obj;
					//把街道的站牌資訊交給list view去做顯示
					myAdapter adapter = new myAdapter(MainActivity.this, rwRes);
					listView.setAdapter(adapter);
					//因為成功接到處理玩的資訊了 所以就把dialog關掉
					dialog.cancel();

					// test.setText(rwRes[2]);
					// RWNotify.sendNotification
				}
			}
		};
		// Intent intent = new Intent(MainActivity.this, RWNotify.class);
		// startService(intent);
	}

	@Override
	protected void onDestroy() { // kill process
		super.onDestroy();
		Intent intent = new Intent(MainActivity.this, RWNotify.class);
		stopService(intent);
		// Kill myself
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//讀取畫面
	public void DialogComplete(String from, String to, String fromID, String toID) {
		fromToText.setText(from + "→" + to);
		dialog.setMessage("讀取中....");
		dialog.show();
		// start service
		Intent intent = new Intent(MainActivity.this, RWNotify.class);
		Bundle stationData = new Bundle();
		stationData.putString("from", fromID);
		stationData.putString("to", toID);
		intent.putExtras(stationData);
		//啟動RWNotify
		startService(intent);
	}

	private OnClickListener startClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			MyDialog dialog = new MyDialog(MainActivity.this,
					R.layout.my_dialog);
			dialog.show();
		}
	};

	private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// stop service
			Intent intent = new Intent(MainActivity.this, RWNotify.class);
			stopService(intent);
		}
	};
}
