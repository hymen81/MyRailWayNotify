package com.example.myrailwaynotify;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyHttpPost {
	protected static final int REFRESH_DATA = 0x00000001;
	private static MyHttpPost instance = null;
	private String res = "";
	private MainActivity ma;
	private RWNotify notify;

	public static MyHttpPost getInstance() {
		if (instance == null) {
			instance = new MyHttpPost();
		}
		return instance;
	}

	public void SetMainActivity(MainActivity mainActivity) {
		ma = mainActivity;
	}

	public void SetService(RWNotify notify) {
		this.notify = notify;
	}

	public void StartHttpPost(String url, List<BasicNameValuePair> params) {
		Thread t = new Thread(new sendPostRunnable(url, params));
		t.start();
	}

	public void StartHttpGet(String url, String params) {
		Thread t = new Thread(new sendGetRunnable(url, params));
		t.start();
	}

	public String GetPostRes() {

		return res;
		// Intent intent = new Intent();
		// intent.setClass(MainActivity.this,MainMenu.class);
		// startActivity(intent);
		// finish();
	}

	public String httpGET(String url, String params) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url + params);
		HttpResponse response;
		try {
			response = client.execute(request);
			HttpEntity resEntity = response.getEntity();
			String res = EntityUtils.toString(resEntity);
			this.res = res;

			Log.d("Response of GET request", res);
			return res;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return "Error!!!";
	}

	public String httpPOST(String url, List<BasicNameValuePair> params) {
		HttpPost post = new HttpPost(url);
		try {
			// 送出HTTP request

			// List<BasicNameValuePair> postData = new
			// ArrayList<BasicNameValuePair>();
			// postData.add("name="+"1411070");
			// postData.add("password"+);
			// params.get(0).toString();

			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			post.setHeader(HTTP.CONTENT_TYPE,
					"application/x-www-form-urlencoded");
			HttpResponse httpResponse = new DefaultHttpClient().execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				return strResult;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESH_DATA :
					String result = null;
					if (msg.obj instanceof String) {
						result = (String) msg.obj;
						// res = result;
						// String[] serverData = res.split(",");
						Message notifyMessage = new Message();
						notifyMessage.what = REFRESH_DATA;
						notifyMessage.obj = msg.obj;
						notify.handler.sendMessage(notifyMessage);
					}
					if (result != null)
						// Toast.makeText(MainActivity.this, result,
						// Toast.LENGTH_LONG).show();
						break;
			}
		}
	};

}

//HTTP的 get method
class sendGetRunnable implements Runnable {
	protected static final int REFRESH_DATA = 0x00000001;
	String url = null;
	String getData = null;
	//設定要傳的字串
	public sendGetRunnable(String url, String params) {
		this.url = url;
		getData = params;
	}
	@Override
	public void run() {
		MyHttpPost mhp = MyHttpPost.getInstance();
		String result = mhp.httpGET(url, getData);
		mhp.mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}

class sendPostRunnable implements Runnable {
	protected static final int REFRESH_DATA = 0x00000001;
	String uri = null;
	List<BasicNameValuePair> postData;
	// 建構子，設定要傳的字串
	public sendPostRunnable(String url, List<BasicNameValuePair> params) {
		this.uri = url;
		postData = params;
	}
	@Override
	public void run() {
		MyHttpPost mhp = MyHttpPost.getInstance();
		String result = mhp.httpPOST(uri, postData);
		mhp.mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}
