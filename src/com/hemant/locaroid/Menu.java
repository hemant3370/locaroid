package com.hemant.locaroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class Menu extends FragmentActivity implements OnItemSelectedListener {
	LocationManager lm;
	Button up, find;
	boolean firstupdate;
	public EditText service;
	public Double mylat, mylng;
	public String myusername, myservice, mystringlat, mystringlng;
	TextView loctv;
	ImageView profilepic;
	URL picurl;
	Bitmap bmval;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		gps = new GPSTracker(Menu.this);
		AdView mAdView = (AdView) findViewById(R.id.adView);
		Spinner serviceselector = (Spinner) findViewById(R.id.spinner);
		up = (Button) findViewById(R.id.button1);
		find = (Button) findViewById(R.id.button2);
		service = (EditText) findViewById(R.id.serviceinput);
		loctv = (TextView) findViewById(R.id.loctv);
		profilepic = (ImageView) findViewById(R.id.profilepic);
		firstupdate = true;
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		// this.mylat =
		// Double.toString(getIntent().getExtras().getDouble("lat"));
		// this.mylng =
		// Double.toString(getIntent().getExtras().getDouble("lng"));
		this.mylat = gps.getLatitude();
		this.mylng = gps.getLongitude();
		mystringlat = Double.toString(gps.getLatitude());
		mystringlng = Double.toString(gps.getLongitude());

		this.myusername = getIntent().getExtras().getString("username");
		loctv.setText("latitude" + mylat + "longitude" + mylng);
		serviceselector.setOnItemSelectedListener(this);
		List<String> categories = new ArrayList<String>();
		categories.add("College Bus");
		categories.add("Public Bus");

		ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categories);
		spinneradapter
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		serviceselector.setAdapter(spinneradapter);

		profilepic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("image", "clicked");
				Uri gplusuri;
				gplusuri = Uri.parse(getIntent().getExtras().getString(
						"profileurl", "http://google.com"));
				Intent gprofileintent = new Intent(Intent.ACTION_VIEW, gplusuri);
				startActivity(gprofileintent);
			}
		});

		find.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent getdistanceintent = new Intent(Menu.this,
						getdistancelist.class);
				getdistanceintent.putExtra("lat", mylat);
				getdistanceintent.putExtra("lng", mylng);
				getdistanceintent.putExtra("gname", getIntent().getExtras()
						.getString("gname"));
				startActivity(getdistanceintent);

			}
		});

		up.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("editext service", service.getText().toString());
				myservice = service.getText().toString();

				if (firstupdate || bmval == null) {

					new locupdate().execute(mystringlat, mystringlng,
							myservice, myusername);
				} else {

					new locationupdateactivity(Menu.this).execute(mystringlat,
							mystringlng, myservice, myusername);
				}

			}
		});
	}

	private class locupdate extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;

		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(Menu.this);
			pDialog.setMessage("Updating ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				picurl = new URL(getIntent().getExtras().getString("picurl"));
				Log.e("bitmap", "tried to get image");
				firstupdate = false;
			} catch (MalformedURLException e) {

				e.printStackTrace();
			}
			if (picurl != null) {
				try {
					bmval = BitmapFactory.decodeStream(picurl.openConnection()
							.getInputStream());
					Log.e("bitmap", "tried to get image");
				} catch (IOException e) {

					e.printStackTrace();
				}

			}

			try {
				String latitude = arg0[0];
				String longitude = arg0[1];
				String service = arg0[2];
				String usname = arg0[3];
				// String link =
				// "http://localhost:82/locaroid/geoupdate.php?longitude="
				// +longitude+"&latitude="+latitude+"&service="+service+"&username="+usname;
				String link = "http://locationservices.site40.net/locaroid/geoupdate.php?longitude="
						+ longitude
						+ "&latitude="
						+ latitude
						+ "&service="
						+ service.replace(" ", "_") + "&username=" + usname;
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(link));
				HttpResponse response = client.execute(request);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				StringBuffer sb = new StringBuffer("");
				String line = "";
				while ((line = in.readLine()) != null) {
					sb.append(line);
					break;
				}
				in.close();
				Log.d("service update response", sb.toString());
				return sb.toString();
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {

			pDialog.dismiss();
			Toast.makeText(Menu.this, "Location updated" + result,
					Toast.LENGTH_SHORT).show();
			profilepic.setImageBitmap(bmval);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// super.onBackPressed();
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.fbshare) {
			Intent fbshare = new Intent(Menu.this, Share.class);
			startActivity(fbshare);
		}

		if (item.getItemId() == R.id.googlesignout) {
			GoogleApiClient gac = new GoogleApiClient.Builder(this).addApi(
					Plus.API).build();
			if (gac.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(gac);
				Plus.AccountApi.revokeAccessAndDisconnect(gac)
						.setResultCallback(new ResultCallback<Status>() {

							@Override
							public void onResult(Status revok) {

								recreate();
								// getParentActivityIntent().putExtra("signoutbool",
								// true);
								// startActivity(getParentActivityIntent());
							}
						});

			}
			gac.disconnect();
			Log.d(myusername, "disconnect reached");
			// gac.connect();
		}
		if (item.getItemId() == R.id.credits) {
			String url2 = "http://locationservices.site40.net/Contacts";
			Intent c1 = new Intent(Menu.this, credsweb.class);
			c1.putExtra("url", url2);
			startActivity(c1);

		}
		if (item.getItemId() == R.id.action_settings) {

			String url1 = "http://locationservices.site40.net";
			Intent c2 = new Intent(Menu.this, credsweb.class);
			c2.putExtra("url", url1);
			startActivity(c2);

		}
		if (item.getItemId() == R.id.exit) {
			AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Menu.this);
			exitbuilder.setMessage("Are you sure you want to exit?");
			exitbuilder.setCancelable(false);
			exitbuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							Menu.this.finish();
							// getParent().finish();

						}
					});

			exitbuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.cancel();
						}
					});
			AlertDialog exiAlertDialog = exitbuilder.create();
			exiAlertDialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		service.setText(parent.getItemAtPosition(position).toString());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		loctv.setText("latitude" + gps.getLatitude() + "longitude"
				+ gps.getLongitude());
		super.onUserInteraction();
	}
}
