package com.example.myrailwaynotify;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.message.BasicNameValuePair;

//��@android service 
//�Ψ���ܴ����e��
public class RWNotify extends Service {

	final int resCountSize = 6;
	final int rowSize = 2;
	public String fromstationID = "";
	public String toStationID = "";
	
	//RWNotify��handler(�Ψӱ�HTTP GET�^�Ǫ����G)
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				//�S�����~���ܴN��ѪR�������P��T�����G�A�ᵹactivity�h���
				case 1 :
					Message message = new Message();
					message.what = 1;
					// handler.sendMessage(message);
					String result = null;
					if (msg.obj instanceof String) {
						result = (String) msg.obj;
						ArrayList<String[]> res = getTrainData(result);
						message.obj = res;
						MainActivity.handler.sendMessage(message);
					}
			}
		}
	};
	
	//�w�F�ѪRhtml��l�X�A���X�_�毸�B�ɶ�
	ArrayList<String[]> getTrainData(String rwRes) {
		ArrayList<String[]> list = new ArrayList<String[]>();
		int startPos = 0;
		int endPos = 0;
		int gridRowEnd = 0;

		startPos = rwRes.indexOf("<tr class=\"Grid_Row\"");
		gridRowEnd = rwRes.indexOf("</tr>", startPos);

		for (int i = 0; i < resCountSize; i++) {

			startPos = rwRes.indexOf("<tr class=\"Grid_Row\"", gridRowEnd);
			gridRowEnd = rwRes.indexOf("</tr>", startPos);

			String gridRow = rwRes.substring(startPos, gridRowEnd);
			Log.i("gridRow", Integer.toString(gridRowEnd));

			startPos = gridRow
					.indexOf("<td align=\"center\" width=\"120\"><font color=\"Black\">");
			endPos = gridRow.indexOf("&rarr", startPos);
			startPos += 51;
			String fromStation = gridRow.substring(startPos, endPos);

			startPos = gridRow.indexOf("&rarr;", endPos);
			endPos = gridRow.indexOf("</font>", startPos);
			startPos += 6;
			String toStation = gridRow.substring(startPos, endPos);

			startPos = gridRow
					.indexOf(
							"<td class=\"SearchResult_Time\" align=\"center\" width=\"65\"><font color=\"Black\">",
							endPos);
			endPos = gridRow.indexOf("</font>", startPos);
			startPos += 76;

			String startTime = gridRow.substring(startPos, endPos);

			startPos = gridRow
					.indexOf(
							"<td class=\"SearchResult_Time\" align=\"center\" width=\"65\"><font color=\"Black\">",
							endPos);
			endPos = gridRow.indexOf("</font>", startPos);
			startPos += 76;
			String endTime = gridRow.substring(startPos, endPos);

			startPos = gridRow.indexOf(
					"<td align=\"center\" width=\"80\"><font color=\"Black\">",
					endPos);
			endPos = gridRow.indexOf("</font>", startPos);
			startPos += 50;
			String dur = gridRow.substring(startPos, endPos);
			
			//Log.i("pos1", Integer.toString(startPos));//debug��
			//Log.i("pos2", Integer.toString(endPos));//debug��
			
			
			//��Ĥ@����ƥᵹservice�M����ܦA���A�C
			if (1 == i)
				sendNotification(fromStation + "��" + toStation + " "
						+ startTime + "  �f���ɶ�:" + dur);
			
			//�M��C����Ƴ��s�_�ӷǳƵ�UI��
			String[] res = new String[rowSize];
			res[0] = fromStation + "��" + toStation + " " + startTime;
			res[1] = "�f���ɶ�:" + dur;
			list.add(res);
		}
		// return fromStation+" "+toStation+" "+startTime+" "+dur;
		return list;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Notify Service", "Start!!!");
		Log.i("bundle",intent.getStringExtra("from"));
		Log.i("bundle",intent.getStringExtra("to"));
		//service�C��|�@���h��s���(���i�H���γo���)
		
		this.fromstationID = intent.getStringExtra("from");
		this.toStationID = intent.getStringExtra("to");
		handler.postDelayed(GETRunable, 1000);//
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(GETRunable);
		super.onDestroy();
	}
	
	
	//�����W�d�쪺�Ϊk
	@SuppressWarnings("deprecation")
	public void sendNotification(String message) {

		try {
			// -- �s���g�k
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Builder builder = new Notification.Builder(this);
			PendingIntent contentIndent = PendingIntent.getActivity(this, 0,
					new Intent(this, MainActivity.class),
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIndent)
					.setSmallIcon(R.drawable.train)
					// �]�m���A�C�̭����ϥܡ]�p�ϥܡ^�@�@�@�@�@�@�@�@�@�@�@
					.setLargeIcon(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.train)) // �U�ԤU�ԲM��̭����ϥܡ]�j�ϥܡ^
					.setTicker(message) // �]�m���A�C����ܪ���T
					.setWhen(System.currentTimeMillis())// �]�m�ɶ��o�ͮɶ�
					.setAutoCancel(false) // �]�m�i�H�M��
					.setContentTitle("�U�Z�����q��") // �]�m�U�ԲM��̪����D
					.setContentText(message); // �]�m�W�U�夺�e

			Notification notification = builder.getNotification();

			// notification.defaults |= Notification.DEFAULT_SOUND;
			// notification.sound = Uri
			// .parse("file:///sdcard/Notifications/hangout_ringtone.m4a");
			// notification.sound = Uri.withAppendedPath(
			// Audio.Media.INTERNAL_CONTENT_URI, "6");
			// notification.sound = Uri.parse("android.resource://"
			// + getPackageName() + "/" + R.raw.koko);
			// �᭱���]�w�|�\���e����

			// ����
			//notification.defaults |= Notification.DEFAULT_VIBRATE; // �Y�Ǥ�����䴩
			// �Х[
			// try catch

			// �{���O
			// notification.defaults |= Notification.DEFAULT_LIGHTS; // ���ըS����

			// �[i�O���F��ܦh��Notification
			notificationManager.notify(1, notification);
			// i++;
			// --
		} catch (Exception e) {
			
		}
	}
	
	
	//��ꤣ�Ӽg�b�o��...�}�@��thread�h��http GET(�Y�@���諸  �j��n�Arunable�̭�����)
	private Runnable GETRunable = new Runnable() {
		
		
		
		public void run() {
			MyHttpPost myHttpPost = MyHttpPost.getInstance();
			myHttpPost.SetService(RWNotify.this);
			String url = "http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx";
			//���ɶ����Ѽ�
			Date d = new Date();
			int h = d.getHours();
			int m = d.getMinutes();
			SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy/MM/dd");
			//GMT�зǮɶ�����[�K�p��
			nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			SimpleDateFormat nowTime = new java.text.SimpleDateFormat("HHmm");
			//GMT�зǮɶ�����[�K�p��
			nowTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			//���o�ثe�ɶ�
			String sdate = nowdate.format(new java.util.Date());
			String stmie = nowTime.format(new java.util.Date());
			
			//���GET���r��
			String now = Integer.toString(h) + m;
			// String nowDay =
			// Integer.toString(year)+"/"+Integer.toString(mouth)+"/"+Integer.toString(day);
			Log.i("time:", stmie);
			
			Log.i("ID:", RWNotify.this.fromstationID);
			
			String params = "?searchtype=0&searchdate="
					+ sdate
					+ "&fromcity=0&tocity=0&fromstation=" + RWNotify.this.fromstationID+
					"&tostation="+RWNotify.this.toStationID+
					"&trainclass=2&fromtime="
					+ stmie + "&totime=2359";
			//�M��N�hGET�M�ᱵ��HTML��l�X��
			myHttpPost.StartHttpGet(url, params);
			
			// GET �����
			// http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx?searchtype=0&searchdate=2015/06/22&fromstation=1008&tostation=1238&trainclass=0&fromtime=0000&totime=2359
			// http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx?searchtype=0&searchdate=2015/06/22&fromcity=0&tocity=10&fromstation=1008&tostation=1238&trainclass=2&fromtime=0000&totime=2359

			handler.postDelayed(this, 30000);

		}
	};
}
