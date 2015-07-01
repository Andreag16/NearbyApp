package com.progetto.nearby.AR;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.view.BeyondarViewAdapter;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.GpsService;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.detailPlaces.DetailPlaceActivity;
import com.progetto.nearby.models.Place;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
								geoObject = new GeoObject(place.id);
								geoObject.setGeoPosition(place.lat, place.longit);
								geoObject.setImageUri(place.urlImg);
								geoObject.setName(place.nome);
								world.addBeyondarObject(geoObject);
						}
						mBeyondarFragment.setWorld(world);
						
						mBeyondarFragment.setOnClickBeyondarObjectListener(new OnClickBeyondarObjectListener() {
							@Override
							public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
								if(beyondarObjects.size() == 0) {
									return;
								}
								BeyondarObject beyondarObject = beyondarObjects.get(0);
								Toast.makeText(ARActivity.this, "Clicked on: " + beyondarObject.getName(), Toast.LENGTH_LONG).show();
								Intent intent = new Intent(ARActivity.this, DetailPlaceActivity.class);
								intent.putExtra(Place.tag_id, beyondarObject.getId());
								startActivity(intent);
							}
						});
						
						//mBeyondarFragment.setBeyondarViewAdapter(new CustomARViewAdapter(ARActivity.this));
						
					} catch (JSONException e) {	e.printStackTrace(); }
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,	String responseString, Throwable throwable) {
					Toast.makeText(getApplicationContext(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		} else {
			Toast.makeText(this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
    }
    
    
    private class CustomARViewAdapter extends BeyondarViewAdapter {

		LayoutInflater inflater;

		public CustomARViewAdapter(Context context) {
			super(context);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final BeyondarObject beyondarObject, View recycledView, ViewGroup parent) {
			/*if (!showViewOn.contains(beyondarObject)) {
				return null;
			}*/
			if (recycledView == null) {
				recycledView = inflater.inflate(R.layout.ar_object_view, null);
			}

			TextView textView = (TextView) recycledView.findViewById(R.id.lblPlaceName);
			textView.setText(beyondarObject.getName());
			
			Button button = (Button) recycledView.findViewById(R.id.btnDetail);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ARActivity.this, DetailPlaceActivity.class);
					intent.putExtra(DetailPlaceActivity.ID_PLACE, beyondarObject.getId());
					startActivity(intent);
				}
			});

			// Once the view is ready we specify the position
			setPosition(beyondarObject.getScreenPositionTopRight());

			return recycledView;
		}

	}
}
