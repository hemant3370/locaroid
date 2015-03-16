package com.hemant.locaroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Mapview extends FragmentActivity {
	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	GoogleMap mymap;
	ImageButton btnSpeak;
	MapView mmapview;
	GPSTracker gps;
	EditText et;
	TextView tv1;
	Typeface tf;
	Button go;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tf = Typeface.createFromAsset(getApplicationContext().getAssets(),
				"fonts/Supercell-Magic_5.ttf");
		gps = new GPSTracker(Mapview.this);

		// getActionBar().hide();

		if (servicesOK()) {
			setContentView(R.layout.mapfragment);
			tv1 = (TextView) findViewById(R.id.tv1);
			tv1.setTypeface(tf);
			go = (Button) findViewById(R.id.go);
			go.setTypeface(tf);
			go.setBackgroundColor(Color.YELLOW);
			et = (EditText) findViewById(R.id.editText1);
			et.setTypeface(tf);
			if (initmap()) {
				Toast.makeText(this, "ready to map", Toast.LENGTH_LONG).show();
				mymap.setMyLocationEnabled(true);
				gotolocation(
						Double.parseDouble(getIntent().getExtras().getString(
								"lat")),
						Double.parseDouble(getIntent().getExtras().getString(
								"lng")), 15);
				MarkerOptions markeroptions = new MarkerOptions().title(
						getIntent().getExtras().getString("service")).position(
						new LatLng(Double.parseDouble(getIntent().getExtras()
								.getString("lat")), Double
								.parseDouble(getIntent().getExtras().getString(
										"lng"))));
				mymap.addMarker(markeroptions);
			} else {
				Toast.makeText(this, "Map not available", Toast.LENGTH_LONG)
						.show();
			}

			// mmapview = (MapView) findViewById(R.id.themap);
			// mmapview.onSaveInstanceState(savedInstanceState);
		} else {
			setContentView(R.layout.mapview);
		}

		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		btnSpeak.setBackgroundColor(Color.YELLOW);
		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				promptSpeechInput();

			}
		});
	}

	private void promptSpeechInput() {
		Intent speechintent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speechintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
				Locale.getDefault());
		speechintent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		try {
			startActivityForResult(speechintent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.mapmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean servicesOK() {
		int isavail = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (isavail == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isavail)) {
			Dialog mdialog = GooglePlayServicesUtil.getErrorDialog(isavail,
					this, GPS_ERRORDIALOG_REQUEST);
			mdialog.show();
		} else {
			Toast.makeText(this, "can't connect to google play services",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private boolean initmap() {
		if (mymap == null) {
			SupportMapFragment mapfrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mymap = mapfrag.getMap();
		}
		return (mymap != null);
	}

	public void gotolocation(double lat, double lng, float zoom) {

		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mymap.moveCamera(update);
	}

	public void geoLocate(View v) throws IOException {
		hideSoftKeyboard(v);

		et = (EditText) findViewById(R.id.editText1);

		String location = et.getText().toString();
		Geocoder gc = new Geocoder(this);
		List<android.location.Address> list = gc.getFromLocationName(location,
				1);
		android.location.Address add = list.get(0);
		String locality = add.getLocality();
		Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
		double latitude = add.getLatitude();
		double longitude = add.getLongitude();
		gotolocation(latitude, longitude, 15);

	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.sat:
			mymap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.normal:
			mymap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.hybrid:
			mymap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		et = (EditText) findViewById(R.id.editText1);
		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				Toast.makeText(Mapview.this, result.get(0).toString(),
						Toast.LENGTH_LONG).show();

				et.setText(result.get(0).toString());
			}
			break;
		}

		}
	}

}
