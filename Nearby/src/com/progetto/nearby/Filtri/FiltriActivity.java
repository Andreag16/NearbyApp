package com.progetto.nearby.Filtri;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.DatabaseLocale.CategoriesTableHelper;
import com.progetto.nearby.DatabaseLocale.NearbyContentProvider;
import com.progetto.nearby.DatabaseLocale.SubcategoriesTableHelper;
import com.progetto.nearby.models.Categories;
import com.progetto.nearby.models.Subcategories;

public class FiltriActivity extends AppCompatActivity {

	public final static String TAG = "FILTRI_ACTIVITY";
	
	private SharedPreferences sharedPreferences;
	
	private SeekBar seekbarDistanza;
	private Spinner spinnerCategorie;
	private Spinner spinnerSottocategorie;
	private TextView txtDistanza;
	private CheckBox chkAC;
	private CheckBox chkPOI;
	private Button btnCerca;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filtri);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		sharedPreferences = getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		
		seekbarDistanza = (SeekBar)findViewById(R.id.seek_bar_distanza);
		spinnerCategorie = (Spinner)findViewById(R.id.spinnerCategorie);
		spinnerSottocategorie = (Spinner)findViewById(R.id.spinnerSottocategorie);
		btnCerca = (Button)findViewById(R.id.btnCerca);
		txtDistanza = (TextView)findViewById(R.id.txtDistanza);
		chkAC = (CheckBox)findViewById(R.id.chkAC);
		chkPOI = (CheckBox)findViewById(R.id.chkPOI);
		
		getCategories();
		
		
		// Setta barra della distanza
		int distanza = sharedPreferences.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);
		int progress = getIndexFromDistanza(distanza);
		
		seekbarDistanza.setProgress(progress);
		txtDistanza.setText( distanza < 1000 ? "" + distanza + "m" : "" + distanza / 1000 + "Km");
		
		
		// Setta radioButton tipologia (Attività commerciale o POI)
		String tipologia = sharedPreferences.getString(Tools.PREFERNCES_TIPOLOGIA, "all");
		chkAC.setChecked(tipologia.equals("all") || tipologia.equals("ac"));
		chkPOI.setChecked(tipologia.equals("all") || tipologia.equals("poi"));
		
		
		btnCerca.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveFilters();
				finish();
			}
		});
		
		seekbarDistanza.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int distanza = getDistanzaFromIndex(progress);
				txtDistanza.setText( distanza < 1000 ? "" + distanza + "m" : "" + distanza / 1000 + "Km");
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
		});
		
		spinnerCategorie.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = getContentResolver()
						.query(NearbyContentProvider.SUBCATEGORIES_URI,
								null,
								SubcategoriesTableHelper.category_id + "=" + id + " OR " + SubcategoriesTableHelper.category_id + "=-1",
								null,
								SubcategoriesTableHelper.name);
				cursor.moveToFirst();

				SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(FiltriActivity.this,
							android.R.layout.simple_spinner_item,
							cursor,
							new String[]{SubcategoriesTableHelper.name},
							new int[]{android.R.id.text1},
							0);
				
				cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerSottocategorie.setAdapter(cursorAdapter);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
	}
	

	private void getCategories() {
		if(Tools.isNetworkEnabled(this)) {
			getContentResolver().delete(NearbyContentProvider.CATEGORIES_URI, null, null);
			getContentResolver().delete(NearbyContentProvider.SUBCATEGORIES_URI, null, null);
			
			AsyncHttpClient clientCategories = new AsyncHttpClient();
			clientCategories.get(Tools.CATEGORIES_URL, new JsonHttpResponseHandler(){
	
				@Override
				public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
					JSONObject jsonCategories;
					List<String> lstCategorie = new ArrayList<String>();
					ContentResolver contentProvider = FiltriActivity.this.getContentResolver();
					lstCategorie.add("Tutte");
					contentProvider.insert(NearbyContentProvider.CATEGORIES_URI, new Categories(-1, "Tutte").getContentValues());
					
					Categories cat;
					for(int i = 0; i < response.length(); i++)
					{
						try {
							jsonCategories = response.getJSONObject(i);
							cat = Categories.decodeJSON(jsonCategories);
							contentProvider.insert(NearbyContentProvider.CATEGORIES_URI, cat.getContentValues());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					Cursor cursor = contentProvider.query(NearbyContentProvider.CATEGORIES_URI, null, null, null, null);
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(FiltriActivity.this,
								android.R.layout.simple_spinner_item,
								cursor,
								new String[]{CategoriesTableHelper.name},
								new int[]{android.R.id.text1},
								0);
					
					spinnerCategorie.setAdapter(cursorAdapter);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
			
			AsyncHttpClient clientSubcategories = new AsyncHttpClient();
			clientSubcategories.get(Tools.SUBCATEGORIES_URL, new JsonHttpResponseHandler(){
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
					JSONObject jsonSubcategories;
					Subcategories subcat;
					ContentResolver contentProvider = FiltriActivity.this.getContentResolver();
					contentProvider.insert(NearbyContentProvider.SUBCATEGORIES_URI, new Subcategories(-1, "Tutte", -1).getContentValues());
					
					for(int i = 0; i < response.length(); i++)
					{
						try {
							jsonSubcategories = response.getJSONObject(i);
							subcat = Subcategories.decodeJSON(jsonSubcategories);
							contentProvider.insert(NearbyContentProvider.SUBCATEGORIES_URI, subcat.getContentValues());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
			
		} else {
			Toast.makeText(this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
	}

	private void saveFilters() {
		long idCategoria = sharedPreferences.getLong(Tools.PREFERNCES_CATEGORIA, -1);
		long idSottocategoria = sharedPreferences.getLong(Tools.PREFERNCES_SOTTOCATEGORIA, -1);
		
		// Salva distanza
		int distanza = getDistanzaFromIndex(seekbarDistanza.getProgress());
		
		
		// Salva categoria
		if(spinnerCategorie.getSelectedItem() != null) {
			idCategoria = spinnerCategorie.getSelectedItemId();
		}
		
		
		// Salva sottocategoria
		if(spinnerSottocategorie.getSelectedItem() != null) {
			idSottocategoria = spinnerSottocategorie.getSelectedItemId();
		}
		
		
		// Salva tipologia (ac poi all)
		String tipologia = "all";
		if(chkAC.isChecked() && !chkPOI.isChecked()) {
			tipologia = "ac";
		} else if (chkPOI.isChecked() && !chkAC.isChecked()) {
			tipologia = "poi";
		}
		
		
		sharedPreferences.edit()
			.putInt(Tools.PREFERNCES_DISTANZA, distanza)
			.putLong(Tools.PREFERNCES_CATEGORIA, idCategoria)
			.putLong(Tools.PREFERNCES_SOTTOCATEGORIA, idSottocategoria)
			.putString(Tools.PREFERNCES_TIPOLOGIA, tipologia)
			.apply();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
        	onBackPressed();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	private int getDistanzaFromIndex(int index) {
		switch (index) {
			case 0:
				return 500;
			case 1:
				return 1000;
			case 2:
				return 2000;
			case 3:
			default:
				return 5000;
			case 4:
				return 10000;
			case 5:
				return 20000;
			case 6:
				return 50000;
		}
	}

	private int getIndexFromDistanza(int distanza) {
		switch (distanza) {
			case 500:
				return 0;
			case 1000:
				return 1;
			case 2000:
				return 2;
			case 5000:
			default:
				return 3;
			case 10000:
				return 4;
			case 20000:
				return 5;
			case 50000:
				return 6;
		}
	}
}
