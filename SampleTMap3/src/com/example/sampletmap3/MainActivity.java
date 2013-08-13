package com.example.sampletmap3;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

public class MainActivity extends Activity implements 
	TMapView.OnClickListenerCallback,
	TMapView.OnCalloutRightButtonClickCallback {

	TMapView mMapView;
	public static final String API_KEY = "458a10f5-c07e-34b5-b2bd-4a891e024c2a";
	boolean isInitialized = false;
	LocationManager mLM;
	Location mCacheLocation;
	
	HashMap<String,MyData> mValueResolver = new HashMap<String, MyData>();
	HashMap<MyData,String> mKeyResolver = new HashMap<MyData, String>();
	
	
	int mIndex = 0;
	
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
			if (isInitialized) {
				moveLocation(location);
				setMyLocation(location);
			} else {
				mCacheLocation = location;
			}
		}
	};
	
	private void moveLocation(Location location) {
		mMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
		mMapView.setZoom(15);
	}
	
	private void setMyLocation(Location location) {
		mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
		Bitmap icon = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
		mMapView.setIcon(icon);
		mMapView.setIconVisibility(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMapView = (TMapView)findViewById(R.id.map);
		new ApiSetup().execute("");
		mLM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Button btn = (Button)findViewById(R.id.zoomIn);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isInitialized) {
					mMapView.MapZoomIn();
				}
			}
		});
		
		btn = (Button)findViewById(R.id.zoomOut);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isInitialized) {
					int zoom = mMapView.getZoom();
					if (zoom > 7) {
						mMapView.MapZoomOut();
					} else {
						Toast.makeText(MainActivity.this, "can't zoom out", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		btn = (Button)findViewById(R.id.addMarker);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isInitialized) {
					TMapPoint point = mMapView.getCenterPoint();
					
					TMapPoint pt = new TMapPoint(point.getLatitude(), point.getLongitude());
					
					TMapMarkerItem item = new TMapMarkerItem();
					item.setTMapPoint(pt);
					item.setPosition(0.5f, 0.5f);
					Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
					item.setIcon(bitmap);
					
					item.setCalloutTitle("MyMarker");
					item.setCalloutSubTitle("marker text");
					item.setCalloutLeftImage(bitmap);
					item.setCalloutRightButtonImage(bitmap);
					item.setCanShowCallout(true);
					
					String markerId = "markerId" + mIndex;
					
					mMapView.addMarkerItem(markerId, item);
					
					MyData value = new MyData();
					value.mIndex = mIndex;
					value.name = "name" + mIndex;
					mValueResolver.put(markerId, value);
					mKeyResolver.put(value,markerId);
					
					mIndex++;
					
					
				}
				
			}
		});
		
	}
	
	
	@Override
	protected void onStart() {
		Location location = mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {
			mListener.onLocationChanged(location);
		}
		mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mListener);
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		mLM.removeUpdates(mListener);
		super.onStop();
	}
	
	public class ApiSetup extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			setUpApiKey();
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			setUpMap();
		}
		
	}

	private void setUpApiKey() {
		mMapView.setSKPMapApiKey(API_KEY);
		mMapView.setLanguage(mMapView.LANGUAGE_KOREAN);
	}
	
	private void setUpMap() {
		// move map...
		// type setting...
		mMapView.setMapType(mMapView.MAPTYPE_STANDARD);
		mMapView.setTrafficInfo(true);
		mMapView.setTrackingMode(false);
		mMapView.setCompassMode(false);
		mMapView.setSightVisible(true);
		
		mMapView.setOnClickListenerCallBack(this);
		
		if (mCacheLocation != null) {
			moveLocation(mCacheLocation);
			setMyLocation(mCacheLocation);
		}
		
		isInitialized = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPressEvent(ArrayList<TMapMarkerItem> markers,
			ArrayList<TMapPOIItem> pois, TMapPoint point, PointF pointf) {
		if (markers != null && markers.size() > 0) {
			for (TMapMarkerItem item : markers) {
				MyData value = mValueResolver.get(item.getID());
				// ...
			}
		}
		return false;
	}

	@Override
	public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markers,
			ArrayList<TMapPOIItem> pois, TMapPoint point, PointF pointf) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCalloutRightButton(TMapMarkerItem marker) {
		MyData value = mValueResolver.get(marker.getID());
		// ...
		
	}

}
