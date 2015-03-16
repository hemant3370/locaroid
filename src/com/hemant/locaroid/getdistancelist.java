package com.hemant.locaroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class getdistancelist extends Activity  {
	ListView list;
	TextView tvservice;
	TextView tvdistance;
	TextView tvmobile;
	TextView tvlat;
	TextView tvlongi;
	Button Btngetdata;
	public String latitude;
	public String longitude;
	GPSTracker gps;
	MediaPlayer mp,mp1;
	Animation btnanim;
	RecyclerView rv;

	// public String url =
	// "http://10.0.2.2:82/locaroid/findwithlatlng.php?latitude=37&longitude=-122&latitude=37&radius=255555";
	public String url;
	public JSONObject jObj;
	ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

	// JSON Node Names
	private static final String TAG_distance = "distance";
	private static final String TAG_service = "service";
	private static final String TAG_mobile = "mobile";
	private static final String TAG_arr = "nearby";
	private static final String TAG_lati = "latitude";
	private static final String TAG_longi = "longitude";
	Typeface tf;
	
	// private static final String TAG_gname = "gname";

	JSONArray android = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.receivelist);
		
		
		btnanim = AnimationUtils.loadAnimation(getdistancelist.this,
				R.anim.button_anim);
		mp = MediaPlayer.create(getdistancelist.this, R.raw.elixir_collect_02);
		mp1 = MediaPlayer.create(getdistancelist.this, R.raw.combat_planning_music);
		tf = Typeface.createFromAsset(getApplicationContext().getAssets(),
				"fonts/Supercell-Magic_5.ttf");
		gps = new GPSTracker(getdistancelist.this);

		this.latitude = Double.toString(gps.getLatitude());
		this.longitude = Double.toString(gps.getLongitude());

		oslist = new ArrayList<HashMap<String, String>>();
		Btngetdata = (Button) findViewById(R.id.getdata);
		Btngetdata.setTypeface(tf);
		Btngetdata.setBackgroundColor(Color.YELLOW);
		Btngetdata.startAnimation(btnanim);

		// latitude =
		// longitude = mylng;
		// url =
		// "http://10.0.2.2:82/locaroid/findwithlatlng.php?latitude="+latitude+"&longitude="+longitude+"&latitude="+latitude+"&radius=1000000";
		url = "http://locationservices.site40.net/locaroid/findwithlatlng.php?latitude="
				+ latitude
				+ "&longitude="
				+ longitude
				+ "&latitude="
				+ latitude + "&radius=25";
		Btngetdata.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mp.start();
				new JSONParse().execute(url);

			}
		});
	}
	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		mp1.start();
		super.onUserInteraction();
	}
	private class JSONParse extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvservice = (TextView) findViewById(R.id.service);
			tvdistance = (TextView) findViewById(R.id.distance);
			tvmobile = (TextView) findViewById(R.id.mobile);
			tvlat = (TextView) findViewById(R.id.lati);
			tvlongi = (TextView) findViewById(R.id.longi);

			pDialog = new ProgressDialog(getdistancelist.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// pDialog.setIndeterminate(true);
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... arg0) {

			try {
				String link = arg0[0];
				pDialog.setProgress(10);
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(link));
				pDialog.setProgress(20);
				HttpResponse response = client.execute(request);
				pDialog.setProgress(30);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				StringBuffer sb = new StringBuffer("");
				String line = "";
				pDialog.setProgress(50);
				while ((line = in.readLine()) != null) {

					sb.append(line);
					break;
				}
				pDialog.setProgress(70);
				in.close();
				pDialog.setProgress(80);
				jObj = new JSONObject(sb.toString());
				Log.e("JSON string", sb.toString());
				pDialog.setProgress(90);
			} catch (Exception e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}

			return jObj;
		}

		// @Override
		// protected JSONObject doInBackground(String... args) {
		// jsonparser jParser = new jsonparser();
		// Getting JSON from URL
		// JSONObject json = jParser.getJSONFromUrl(url);
		// return json;
		// }
		@Override
		protected void onPostExecute(JSONObject json) {
			pDialog.setProgress(100);
			pDialog.dismiss();

			try {
				// Getting JSON Array from URL
				android = json.getJSONArray(TAG_arr);
				for (int i = 0; i < android.length(); i++) {
					JSONObject c = android.getJSONObject(i);
					// Storing JSON item in a Variable
					String distance = c.getString(TAG_distance);
					String mobile = c.getString(TAG_mobile);
					String service = c.getString(TAG_service);
					String jlatitude = c.getString(TAG_lati);
					Log.d("json data", "lati:" + jlatitude);
					String jlongitude = c.getString(TAG_longi);
					Log.d("json data", "longi:" + jlongitude);
					// String gname = c.getString(TAG_gname);
					// Adding value HashMap key => value
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(TAG_distance, "distance(km): " + distance);
					map.put(TAG_service,
							"Service: " + service.replace("_", " "));
					map.put(TAG_mobile, "Mobile Number: " + mobile);
					map.put(TAG_lati, "Latitude:" + jlatitude);
					map.put(TAG_longi, "Longitude:" + jlongitude);
					/*
					 * tvservice.setTypeface(tf); tvdistance.setTypeface(tf);
					 * tvlongi.setTypeface(tf); tvlat.setTypeface(tf);
					 * tvmobile.setTypeface(tf);
					 */
					oslist.add(map);
					list = (ListView) findViewById(R.id.list);
					ListAdapter adapter = new SimpleAdapter(
							getdistancelist.this, oslist, R.layout.list_v,
							new String[] { TAG_distance, TAG_mobile,
									TAG_service, TAG_lati, TAG_longi },
							new int[] { R.id.distance, R.id.mobile,
									R.id.service, R.id.lati, R.id.longi });

					list.setAdapter(adapter);
					list.setBackgroundColor(Color.CYAN);

					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							Toast.makeText(
									getdistancelist.this,
									"You Clicked at "
											+ oslist.get(+position).get(
													"service"),
									Toast.LENGTH_SHORT).show();
							mp1.stop();
							Intent mapintent = new Intent(getdistancelist.this,
									Mapview.class);
							mapintent.putExtra("lat", oslist.get(+position)
									.get("latitude").substring(9));
							mapintent.putExtra("lng", oslist.get(+position)
									.get("longitude").substring(10));
							mapintent.putExtra("service", oslist.get(+position)
									.get("service"));

							startActivity(mapintent);
						}
					});

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mp1.stop();
		super.onPause();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		mp1.stop();
		super.onBackPressed();
	}
}
