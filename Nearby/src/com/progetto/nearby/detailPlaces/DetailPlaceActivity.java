package com.progetto.nearby.detailPlaces;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.models.Offerta;
import com.progetto.nearby.models.Place;
import com.progetto.nearby.offerte.OffertaAdapterRV;

public class DetailPlaceActivity extends AppCompatActivity {

	private LinearLayout scrollImages;
	private TextView txtNome, txtdescrizione, txtPhone, txtCitta;
	private Button btnMappa;
	private RecyclerView rvofferte;
	private ArrayList<Offerta> offerte = new ArrayList<Offerta>();
	private OffertaAdapterRV adapter;
	private AsyncHttpClient client;
	private SmartImageView logo, image_detail;
	private int idPlace;
	private Place place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		idPlace = (int) getIntent().getExtras().getLong(Place.tag_id);
		if(idPlace > 0)
			getPlace(idPlace);
		setupGUI();
	}

	private void setupGUI() {
		// TODO Auto-generated method stub
		//galleryPlace = (Gallery) findViewById(R.id.galleryPlace);
		scrollImages = (LinearLayout) findViewById(R.id.images);
		logo = (SmartImageView) findViewById(R.id.logo);
		txtNome = (TextView) findViewById(R.id.txtDetNome);
		txtdescrizione = (TextView) findViewById(R.id.txtDetDescr);
		txtPhone = (TextView) findViewById(R.id.txtDetTelephone);
		txtCitta = (TextView) findViewById(R.id.txtDetTown);
		btnMappa = (Button) findViewById(R.id.btnMappa);
		rvofferte = (RecyclerView) findViewById(R.id.rvoffertsplace);
		LinearLayoutManager llm = new LinearLayoutManager(DetailPlaceActivity.this);
		rvofferte.setLayoutManager(llm);
	}

	private void getPlace(final int idPlace) {
		// TODO Auto-generated method stub
		if(Tools.isNetworkEnabled(DetailPlaceActivity.this)) {
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
				Toast.makeText(DetailPlaceActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
				super.onFailure(statusCode, headers, responseString, throwable);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				Toast.makeText(DetailPlaceActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(DetailPlaceActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
			
			private void updateDetailGUI() {
				// TODO Auto-generated method stub
				logo.setImageUrl(place.gallery.get(0));
				if(place.gallery.size() > 1)
				{
					LinearLayout.LayoutParams imagesLayout = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					imagesLayout.setMargins(5, 0, 5, 0);
					for(int i = 1; i < place.gallery.size(); i++)
					{
						image_detail = new SmartImageView(DetailPlaceActivity.this);
						image_detail.setLayoutParams(imagesLayout);
						image_detail.setScaleType(ScaleType.CENTER_CROP);
						image_detail.getLayoutParams().width = 100;
						image_detail.setImageUrl(place.gallery.get(i));
						final int index_id_dialog = i;
						image_detail.setOnClickListener(new OnClickListener() {
						
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								DialogImage dialog = DialogImage.newInstance(place.gallery.get(index_id_dialog).toString());
								dialog.show(getFragmentManager(), Tools.TAG_DIALOG_IMAGE);
							}
						});
						scrollImages.addView(image_detail);
					}
				}
				
				client.get(Tools.OFFERS_BY_PLACE + idPlace, new JsonHttpResponseHandler(){

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub
						for(int i = 0; i < response.length(); i++)
						{
							Log.d("off", response.toString());
							try {
								offerte.add(Offerta.decodeJSON(response.getJSONObject(i)));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							adapter = new OffertaAdapterRV(DetailPlaceActivity.this, offerte);
							rvofferte.setAdapter(adapter);
						}
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString, throwable);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}

					
					
				});
				//imageAdapter = new imageAdapter(DetailPlaceActivity.this, place.gallery);
				//galleryPlace.setAdapter(imageAdapter);
//				galleryPlace.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
//				galleryPlace.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				txtNome.setText(place.nome);
				txtdescrizione.setText(place.description);
				txtPhone.setText(place.telefono);
				txtCitta.setText(place.città);
				btnMappa.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String url = "http://maps.google.com/maps?"
								+ "daddr=" + place.lat + "," + place.longit;
						Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(mapIntent);
					}
				});
			}
		});
		}
		else
			Toast.makeText(DetailPlaceActivity.this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
	}
}
