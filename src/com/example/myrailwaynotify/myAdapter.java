package com.example.myrailwaynotify;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView; 

public class myAdapter extends BaseAdapter {

	private final Context context;
	private final ArrayList<String[]> values;

	public myAdapter(Context context, ArrayList<String[]> values) {
		super();
		this.context = context;
		this.values = values;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.my_row, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.Text1);
		TextView textView2 = (TextView) rowView.findViewById(R.id.Text2);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.Image);
		textView.setText(values.get(position)[0]);
		textView2.setText(values.get(position)[1]);
		
		// Change the icon for Windows and iPhone
		// String s = values[position];
		// if (s.startsWith("Windows7") || s.startsWith("iPhone")
		// || s.startsWith("Solaris")) {
		// imageView.setImageResource(R.drawable.no);
		// } else {
		// imageView.setImageResource(R.drawable.ok);
		// }

		return rowView;
	}
	@Override
	public int getCount() {
		return values.size();
	}
	@Override
	public Object getItem(int position) {
		return position;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
}
