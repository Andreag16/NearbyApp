package com.progetto.nearby.gpsService;

import org.apache.http.Header;
import org.json.JSONArray;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.Tools;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class GpsService extends Service {
	private static final String TAG = "GpsService";
	private static LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 5000; // 1 secondo
	private static final float LOCATION_DISTANCE = 10; //10 metri

	private static Location mLastLocation;

	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
        public GpsService getService() {
            return GpsService.this;
        }
    }
	
	
	private class OfferteLocationListener implements LocationListener {
	    
	    public OfferteLocationListener(String provider)
	    {
	        Log.e(TAG, "LocationListener " + provider);
	    }
	    @Override
	    public void onLocationChanged(Location location)
	    {
	        Log.e(TAG, "onLocationChanged: " + location);
	        mLastLocation.set(location);
	        Toast.makeText(getApplicationContext(), "cerco offerte", Toast.LENGTH_SHORT).show();
	        
	        if(Tools.isNetworkEnabled(getApplicationContext())) {
					AsyncHttpClient client = new AsyncHttpClient();
					
					int range = getApplicationContext()
							.getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
							.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);
					
					String url = Tools.OFFERS_BY_GPS_URL +
							mLastLocation.getLatitude() +
							"&" + mLastLocation.getLongitude() +
							"&" + range;
					
					client.get(url, new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
							if(response.length() > 0) {
								Toast.makeText(getApplicationContext(), "" + response.length(), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "nessuna offerta", Toast.LENGTH_SHORT).show();
							}
						}	
						
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							Toast.makeText(getApplicationContext(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
			} else {
				Toast.makeText(getApplicationContext(), "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
			}
	    }
	    @Override
	    public void onProviderDisabled(String provider)
	    {
	        Log.e(TAG, "onProviderDisabled: " + provider);
	    }
	    @Override
	    public void onProviderEnabled(String provider)
	    {
	        Log.e(TAG, "onProviderEnabled: " + provider);
	    }
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras)
	    {
	        Log.e(TAG, "onStatusChanged: " + provider);
	    }
	} 
	
	
	OfferteLocationListener[] mLocationListeners = new OfferteLocationListener[] {
	        new OfferteLocationListener(LocationManager.GPS_PROVIDER),
	        new OfferteLocationListener(LocationManager.NETWORK_PROVIDER)
	};
	
	@Override
	public IBinder onBind(Intent arg0)
	{
	    return mBinder;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
	    Log.e(TAG, "onStartCommand");
	    super.onStartCommand(intent, flags, startId);       
	    return START_STICKY;
	}
	@Override
	public void onCreate()
	{
	    Log.e(TAG, "onCreate");
	    initializeLocationManager();	    
	    try {
	        mLocationManager.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[1]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
	    }
	    try {
	        mLocationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[0]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
	    }
	}
	@Override
	public void onDestroy()
	{
	    Log.e(TAG, "onDestroy");
	    super.onDestroy();
	    if (mLocationManager != null) {
	        for (int i = 0; i < mLocationListeners.length; i++) {
	            try {
	                mLocationManager.removeUpdates(mLocationListeners[i]);
	            } catch (Exception ex) {
	                Log.i(TAG, "fail to remove location listners, ignore", ex);
	            }
	        }
	    }
	} 
	private void initializeLocationManager() {
	    Log.e(TAG, "initializeLocationManager");
	    if (mLocationManager == null) {
	        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	        mLastLocation = new Location(LocationManager.NETWORK_PROVIDER);
	    }
	}
	
	
	public static boolean isLocationEnabled() {
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public static double getLatitude() {
		return mLastLocation.getLatitude();
	}
	
	public static double getLongitude() {
		return mLastLocation.getLongitude();
	}
}
