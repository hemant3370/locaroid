package com.hemant.locaroid;

import android.app.AlertDialog;
import android.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.hemant.locaroid.GPSTracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements ConnectionCallbacks,
		OnConnectionFailedListener, OnClickListener {

	public boolean mSignInClicked, myloginclicked, myregisterclicked,
			isregistered;
	private ConnectionResult mConnectionResult;
	
	private boolean isResumed = false;
	
	
	public Location location;
	String personName;
	public String provider, present_username, present_password, present_mobile,
			picurl, profileurl;
	String imei, simSerialNumber, devicemobileno;
	SharedPreferences mycreds;
	com.google.android.gms.plus.model.people.Person.Image personPhoto;
	private SignInButton mygooglesigninbtn;
	
	// Registerintentservice regintservice;
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

	public void login(View view) {
		
		

		/*
		 * // new SigninActivity(this).execute(username,password); //
		 * Toast.makeText(MainActivity.this,"location is" +
		 * locationManager.getLastKnownLocation(provider).toString(),
		 * Toast.LENGTH_LONG).show(); if(Double.toString(lat) == null) {
		 * Toast.makeText(this, "no location", Toast.LENGTH_LONG).show(); }
		 */
		// new
		// SigninActivity(MainActivity.this).execute(present_username,present_password);

		Intent menuintent = new Intent(MainActivity.this,
				com.hemant.locaroid.Menu.class);

		menuintent.putExtra("username", present_username);

		startActivity(menuintent);

		// if(this.role.toString() == "success")
		// {

		// startActivity(new Intent(MainActivity.this,
		// com.hemant.locaroid.Menu.class));

		// }

	}

	public void register(View view) {


		new Registeractivity().execute(present_username, present_password,
				present_mobile);

	}

	public void gsignout(View view) {
		Toast.makeText(this, "sign out clicked", Toast.LENGTH_SHORT).show();
	}

	/*
	 * @Override public void onLocationChanged(Location location) {
	 * 
	 * lat = (location.getLatitude()); lng = (location.getLongitude());
	 * Toast.makeText(MainActivity.this, "location changed",
	 * Toast.LENGTH_LONG).show(); Log.e(LOCATION_SERVICE, "changed location"); }
	 * 
	 * @Override public void onProviderDisabled(String provider) {
	 * 
	 * Toast.makeText(MainActivity.this, "providor disabled",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * @Override public void onProviderEnabled(String provider) {
	 * 
	 * Toast.makeText(this, "Enabled new provider " + provider,
	 * Toast.LENGTH_SHORT).show(); }
	 * 
	 * @Override public void onStatusChanged(String provider, int status, Bundle
	 * extras) {
	 * 
	 * Toast.makeText(this, "Disabled provider " + provider,
	 * Toast.LENGTH_SHORT).show(); }
	 */
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
			// picurl =
			// "https://m.ak.fbcdn.net/photos-c.ak/hphotos-ak-xfa1/v/t1.0-0/10414577_898043200224001_7203156539554376376_n.jpg";
			Toast.makeText(MainActivity.this,
					"hey! " + personName + " is connected", Toast.LENGTH_SHORT)
					.show();
			// new
			// Registeractivity(this).execute(present_username,currentPerson.getBirthday().toString(),"0");
			// firstgps.getuname(present_username);
			
				new Registeractivity().execute(present_username);
				Log.d(personName, "register called");
		
			Intent menuintent = new Intent(MainActivity.this,
					com.hemant.locaroid.Menu.class);
			menuintent.putExtra("username", present_username);
			menuintent.putExtra("picurl", picurl);
			menuintent.putExtra("profileurl", profileurl);
			menuintent.putExtra("gname", personName);
			ActivityCompat.finishAffinity(this);
			startActivity(menuintent);
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
				&& !mygoogleapiclient.isConnecting()) {
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
		
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		
		isResumed = false;
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

	

	

}