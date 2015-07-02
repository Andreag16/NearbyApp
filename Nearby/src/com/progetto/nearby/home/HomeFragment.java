package com.progetto.nearby.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.GpsService;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.detailPlaces.DetPlaActivity;
import com.progetto.nearby.detailPlaces.DetailPlaceActivity;
import com.progetto.nearby.models.Place;

public class HomeFragment extends MapFragment implements OnMapReadyCallback, LocationListener {

	public static final String TAG = "HOME_FRAGMENT";
	
	private GoogleMap googleMap;
	private RecyclerView rvPlaces;
	private PlaceAdapterRV adapter;
	private ArrayList<Place> allPlaces = new ArrayList<Place>();
	private Map<Marker, Integer> markers = new HashMap<Marker, Integer>();
	private HomeActivity homeActivity;
	
	private static boolean isFirstTimeOpen = true;

	
	
	private void centerMyPosition() {
		if(googleMap != null) {
			if(GpsService.isLocationEnabled()){
	        	LatLng latLng = new LatLng(GpsService.getLatitude(), GpsService.getLongitude());
	        	float zoom = calcGoogleMapsZoom();
	        	CameraPosition cameraPosition = new CameraPosition
	        									.Builder()
	    								        .target(latLng)
	    								        .zoom(zoom)
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
    
	private float calcGoogleMapsZoom() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		int distance = sharedPreferences.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);
		Display display = getActivity(). getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);		
		
		double equatorLength = 40075004;
	    double widthInPixels = size.x;
	    double metersPerPixel = equatorLength / size.x;
	    float zoomLevel = 1;
	    while ((metersPerPixel * widthInPixels) > distance) {
	        metersPerPixel /= 1.5;
	        zoomLevel += 0.5;
	    }		
		
		return zoomLevel - 0.3f;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		//if(googleMap == null)
			((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
		
		rvPlaces = (RecyclerView) rootView.findViewById(R.id.rv_places);
		LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		rvPlaces.setLayoutManager(llm);
		rvPlaces.setSaveEnabled(false);
		rvPlaces.setAdapter(null);
		
		super.onCreateView(inflater, container, savedInstanceState);
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof HomeActivity)
			homeActivity = (HomeActivity) activity;
	}
	
	@Override
	public void onResume() {
		//if(googleMap == null)
			//((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
		//centerMyPosition();
		super.onResume();
		if(homeActivity.isBound)
			getPlaces();
	}

	private void getPlaces() {
		if(Tools.isNetworkEnabled(getActivity())) {
			//Toast.makeText(getActivity(), "GET places", Toast.LENGTH_LONG).show();
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
						centerMyPosition();
					}
					

					adapter = new PlaceAdapterRV(getActivity().getApplicationContext(), allPlaces);
					rvPlaces.setAdapter(adapter);
					rvPlaces.addOnItemTouchListener(
							(OnItemTouchListener) new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
							      @Override public void onItemClick(View view, int position) {
							    	  enterDetails(allPlaces.get(position).id);
							      }
							    })
					);
				}	
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
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
			//centerMyPosition();
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
		int icon;
		for (Place place : allPlaces) {
			icon = place.tipo.equals("ac") ? R.drawable.marker_ac : R.drawable.marker_poi;
			marker = googleMap.addMarker(new MarkerOptions()
								.position(new LatLng(place.lat, place.longit))
								.title(place.nome)
								.icon(BitmapDescriptorFactory.fromResource(icon)));
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
	
	@Override
	public void onDetach() {
		super.onDetach();
		homeActivity = null;
	}
	
	public void onServiceConnected() {
		//GpsService.registerLocationListener(this, LocationManager.NETWORK_PROVIDER);
		if(GpsService.isLocationEnabled()){
			GpsService.registerLocationListener(this, LocationManager.GPS_PROVIDER);
			GpsService.registerLocationListener(this, LocationManager.NETWORK_PROVIDER);
		} else {
			showSettingsAlert();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(location.getLatitude() != 0 && location.getLongitude() != 0) {
			getPlaces();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
	@Override
	public void onProviderEnabled(String provider) { }
	@Override
	public void onProviderDisabled(String provider) { }
}
