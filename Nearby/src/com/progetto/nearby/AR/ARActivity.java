package com.progetto.nearby.AR;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.gpsService.GpsService;
import com.progetto.nearby.models.Place;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ARActivity extends Activity {

	private BeyondarFragment mBeyondarFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        
        mBeyondarFragment = (BeyondarFragment) getFragmentManager().findFragmentById(R.id.beyondarFragment);
        
        final World world = new World(this);

	    world.setDefaultBitmap(R.drawable.beyondar_default_unknow_icon, 0);
	    world.setGeoPosition(GpsService.getLatitude(), GpsService.getLongitude());
	    
	    mBeyondarFragment.setDistanceFactor(8);
	    mBeyondarFragment.setMaxDistanceToRender(52000);
	    mBeyondarFragment.setPullCloserDistance(50);
	    mBeyondarFragment.setPushAwayDistance(30);
	    
	    if(Tools.isNetworkEnabled(this)) {
			AsyncHttpClient client = new AsyncHttpClient();
			
			String url = Tools.buildPlacesUrl(getApplicationContext(), GpsService.getLatitude(), GpsService.getLongitude());
			
			client.get(url, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
					JSONObject jsonPlace;
					GeoObject geoObject;
					Place place;
					try {
						for(int i = 0; i < response.length(); i++)
						{
								jsonPlace = response.getJSONObject(i);
								place = Place.decodeJSON(jsonPlace);
								geoObject = new GeoObject(i);
								geoObject.setGeoPosition(place.lat, place.longit);
								geoObject.setImageUri(place.urlImg);
								geoObject.setName(place.nome);
								world.addBeyondarObject(geoObject);
						}
						mBeyondarFragment.setWorld(world);
					} catch (JSONException e) {	e.printStackTrace(); }
				}	
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(getApplicationContext(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		} else {
			Toast.makeText(this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}	    
    }
}
