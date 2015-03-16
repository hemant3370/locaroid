package com.hemant.locaroid;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class locationfinder extends Activity implements LocationListener {
	private LocationManager locationManager;
	private String provider;
	// private Context ctx;
	public double lat;
	public double lng;
	public String mmobile;

	public locationfinder(String mobile) {
		this.mmobile = mobile;
		mmobile = mobile.toString();
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean gpsenabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsenabled) {
			Intent gpsintent = new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(gpsintent);

			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				System.out.println("Provider " + provider
						+ " has been selected.");

				onLocationChanged(location);

			} else {
				Toast.makeText(getApplicationContext(),
						"Location not available", Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 6000, 1, this);
		// new locationupdateactivity().execute(mylat,mylng,myusername);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lat = (location.getLatitude());
		lng = (location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	public String latsender() {
		return Double.toString(lat);

	}

	public String lngsender() {
		return Double.toString(lng);

	}

}
