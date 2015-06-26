package com.progetto.nearby.offerte;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.gpsService.GpsService;
import com.progetto.nearby.models.Offerta;

public class OfferteFragment extends Fragment {

	public static final String TAG = "OFFERTS_FRAGMENT";
	//private ListView listaOfferte;
	private RecyclerView rv;
	private OffertaAdapterRV adapter;
	private ArrayList<Offerta> offerts;
	private long lastUpdateMillis = 0;
	private AsyncHttpClient client;
	private ProgressDialog progressdialog;
	
	public static OfferteFragment newInstance(Bundle args){
		OfferteFragment offerte_fragment = new OfferteFragment();
		offerte_fragment.setArguments(args);
		return offerte_fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View offerte_view = inflater.inflate(R.layout.fragment_offerte, null);
		//listaOfferte = (ListView) offerte_view.findViewById(R.id.listaOfferte);
		rv  = (RecyclerView)offerte_view.findViewById(R.id.rv);
		LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		rv.setLayoutManager(llm);
		getOffertsByGPS();
		return offerte_view;
	}

	private void getOffertsByGPS() {
		long currentMillis = Calendar.getInstance().getTimeInMillis();
		if(Tools.isNetworkEnabled(getActivity())) {
			if((currentMillis - lastUpdateMillis) > 3000) {
				lastUpdateMillis = currentMillis;
				int distance = getActivity()
						.getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
						.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);

				Log.d("distance", ""+ distance);
				String url = Tools.OFFERS_BY_GPS_URL +
						GpsService.getLatitude() +
						"&" + GpsService.getLongitude() +
						"&" + distance;
				client = new AsyncHttpClient();
				client.get(url, new JsonHttpResponseHandler(){

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						offerts  = new ArrayList<Offerta>();
						for(int i = 0; i < response.length(); i++)
						{
							try {
								offerts.add(Offerta.decodeJSON(response.getJSONObject(i)));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						adapter = new OffertaAdapterRV(getActivity(), offerts);
						rv.setAdapter(adapter);
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
		}
		else {
			Toast.makeText(getActivity(), "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
	}
	
}
