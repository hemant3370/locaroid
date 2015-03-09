package com.hemant.locaroid;

import android.app.Activity;
import android.os.Bundle;

public class Viewer extends Activity {
	String myview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getIntent().getExtras().getString("view");

	}
}
