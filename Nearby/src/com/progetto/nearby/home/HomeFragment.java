package com.progetto.nearby.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.detailPlaces.DetPlaActivity;
import com.progetto.nearby.detailPlaces.DetailPlaceActivity;
import com.progetto.nearby.gpsService.GpsService;
import com.progetto.nearby.models.Place;

public class HomeFragment extends MapFragment implements OnMapReadyCallback {

	public static final String TAG = "HOME_FRAGMENT";
	
	private GoogleMap googleMap;
	private RecyclerView rvPlaces;
	private PlaceAdapterRV adapter;
	private ArrayList<Place> allPlaces = new ArrayList<Place>();
	private Map<Marker, Integer> markers = new HashMap<Marker, Integer>();
	
	private long lastUpdateMillis = 0;
	private static boolean isFirstTimeOpen = true;
	
	
	/*GpsService myService;
    boolean isBound = false;

	private ServiceConnection myConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        myService = binder.getService();
	        isBound = true;
	        
	    }
	    
	    public void onServiceDisconnected(ComponentName arg0) {
	        isBound = false;
	    }
    };*/
	
	private void centerMyPosition() {
		if(googleMap != null) {
			if(GpsService.isLocationEnabled()){
	        	LatLng latLng = new LatLng(GpsService.getLatitude(), GpsService.getLongitude());
	        	CameraPosition cameraPosition = new CameraPosition
	        									.Builder()
	    								        .target(latLng)
	    								        .zoom(5.8f)
	    								        .build();
	        	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
		}
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		/*Intent intent = new Intent(getActivity(), GpsService.class);
        getActivity().bindService(intent, myConnection, Context.BIND_AUTO_CREATE);*/
				
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
		
		//lstPlaces = (ListView)rootView.findViewById(R.id.lstPlaces);
		rvPlaces = (RecyclerView) rootView.findViewById(R.id.rv_places);
		LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		rvPlaces.setLayoutManager(llm);
		rvPlaces.setSaveEnabled(false);
		rvPlaces.setAdapter(null);
        //getPlaces();
		
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
				
				Toast.makeText(getActivity(), "GET places", Toast.LENGTH_LONG).show();
				AsyncHttpClient client = new AsyncHttpClient();
				
				String url = Tools.buildPlacesUrl(getActivity(), GpsService.getLatitude(), GpsService.getLongitude());
				
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
						

						adapter = new PlaceAdapterRV(getActivity().getApplicationContext(), allPlaces);
						rvPlaces.setAdapter(adapter);
						rvPlaces.addOnItemTouchListener(
								(OnItemTouchListener) new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
								      @Override public void onItemClick(View view, int position) {
								       //Log.d("idPl", "" + PlaceAdapterRV.idPlace);
								       enterDetails(PlaceAdapterRV.idPlace);
								      }
								    })
						);
						
						
						//						lstPlaces.setAdapter(adapter);
//						lstPlaces.setOnItemClickListener(new OnItemClickListener() {
//	
//							@Override
//							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//								enterDetails(arg3);
//							}
//						});
					}	
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			}
		} else {
			Toast.makeText(getActivity(), "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
	}

	protected void enterDetails(long id) {
		Intent inte = new Intent(getActivity(), DetPlaActivity.class);
    	Bundle placeBundle = new Bundle();
    	placeBundle.putLong(Place.tag_id, id);
    	inte.putExtras(placeBundle);
		startActivity(inte);
//		Intent intent = new Intent(getActivity(), DetailPlaceActivity.class);
//    	Bundle placeBundle = new Bundle();
//    	placeBundle.putLong(Place.tag_id, id);
//    	intent.putExtras(placeBundle);
//        startActivity(intent);
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
			centerMyPosition();
	        googleMap.getUiSettings().setZoomControlsEnabled(true);
	        //decoreMap();
	        
	    	googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					Intent intent = new Intent(getActivity(), DetailPlaceActivity.class);
			    	intent.putExtra(DetailPlaceActivity.ID_PLACE, markers.get(marker));
			        startActivity(intent);
				}
			});
		}
	}
	
	private void decoreMap() {
		Marker marker;
		googleMap.clear();
		markers.clear();
		for (Place place : allPlaces) {
			marker = googleMap.addMarker(new MarkerOptions()
								.position(new LatLng(place.lat, place.longit))
								.title(place.nome));
			markers.put(marker, place.id);
		}
	}
	
	@Override
	public void onDestroyView() {
		MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

		if (f != null) {
		    try {
		    	if(googleMap != null)
		    		googleMap.clear();
		        getFragmentManager().beginTransaction().remove(f).commit();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		if(googleMap != null)
			googleMap.setMyLocationEnabled(false);
		super.onDestroy();
	}
}
