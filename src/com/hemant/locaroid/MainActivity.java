package com.hemant.locaroid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends FragmentActivity implements ConnectionCallbacks,
		OnConnectionFailedListener, OnClickListener {

	public boolean mSignInClicked, myloginclicked, myregisterclicked,isregistered;
	private ConnectionResult mConnectionResult;
	private ProgressDialog pDialog;
	JSONObject jobj;
	public Location location;
	String personName;
	public String provider, present_username, present_password, present_mobile,
			picurl, profileurl;
	String imei, simSerialNumber, devicemobileno;
	SharedPreferences mycreds;
	com.google.android.gms.plus.model.people.Person.Image personPhoto;
	GPSTracker firstgps;
	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;
	/* Client used to interact with Google APIs. */
	public GoogleApiClient mygoogleapiclient;
	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;
    TextView appname;
	Typeface tf;
	TextToSpeech welcome ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Typeface tf = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/Supercell-Magic_5.ttf");
		appname = (TextView) findViewById(R.id.appname);
		appname.setTypeface(tf);
		
		mygoogleapiclient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

	
		findViewById(R.id.googlesignin).setOnClickListener(this);
		
		mycreds = getSharedPreferences("myprefs", 0);

		present_username = mycreds.getString("uname", "");
		present_password = mycreds.getString("pass", "");
		present_mobile = mycreds.getString("mobi", "");

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// get IMEI
		imei = tm.getDeviceId();

		// get SimSerialNumber
		simSerialNumber = tm.getSimSerialNumber();
		devicemobileno = tm.getLine1Number();

		Toast.makeText(MainActivity.this, "user:" + present_username,
				Toast.LENGTH_SHORT).show();
		Toast.makeText(
				MainActivity.this,
				"IEMI is" + imei + "sim number is:" + simSerialNumber
						+ "phone number is:" + devicemobileno,
				Toast.LENGTH_LONG).show();
		if (present_username != null) {
			
		}

		/*
		 * boolean gpsenabled =
		 * locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); if
		 * (!gpsenabled) { Intent gpsintent = new
		 * Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		 * startActivity(gpsintent); }
		 */
		// regintservice = new Registerintentservice("name");
		// regintservice.onCreate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (!mygoogleapiclient.isConnected()) {

			if (mConnectionResult.hasResolution()) {
				try {
					mIntentInProgress = true;
					startIntentSenderForResult(mConnectionResult
							.getResolution().getIntentSender(), RC_SIGN_IN,
							null, 0, 0, 0);
				} catch (SendIntentException e) {
					// The intent was canceled before it was sent. Return to the
					// default
					// state and attempt to connect to get an updated
					// ConnectionResult.
					mIntentInProgress = false;
					// setContentView(R.layout.activity_main);

					mygoogleapiclient.connect();
				}
			}
		} else {
			return;

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mygoogleapiclient.connect();

	}

	@Override
	protected void onStop() {

		super.onStop();
		if (mygoogleapiclient.isConnected()) {
			mygoogleapiclient.disconnect();
		}
		SharedPreferences mycreds = getSharedPreferences("myprefs", 0);
		SharedPreferences.Editor editor = mycreds.edit();
		editor.putString("uname", present_username);
		editor.putString("pass", present_password);
		editor.putString("mobi", present_mobile);

		editor.commit();

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		// We've resolved any connection errors. mygoogleapiclient can be used
		// to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		if (Plus.PeopleApi.getCurrentPerson(mygoogleapiclient) != null) {
			Person currentPerson = Plus.PeopleApi
					.getCurrentPerson(mygoogleapiclient);
			personName = currentPerson.getDisplayName();
			personPhoto = currentPerson.getImage();
			String personGooglePlusProfile = currentPerson.getUrl();
			present_username = Plus.AccountApi
					.getAccountName(mygoogleapiclient);

			picurl = personPhoto.getUrl();
			profileurl = personGooglePlusProfile.toString();
			
			Toast.makeText(MainActivity.this,
					"hey! " + personName + " is connected", Toast.LENGTH_SHORT)
					.show();
			// new
			// Registeractivity(this).execute(present_username,currentPerson.getBirthday().toString(),"0");
			// firstgps.getuname(present_username);
			
				//new Registeractivity().execute(present_username);
				Log.d(personName, "register called");
				new register().execute(Plus.AccountApi.getAccountName(mygoogleapiclient));
		
			
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "connection suspended:" + cause, Toast.LENGTH_LONG)
				.show();
		mygoogleapiclient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "connection failed" + result, Toast.LENGTH_SHORT)
				.show();

		if (!mIntentInProgress && result.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(result.getResolution()
						.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mygoogleapiclient.connect();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, responseCode, intent);
		
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mygoogleapiclient.isConnecting()) {
				mygoogleapiclient.connect();
			}
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (view.getId() == R.id.googlesignin
				&& ! mygoogleapiclient.isConnecting()) {
			Toast.makeText(this, "sign in clicked", Toast.LENGTH_SHORT).show();
			mSignInClicked = true;
			resolveSignInError();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		

		if (item.getItemId() == R.id.googlesignout) {
			GoogleApiClient gac = new GoogleApiClient.Builder(this).addApi(
					Plus.API).build();
			if (gac.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(gac);
				Plus.AccountApi.revokeAccessAndDisconnect(gac)
						.setResultCallback(new ResultCallback<Status>() {
							
							@Override
							public void onResult(Status revok) {
								// TODO Auto-generated method stub
								recreate();
							}
						}); {
//.setResultCallback(new ResultCallback<Status>() {
						}
			}
			gac.disconnect();
			Log.d(present_username, "disconnect reached");
			// gac.connect();
		}
		if (item.getItemId() == R.id.action_settings) {

			setContentView(R.layout.settingsinflator);
			Button help;
			help = (Button) findViewById(R.id.buttonhelp);
			help.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					// getWindow().requestFeature(Window.FEATURE_PROGRESS);
					Log.e("webview ", "start");

					setContentView(R.layout.websiteview);
					WebView wv = new WebView(MainActivity.this);
					setContentView(wv);
					Log.e("webview ", "view set");

					wv.loadUrl("http://locationservices.site40.net");

				}
			});
		}
		if (item.getItemId() == R.id.exit) {
			AlertDialog.Builder exitbuilder = new AlertDialog.Builder(
					MainActivity.this);
			exitbuilder.setMessage("Are you sure you want to exit?");
			exitbuilder.setCancelable(false);
			exitbuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							getParent().finish();
							MainActivity.this.finish();

						}
					});

			exitbuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
			AlertDialog exiAlertDialog = exitbuilder.create();
			exiAlertDialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	private class register extends AsyncTask<String, String, JSONObject> {
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Authenticating ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... arg0) {
		
			try {

				String username = arg0[0];
				// String password = (String)arg0[1];
				// String mobile = (String)arg0[2];
				//String link ="http://10.0.2.2:82/locaroid/registerwithemailonly.php?username="+username;
				String link = "http://locationservices.site40.net/locaroid/registerwithemailonly.php?username="+username;
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
				jobj = new JSONObject(sb.toString());
				Log.e("Register JSON string", sb.toString());

			} catch (Exception e) {
				Log.e(" Register JSON Parser", "Error parsing data " + e.toString());
			}
			return jobj;

		}

		

		@Override
		protected void onPostExecute(JSONObject result) {

			pDialog.dismiss();
			//Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
			Intent menuintent = new Intent(MainActivity.this,
					com.hemant.locaroid.Menu.class);
			menuintent.putExtra("username", present_username);
			menuintent.putExtra("picurl", picurl);
			menuintent.putExtra("profileurl", profileurl);
			menuintent.putExtra("gname", personName);
			ActivityCompat.finishAffinity(MainActivity.this);
			startActivity(menuintent);
		}
	}

	

}
