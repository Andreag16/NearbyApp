package com.progetto.nearby.Filtri;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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

public class FiltriActivity extends Activity {

	/*private TextView text;
	private Button insert;
	private AsyncHttpClient client;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filtri);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		text = (TextView) findViewById(R.id.textView1);
		insert = (Button) findViewById(R.id.button1);
		client = new AsyncHttpClient();
		client.get("http://nearby.altervista.org/api.php?action=get_places", new JsonHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				text.setText("" + response.toString());
			}
			
		});
		insert.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JSONObject object = new JSONObject();
				StringEntity entity = null;
				try {
					object.put("title", "zecchi gay!");
					entity = new StringEntity(object.toString());
					entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				client.post(getApplicationContext(), "http://nearby.altervista.org/test/index.php/insert_offer", entity, "application/json", new JsonHttpResponseHandler(){

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						Log.d("result", response.toString());
						Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString, throwable);
					}
					
				});
			}
		});
		
	}*/
	
	public final static String TAG = "FILTRI_ACTIVITY";
	
	private SharedPreferences sharedPreferences;
	private boolean canRefresh = false;
	
	private SeekBar seekbarDistanza;
	private Spinner spinnerCategorie;
	private Spinner spinnerSottocategorie;
	private RadioGroup radioTipologia;
	private TextView txtDistanza;
	private Button btnCerca;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filtri);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		sharedPreferences = getSharedPreferences(Tools.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		
		seekbarDistanza = (SeekBar)findViewById(R.id.seek_bar_distanza);
		spinnerCategorie = (Spinner)findViewById(R.id.spinnerCategorie);
		spinnerSottocategorie = (Spinner)findViewById(R.id.spinnerSottocategorie);
		radioTipologia = (RadioGroup)findViewById(R.id.rdoTipologia);
		btnCerca = (Button)findViewById(R.id.btnCerca);
		txtDistanza = (TextView)findViewById(R.id.txtDistanza);
		
		getCategories();
		
		
		// Setta barra della distanza
		int distanza = sharedPreferences.getInt(Tools.PREFERNCES_DISTANZA, Tools.FILTRO_DISTANZA_DEFAULT);
		int progress = getIndexFromDistanza(distanza);
		
		seekbarDistanza.setProgress(progress);
		txtDistanza.setText( distanza < 1000 ? "" + distanza + "m" : "" + distanza / 1000 + "Km");
		
		
		// Setta radioButton tipologia (Attività commerciale o POI)
		boolean flag = sharedPreferences.getBoolean(Tools.PREFERNCES_TIPOLOGIA, true);
		if(flag)
			radioTipologia.check(R.id.rbAC);
		else
			radioTipologia.check(R.id.rbPOI);
		
		
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
				refreshSubcategories();
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
					
					Categories cat;
					for(int i = 0; i < response.length(); i++)
					{
						try {
							jsonCategories = response.getJSONObject(i);
							cat = Categories.decodeJSON(jsonCategories);
							contentProvider.insert(NearbyContentProvider.CATEGORIES_URI, cat.getContentValues());
							lstCategorie.add(cat.name);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}					
					ArrayAdapter<String> categorieAdapter = new ArrayAdapter<String>(FiltriActivity.this, android.R.layout.simple_spinner_dropdown_item, lstCategorie);
					spinnerCategorie.setAdapter(categorieAdapter);
					spinnerCategorie.setSelection(0);
					
					if(canRefresh)
						refreshSubcategories();
					else
						canRefresh = true;
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONArray errorResponse) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
			
			AsyncHttpClient clientSubcategories = new AsyncHttpClient();
			clientSubcategories.get(Tools.SUBCATEGORIES_URL, new JsonHttpResponseHandler(){
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
					JSONObject jsonSubcategories;
					Subcategories subcat;
					ContentResolver contentProvider = FiltriActivity.this.getContentResolver();
					
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
					if(canRefresh)
						refreshSubcategories();
					else
						canRefresh = true;
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONArray errorResponse) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					Toast.makeText(FiltriActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_LONG).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
			
		} else {
			Toast.makeText(this, "Nessuna connessione disponibile!", Toast.LENGTH_LONG).show();
		}
	}


	private void refreshSubcategories() {
		String categoria = spinnerCategorie.getSelectedItem().toString();
		
		Cursor cursor = getContentResolver()
				.query(NearbyContentProvider.CATEGORIES_URI, null, CategoriesTableHelper.name + "='" + categoria + "'", null, null);
		if(cursor != null) {
			cursor.moveToFirst();
			int category_id = cursor.getInt(cursor.getColumnIndex(CategoriesTableHelper._ID));
			cursor.close();
			
			cursor = getContentResolver()
					.query(NearbyContentProvider.SUBCATEGORIES_URI, null, SubcategoriesTableHelper.category_id + "=" + category_id, null, SubcategoriesTableHelper.name);
			cursor.moveToFirst();

			List<String> lstSubcategorie = new ArrayList<String>();
			do {
				lstSubcategorie.add(cursor.getString(cursor.getColumnIndex(SubcategoriesTableHelper.name)));
			} while (cursor.moveToNext());
			ArrayAdapter<String> categorieAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstSubcategorie);
			spinnerSottocategorie.setAdapter(categorieAdapter);
			cursor.close();
		}
	}

	private void saveFilters() {
		int idCategoria = sharedPreferences.getInt(Tools.PREFERNCES_CATEGORIA, -1);
		int idSottocategoria = sharedPreferences.getInt(Tools.PREFERNCES_SOTTOCATEGORIA, -1);
		
		// Salva distanza
		int distanza = getDistanzaFromIndex(seekbarDistanza.getProgress());
		
		
		// Salva categoria
		if(spinnerCategorie.getSelectedItem() != null) {
			String strCategoria = spinnerCategorie.getSelectedItem().toString();
			Cursor cursor = getContentResolver().query(NearbyContentProvider.CATEGORIES_URI, null, CategoriesTableHelper.name + "='" + strCategoria + "'", null, null);
			cursor.moveToFirst();
			idCategoria = cursor.getInt(cursor.getColumnIndex(CategoriesTableHelper._ID));
			cursor.close();
		}
		
		
		// Salva sottocategoria
		if(spinnerSottocategorie != null) {
			String strSottocategoria = spinnerSottocategorie.getSelectedItem().toString();
			Cursor cursor = getContentResolver().query(NearbyContentProvider.SUBCATEGORIES_URI, null, SubcategoriesTableHelper.name + "='" + strSottocategoria + "'", null, null);
			cursor.moveToFirst();
			idSottocategoria = cursor.getInt(cursor.getColumnIndex(SubcategoriesTableHelper._ID));
			cursor.close();
		}
		
		
		// Salva tipologia (Attività commerciale o POI)
		// true = selezionato "Attivita commerciale"
		// false = selezionato "POI"
		boolean flag = radioTipologia.getCheckedRadioButtonId() == R.id.rbAC;
		
		
		sharedPreferences.edit()
		.putInt(Tools.PREFERNCES_DISTANZA, distanza)
		.putInt(Tools.PREFERNCES_CATEGORIA, idCategoria)
		.putInt(Tools.PREFERNCES_SOTTOCATEGORIA, idSottocategoria)
		.putBoolean(Tools.PREFERNCES_TIPOLOGIA, flag)
		.apply();		
	}
	
	
	@Override
	protected void onDestroy() { // Salvo lo stato dei filtri all'uscita dell'activity
		saveFilters();		
		super.onDestroy();
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
