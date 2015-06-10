package com.progetto.nearby.home;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.GPSProvider.IGPSCallbacks;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.detailPlaces.DetailPlaceActivity;
import com.progetto.nearby.models.Place;

public class HomeFragment extends MapFragment implements OnMapReadyCallback, IGPSCallbacks  {

	public static final String TAG = "HOME_FRAGMENT";
	
	private GoogleMap googleMap;
	private ListView lstPlaces;
	private PlacesAdapter adapter;
	private ArrayList<Place> allPlaces = new ArrayList<Place>();
	
	private long lastUpdateMillis = 0;
	private static boolean isFirstTimeOpen = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Tools.gpsProvider.registerListener(this);
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
		
		lstPlaces = (ListView)rootView.findViewById(R.id.lstPlaces);
		getPlaces();
		
		super.onCreateView(inflater, container, savedInstanceState);
		return rootView;
	}
	
	@Override
	public void onResume() {
		getPlaces();
		super.onResume();
	}

	private void getPlaces() {
		long currentMillis = Calendar.getInstance().getTimeInMillis();
		if(Tools.isNetworkEnabled(getActivity())) {
			if((currentMillis - lastUpdateMillis) > 3000) {
				lastUpdateMillis = currentMillis;
				
				Toast.makeText(getActivity(), "GET", Toast.LENGTH_LONG).show();
				AsyncHttpClient client = new AsyncHttpClient();
				int distance = getActivity()
								.getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
								.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);
				String url = Tools.PLACES_URL + "/" +
						Tools.gpsProvider.getLatitude() +
						"+" + Tools.gpsProvider.getLongitude() +
						"+" + distance;
				
				client.get(url, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
						JSONObject jsonPlace;
						allPlaces = new ArrayList<Place>();
						for(int i = 0; i < response.length(); i++)
						{
							try {
								jsonPlace = response.getJSONObject(i);
								allPlaces.add(Place.decodeJSON(jsonPlace));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						if(googleMap != null) {
							decoreMap();
						}
						

						adapter = new PlacesAdapter(getActivity().getApplicationContext(), allPlaces);
						lstPlaces.setAdapter(adapter);
						lstPlaces.setOnItemClickListener(new OnItemClickListener() {
	
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								enterDetails(arg3);
							}
						});
					}	
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, responseString, throwable);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
				});
			}
		} else {
			Toast.makeText(getActivity(), "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
	}

	protected void enterDetails(long id) {
		Intent intent = new Intent(getActivity(), DetailPlaceActivity.class);
    	//intent.putExtra(DetailPlaceActivity.ID_PLACE, (int)id);
    	Bundle placeBundle = new Bundle();
    	placeBundle.putLong(Place.tag_id, id);
    	intent.putExtras(placeBundle);
        startActivity(intent);
	}

	public static HomeFragment newInstance(Bundle args) {
		HomeFragment fragment = new HomeFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
      
        alertDialog.setTitle("Posizione GPS");
        alertDialog.setMessage("La connessione GPS è disattivata. Vuoi abilitarla per una posizione più precisa?");
        
        alertDialog.setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });

        alertDialog.show();
    }

	@Override
	public void onMapReady(GoogleMap map) {
		googleMap = map;
		if(googleMap != null) {
			googleMap.setMyLocationEnabled(true);
	        	        
	        if(Tools.gpsProvider.canGetLocation()){
	        	centerMyPosition();
	        } else {
	        	if(isFirstTimeOpen) {
	        		showSettingsAlert();
	        		isFirstTimeOpen = false;
	        	}
	        	CameraPosition cameraPosition = new CameraPosition
						.Builder()
				        .target(new LatLng(43.028316, 12.460283))
				        .zoom(5.2f)
				        .build();
	        	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	        }
	        
	        googleMap.getUiSettings().setZoomControlsEnabled(true);
	        //decoreMap();
	    	googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					/*
					Intent intent = new Intent(getActivity(), DettagliPlaceActivity.class);
			    	intent.putExtra(DettagliPlaceActivity.ID_PLACE, Tools.markersList.get(marker).id);
			        startActivity(intent);
			        */
				}
			});
		}
	}
	
	private void decoreMap() {
		googleMap.clear();
		for (Place place : allPlaces) {
			googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(place.lat, place.longit))
				.title(place.nome));
		}
	}

	private void centerMyPosition() {
    	CameraPosition cameraPosition = new CameraPosition
    									.Builder()
								        .target(Tools.gpsProvider.getLatLng())
								        .zoom(5.8f)
								        .build();
    	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
	
	@Override
	public void onDestroy() {
		if(googleMap != null)
			googleMap.setMyLocationEnabled(false);
		super.onDestroy();
	}


	@Override
	public void onLocationChanged() {
		getPlaces();		
	}
}
