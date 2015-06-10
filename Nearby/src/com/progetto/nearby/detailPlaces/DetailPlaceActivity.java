package com.progetto.nearby.detailPlaces;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.models.Place;

public class DetailPlaceActivity extends Activity {

	private LinearLayout scrollImages;
	private TextView txtNome, txtdescrizione, txtGPS, txtPhone, txtCitta;
	private imageAdapter imageAdapter;
	private SmartImageView logo, image_detail;
	private int idPlace;
	private Place place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		txtGPS = (TextView) findViewById(R.id.txtDetGPS);
		txtPhone = (TextView) findViewById(R.id.txtDetTelephone);
		txtCitta = (TextView) findViewById(R.id.txtDetTown);
	}

	private void getPlace(final int idPlace) {
		// TODO Auto-generated method stub
		if(Tools.isNetworkEnabled(DetailPlaceActivity.this)) {
		AsyncHttpClient client = new AsyncHttpClient();
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
				//imageAdapter = new imageAdapter(DetailPlaceActivity.this, place.gallery);
				//galleryPlace.setAdapter(imageAdapter);
//				galleryPlace.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
//				galleryPlace.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				txtNome.setText(place.nome);
				txtdescrizione.setText(place.description);
				txtGPS.setText("Lat: " + place.lat + " Long: " + place.longit);
				txtPhone.setText(place.telefono);
				txtCitta.setText(place.città);
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
			
		});
		}
		else
			Toast.makeText(DetailPlaceActivity.this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
	}
}
