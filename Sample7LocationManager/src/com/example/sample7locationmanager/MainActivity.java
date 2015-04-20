package com.example.sample7locationmanager;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	LocationManager mLM;
	String mProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mProvider = LocationManager.GPS_PROVIDER;
		mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	LocationListener mListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {
			Toast.makeText(
					MainActivity.this,
					"lat : " + location.getLatitude() + ", lng : "
							+ location.getLongitude(), Toast.LENGTH_SHORT)
					.show();
		}
	};

	boolean isFirst = true;
	@Override
	protected void onStart() {
		super.onStart();
		if (!mLM.isProviderEnabled(mProvider)) {
			if (isFirst) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				isFirst = false;
			} else {
				Toast.makeText(this, "location settings....", Toast.LENGTH_SHORT).show();
				finish();
			}
			return;
		}
		
		Location location = mLM.getLastKnownLocation(mProvider);
		if (location != null) {
			mListener.onLocationChanged(location);
		}
		
		mLM.requestLocationUpdates(mProvider, 2000, 5, mListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLM.removeUpdates(mListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}