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

//實作android service 
//用來顯示提醒畫面
public class RWNotify extends Service {

	final int resCountSize = 6;
	final int rowSize = 2;
	public String fromstationID = "";
	public String toStationID = "";
	
	//RWNotify的handler(用來接HTTP GET回傳的結果)
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				//沒有錯誤的話就把解析玩的站牌資訊的結果再丟給activity去顯示
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
	
	//硬幹解析html原始碼，取出起驛站、時間
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
			
			//Log.i("pos1", Integer.toString(startPos));//debug用
			//Log.i("pos2", Integer.toString(endPos));//debug用
			
			
			//把第一筆資料丟給service然後顯示再狀態列
			if (1 == i)
				sendNotification(fromStation + "→" + toStation + " "
						+ startTime + "  搭車時間:" + dur);
			
			//然後每筆資料都存起來準備給UI用
			String[] res = new String[rowSize];
			res[0] = fromStation + "→" + toStation + " " + startTime;
			res[1] = "搭車時間:" + dur;
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
		//service每秒會一直去更新資料(其實可以不用這麼快)
		
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
	
	
	//網路上查到的用法
	@SuppressWarnings("deprecation")
	public void sendNotification(String message) {

		try {
			// -- 新的寫法
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Builder builder = new Notification.Builder(this);
			PendingIntent contentIndent = PendingIntent.getActivity(this, 0,
					new Intent(this, MainActivity.class),
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIndent)
					.setSmallIcon(R.drawable.train)
					// 設置狀態列裡面的圖示（小圖示）　　　　　　　　　　　
					.setLargeIcon(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.train)) // 下拉下拉清單裡面的圖示（大圖示）
					.setTicker(message) // 設置狀態列的顯示的資訊
					.setWhen(System.currentTimeMillis())// 設置時間發生時間
					.setAutoCancel(false) // 設置可以清除
					.setContentTitle("下班火車通知") // 設置下拉清單裡的標題
					.setContentText(message); // 設置上下文內容

			Notification notification = builder.getNotification();

			// notification.defaults |= Notification.DEFAULT_SOUND;
			// notification.sound = Uri
			// .parse("file:///sdcard/Notifications/hangout_ringtone.m4a");
			// notification.sound = Uri.withAppendedPath(
			// Audio.Media.INTERNAL_CONTENT_URI, "6");
			// notification.sound = Uri.parse("android.resource://"
			// + getPackageName() + "/" + R.raw.koko);
			// 後面的設定會蓋掉前面的

			// 振動
			//notification.defaults |= Notification.DEFAULT_VIBRATE; // 某些手機不支援
			// 請加
			// try catch

			// 閃光燈
			// notification.defaults |= Notification.DEFAULT_LIGHTS; // 測試沒反應

			// 加i是為了顯示多條Notification
			notificationManager.notify(1, notification);
			// i++;
			// --
		} catch (Exception e) {
			
		}
	}
	
	
	//其實不該寫在這裡...開一個thread去做http GET(某一版改的  強制要再runable裡面執行)
	private Runnable GETRunable = new Runnable() {
		
		
		
		public void run() {
			MyHttpPost myHttpPost = MyHttpPost.getInstance();
			myHttpPost.SetService(RWNotify.this);
			String url = "http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx";
			//取時間的參數
			Date d = new Date();
			int h = d.getHours();
			int m = d.getMinutes();
			SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy/MM/dd");
			//GMT標準時間往後加八小時
			nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			SimpleDateFormat nowTime = new java.text.SimpleDateFormat("HHmm");
			//GMT標準時間往後加八小時
			nowTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			//取得目前時間
			String sdate = nowdate.format(new java.util.Date());
			String stmie = nowTime.format(new java.util.Date());
			
			//塞到GET的字串
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
			//然後就去GET然後接收HTML原始碼拉
			myHttpPost.StartHttpGet(url, params);
			
			// GET 實驗用
			// http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx?searchtype=0&searchdate=2015/06/22&fromstation=1008&tostation=1238&trainclass=0&fromtime=0000&totime=2359
			// http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx?searchtype=0&searchdate=2015/06/22&fromcity=0&tocity=10&fromstation=1008&tostation=1238&trainclass=2&fromtime=0000&totime=2359

			handler.postDelayed(this, 30000);

		}
	};
}
