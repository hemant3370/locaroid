package com.hemant.locaroid;

import com.hemant.locaroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class credsweb extends Activity {
	String myurl;

	WebView credswebview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.hemant.locaroid.R.layout.credits);
		myurl = getIntent().getExtras().getString("url");
		credswebview = (WebView) findViewById(R.id.webView1);
		if (myurl != null) {
			credswebview.loadUrl(myurl);
		}

	}
}
