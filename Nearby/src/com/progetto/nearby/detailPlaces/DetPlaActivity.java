package com.progetto.nearby.detailPlaces;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.R.layout;
import com.progetto.nearby.models.Offerta;
import com.progetto.nearby.models.Place;
import com.progetto.nearby.offerte.OffertaAdapterRV;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class DetPlaActivity extends AppCompatActivity {

	private int idPlace;
	private SmartImageView logo;
	private AsyncHttpClient client;
	private Place place;
	private ActionBar actionBar;
	private CollapsingToolbarLayout collapsingToolbarLayout;
	private RecyclerView rvofferte;
	private OffertaAdapterRV adapter;
	private ArrayList<Offerta> offerte = new ArrayList<Offerta>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_det_pla);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		 
		if (actionBar != null) {
		    actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		    actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		idPlace = (int) getIntent().getExtras().getLong(Place.tag_id);
		setupGUI();
		if(idPlace > 0)
			getPlace(idPlace);
	}
	
	private void setupGUI() {
		// TODO Auto-generated method stub
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
		logo = (SmartImageView) findViewById(R.id.logoo);
		//rvofferte = (RecyclerView) findViewById(R.id.rvofferte);
		//LinearLayoutManager llm = new LinearLayoutManager(DetPlaActivity.this);
		//rvofferte.setLayoutManager(llm);
		//rvofferte.setSaveEnabled(false);
	}
	
	private void getPlace(int idPlace2) {
		// TODO Auto-generated method stub
		if(Tools.isNetworkEnabled(DetPlaActivity.this)) {
			client = new AsyncHttpClient();
			client.get(Tools.GET_DETAIL_URL + idPlace, new JsonHttpResponseHandler(){
	
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					if(response != null)
					{
						place = Place.decodeJSON(response);
						place.id = idPlace;
						updateDetailGUI();
					}
					else
						Toast.makeText(getApplicationContext(), 
								"Errore", Toast.LENGTH_LONG).show();
				}
	
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(DetPlaActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONArray errorResponse) {
					Toast.makeText(DetPlaActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					Toast.makeText(DetPlaActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
				
				private void updateDetailGUI() {
					// TODO Auto-generated method stub
					//actionBar.setTitle(place.nome);
					if(place.nome != null)
						collapsingToolbarLayout.setTitle(place.nome);
					Log.d("aa", place.gallery.get(0));
					logo.setImageUrl(place.gallery.get(0));
//					if(place.gallery.size() > 1)
//					{
//						LinearLayout.LayoutParams imagesLayout = new LinearLayout.LayoutParams(
//							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//						imagesLayout.setMargins(5, 0, 5, 0);
//						for(int i = 1; i < place.gallery.size(); i++)
//						{
//							image_detail = new SmartImageView(DetailPlaceActivity.this);
//							image_detail.setLayoutParams(imagesLayout);
//							image_detail.setScaleType(ScaleType.CENTER_CROP);
//							image_detail.getLayoutParams().width = 100;
//							image_detail.setImageUrl(place.gallery.get(i));
//							final int index_id_dialog = i;
//							image_detail.setOnClickListener(new OnClickListener() {
//							
//								@Override
//								public void onClick(View v) {
//									// TODO Auto-generated method stub
//									DialogImage dialog = DialogImage.newInstance(place.gallery.get(index_id_dialog).toString());
//									dialog.show(getFragmentManager(), Tools.TAG_DIALOG_IMAGE);
//								}
//							});
//							scrollImages.addView(image_detail);
//						}
//					}
//					else
//						scrollImages.setVisibility(View.GONE);
				
//					txtdescrizione.setText(place.description);
//					txtPhone.setText(place.telefono);
//					txtCitta.setText(place.città);
//					btnMappa.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							String url = "http://maps.google.com/maps?"
//									+ "daddr=" + place.lat + "," + place.longit;
//							Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//							startActivity(mapIntent);
//						}
//					});
				}
			});
			Log.d("url offerta", Tools.OFFERS_BY_PLACE + idPlace);
//			client.get(Tools.OFFERS_BY_PLACE + idPlace, new JsonHttpResponseHandler(){
//
//				@Override
//				public void onSuccess(int statusCode, Header[] headers,
//						JSONArray response) {
//					// TODO Auto-generated method stub
//					for(int i = 0; i < response.length(); i++)
//					{
//						Log.d("off", response.toString());
//						try {
//							offerte.add(Offerta.decodeJSON(response.getJSONObject(i)));
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					adapter = new OffertaAdapterRV(DetPlaActivity.this, offerte);
//					if(adapter != null)
//						rvofferte.setAdapter(adapter);
//				}
//				
//				@Override
//				public void onFailure(int statusCode, Header[] headers,
//						String responseString, Throwable throwable) {
//					// TODO Auto-generated method stub
//					super.onFailure(statusCode, headers, responseString, throwable);
//				}
//
//				@Override
//				public void onFailure(int statusCode, Header[] headers,
//						Throwable throwable, JSONArray errorResponse) {
//					// TODO Auto-generated method stub
//					super.onFailure(statusCode, headers, throwable, errorResponse);
//				}
//
//				@Override
//				public void onFailure(int statusCode, Header[] headers,
//						Throwable throwable, JSONObject errorResponse) {
//					// TODO Auto-generated method stub
//					super.onFailure(statusCode, headers, throwable, errorResponse);
//				}
//			});
		}
		else
			Toast.makeText(DetPlaActivity.this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
	}

	
	
}
