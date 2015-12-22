package com.example.myrailwaynotify;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MyDialog extends Dialog {
	Context context;
	Button comfirm;
	Spinner toSp, fromSp;
	private String[][] stationName = {{"����", "1031"}, {"�n��", "1006"}, {"�Q�s", "1007"}, {"�x�_", "1008"},
			{"�U��", "1009"}, {"�O��", "1011"}, {"�֦{", "1032"}, {"��L", "1012"}, {"�s��", "1013"},
			{"�a�q", "1014"}};
	ArrayList<String[]> s;
	public MyDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.my_dialog);
		comfirm = (Button) findViewById(R.id.comformBtn);
		comfirm.setOnClickListener(comfirmStationClickListener);
		fromSp = (Spinner) findViewById(R.id.fromSpinner);
		toSp = (Spinner) findViewById(R.id.Tospinner);
		s = new ArrayList<String[]>();
		for (int i = 0; i < stationName.length; i++)
			s.add(stationName[i]);

		myAdapter adapter = new myAdapter(MyDialog.this.context, s);
		// adapter.setDropDownViewResource(R.layout.my_row);

		fromSp.setAdapter(adapter);
		toSp.setAdapter(adapter);
	}

	private Button.OnClickListener comfirmStationClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// ����A��
			// Intent intent = new Intent(MainActivity.this, RWNotify.class);
			// stopService(intent);
			MainActivity ma = (MainActivity) MyDialog.this.context;
			int fromSpPos = fromSp.getSelectedItemPosition();
			int toSpPos = toSp.getSelectedItemPosition();
			ma.DialogComplete(s.get(fromSpPos)[0], s.get(toSpPos)[0], s.get(fromSpPos)[1], s.get(toSpPos)[1]);
			MyDialog.this.dismiss();
		}
	};

}