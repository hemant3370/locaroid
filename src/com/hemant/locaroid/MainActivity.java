package com.hemant.locaroid;

import android.app.AlertDialog;

import com.hemant.locaroid.GPSTracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {

	public boolean mSignInClicked, myloginclicked, myregisterclicked,
			isregistered;
	private ConnectionResult mConnectionResult;
	private UiLifecycleHelper uiHelper;
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;
	private boolean isResumed = false;
	private MenuItem FBlogout;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	public Location location;
	String personName;
	public String provider, present_username, present_password, present_mobile,
			picurl, profileurl;
	String imei, simSerialNumber, devicemobileno;
	SharedPreferences mycreds;
	Image personPhoto;
	private SignInButton mygooglesigninbtn;
	private Button mygooglesignoutbtn;
	// Registerintentservice regintservice;
	GPSTracker firstgps;
	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;
	private Fbauth fbauth;

	/* Client used to interact with Google APIs. */
	public GoogleApiClient mygoogleapiclient;

	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private EditText usernameField, passwordField, mobileField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.facebooklayout);
		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		mygoogleapiclient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		usernameField = (EditText) findViewById(R.id.editText1);
		passwordField = (EditText) findViewById(R.id.editText2);
		mobileField = (EditText) findViewById(R.id.mobi);
		findViewById(R.id.googlesignin).setOnClickListener(this);
		mygooglesignoutbtn = (Button) findViewById(R.id.button2);
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
			usernameField.setText(present_username);
			passwordField.setText(present_password);
			mobileField.setText(present_mobile);
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
		String username = usernameField.getText().toString();
		String password = passwordField.getText().toString();
		present_username = username;
		present_password = password;

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
		// TODO Auto-generated method stub
		String username = usernameField.getText().toString();
		String password = passwordField.getText().toString();
		present_mobile = mobileField.getText().toString();
		present_username = username;
		present_password = password;
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
			if (present_username == null) {
				new Registeractivity().execute(present_username);
			}
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
		uiHelper.onActivityResult(requestCode, responseCode, intent);
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
		if (item.equals(FBlogout)) {
			showFragment(SETTINGS, true);
			return true;
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
								// TODO Auto-generated method stub

								recreate();
								// getParentActivityIntent().putExtra("signoutbool",
								// true);
								// startActivity(getParentActivityIntent());
							}
						});

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

	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		AppEventsLogger.activateApp(this);
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		AppEventsLogger.deactivateApp(this);
		isResumed = false;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// Only make changes if the activity is visible
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			// Clear the back stack
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			if (state.isOpened()) {
				// If the session state is open:
				// Show the authenticated fragment
				showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				// If the session state is closed:
				// Show the login fragment
				showFragment(SPLASH, false);
			}
		}
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Session session = Session.getActiveSession();

		if (session != null && session.isOpened()) {
			// if the session is already open,
			// try to show the selection fragment
			showFragment(SELECTION, false);
		} else {
			// otherwise present the splash screen
			// and ask the person to login.
			showFragment(SPLASH, false);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (fragments[SELECTION].isVisible()) {
			if (menu.size() == 0) {
				FBlogout = menu.add(R.string.fblogout);
			}
			return true;
		} else {
			menu.clear();
			FBlogout = null;
		}
		return false;

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

}