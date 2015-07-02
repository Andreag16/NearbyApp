package com.progetto.nearby.dettaglioOfferta;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.models.Offerta;

public class DettaglioOffertaActivity extends AppCompatActivity {

	private AsyncHttpClient client;
	private Offerta offerta;
	private TextView nomeofferta, descrofferta, nomeposto, cittaposto, indirizzoposto;
	private FloatingActionButton btnMappa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dettaglio_offerta);
		getSupportActionBar().setTitle("Offerta");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		int idofferta = (int) getIntent().getExtras().getLong(Offerta.tag_id);
		setupGUI();
		if(idofferta > 0)
			getdettaglioOfferta(idofferta);
	}

	private void getdettaglioOfferta(final int idofferta) {
		// TODO Auto-generated method stub
		client = new AsyncHttpClient();
		if(Tools.isNetworkEnabled(getApplicationContext()))
		{
			client.get(Tools.GET_DETAIL_OFFERTA_URL + idofferta, new JsonHttpResponseHandler(){

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					try {
						offerta = Offerta.decodeJSON(response);
						offerta.id = idofferta;
						updateDettaglioGUI();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				private void updateDettaglioGUI() {
					// TODO Auto-generated method stub
					nomeofferta.setText(offerta.nomeofferta);
					descrofferta.setText(offerta.descrizione);
					nomeposto.setText(offerta.nomepostoofferta);
					cittaposto.setText(offerta.nomecittaofferta);
					indirizzoposto.setText(offerta.indirizzoposto);
					btnMappa.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(offerta.lat > 0 && offerta.longit > 0)
							{
								String url = "http://maps.google.com/maps?"
										+ "daddr=" + offerta.lat + "," + offerta.longit;
								Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
								startActivity(mapIntent);
							}
						}
					});
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
		}
		else
			Toast.makeText(getApplicationContext(), "Nessuna connessione ad internet trovata", Toast.LENGTH_LONG).show();
	}

	private void setupGUI() {
		// TODO Auto-generated method stub
		nomeofferta = (TextView) findViewById(R.id.txtDetOffNomeOfferta);
		descrofferta = (TextView) findViewById(R.id.txtDescrOff);
		nomeposto = (TextView) findViewById(R.id.txtDetOffNomePosto);
		cittaposto = (TextView) findViewById(R.id.txtDetOffertaTown);
		indirizzoposto = (TextView) findViewById(R.id.txtDetOffertaIndirizzo);
		btnMappa = (FloatingActionButton) findViewById(R.id.btnMappaOfferta);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			default: 
				return true;
		}
	}
	
	
}
