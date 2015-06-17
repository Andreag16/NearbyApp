package com.progetto.nearby.offerte;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.GPSProvider.IGPSCallbacks;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.models.Offerta;

public class OfferteFragment extends Fragment implements IGPSCallbacks{

	public static final String TAG = "OFFERTS_FRAGMENT";
	//private ListView listaOfferte;
	private RecyclerView rv;
	private OffertaAdapterRV adapter;
	private ArrayList<Offerta> offerts = new ArrayList<Offerta>();
	private long lastUpdateMillis = 0;
	private AsyncHttpClient client;
	
	public static OfferteFragment newInstance(Bundle args){
		OfferteFragment offerte_fragment = new OfferteFragment();
		offerte_fragment.setArguments(args);
		return offerte_fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View offerte_view = inflater.inflate(R.layout.fragment_offerte, null);
		//listaOfferte = (ListView) offerte_view.findViewById(R.id.listaOfferte);
		rv  = (RecyclerView)offerte_view.findViewById(R.id.rv);
		LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		rv.setLayoutManager(llm);
		getOffertsByGPS();
		return offerte_view;
	}

	private void getOffertsByGPS() {
		// TODO Auto-generated method stub
		long currentMillis = Calendar.getInstance().getTimeInMillis();
		if(Tools.isNetworkEnabled(getActivity())) {
			if((currentMillis - lastUpdateMillis) > 3000) {
				lastUpdateMillis = currentMillis;
				int distance = getActivity()
						.getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
						.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);
		
//				String url = Tools.OFFERTS_URl + "/" +
//						Tools.gpsProvider.getLatitude() +
//						"+" + Tools.gpsProvider.getLongitude() +
//						"+" + distance;
				Log.d("distance", ""+ distance);
				String url = Tools.OFFERS_BY_GPS_URL + "/" +
						45.9536714 +
						"&" + 12.6858874 +
						"&" + distance;
				client = new AsyncHttpClient();
				client.get(url, new JsonHttpResponseHandler(){

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub
						for(int i = 0; i < response.length(); i++)
						{
							try {
								offerts.add(Offerta.decodeJSON(response.getJSONObject(i)));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						adapter = new OffertaAdapterRV(getActivity(), offerts);
						rv.setAdapter(adapter);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, responseString, throwable);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
					
				});
			}
		}
		else {
			Toast.makeText(getActivity(), "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onLocationChanged() {
		// TODO Auto-generated method stub
		getOffertsByGPS();
	}
	
}
