package com.jaycee.downloadmanager;

import java.io.File;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	private DownloadManager downManager;
	private String downUrl = "";
	private WebView webView;

	@SuppressLint({ "JavascriptInterface", "InlinedApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		
		webView = (WebView) findViewById(R.id.webView);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setUseWideViewPort(true);
		webView.loadUrl("file:///android_asset/download.html");
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webView.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			public void download(String url) {
				downUrl = "http://www.plapk.com/upload/topeaizinoeng.apk";
				handler.sendEmptyMessage(1);
			}
		}, "download_java");
	}

	Handler handler = new Handler(new Handler.Callback() {
		@SuppressLint("NewApi")
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				downManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				Uri uri = Uri.parse(downUrl);
				Request request = new Request(uri);
				request.setDestinationInExternalPublicDir(
						Environment.DIRECTORY_DOWNLOADS, "paoku.apk");
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
						| DownloadManager.Request.NETWORK_WIFI);
				downManager.enqueue(request);

			}
			return true;
		}
	});
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				openFile(file.getAbsolutePath());
			}
		}
	};

	private void openFile(String path) {
		Log.i("MainActivity", path);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.fromFile(new File(path + "/", "paoku.apk")),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
