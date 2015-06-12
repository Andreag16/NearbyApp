package com.progetto.nearby;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Tools {
	
	public static String SERVICE_URL = "http://nearby.altervista.org";
	public static String PLACES_URL = SERVICE_URL + "/test/index.php/places";
	public static String OFFERTS_URl = SERVICE_URL + "/test/index.php/offerbygps";
	public static String OFFERTS_BY_PLACE = SERVICE_URL + "/test/index.php/offerbyplace/";
	public static String CATEGORIES_URL = SERVICE_URL + "/test/index.php/categories";
	public static String SUBCATEGORIES_URL = SERVICE_URL + "/test/index.php/subcategories";
	public static String GET_IMAGE_URL = SERVICE_URL + "/images/";
	public static String GET_DETAIL_URL = SERVICE_URL + "/test/index.php/place/";
	
	public static final String PREFERENCES_FILE_NAME = "nearbypreferences";
	public static final String PREFERNCES_DISTANZA = "distanza";
	public static final String PREFERNCES_CATEGORIA = "categoria";
	public static final String PREFERNCES_SOTTOCATEGORIA = "sottocategoria";
	public static final String PREFERNCES_TIPOLOGIA = "tipologia";
	public static final String URL_IMMAGINE_DETTAGLIO = "immagine dettaglio";
	public static final String TAG_DIALOG_IMAGE = "dialog image";
	
	public static final int FILTRO_DISTANZA_DEFAULT = 1000;
	
	
	public static GPSProvider gpsProvider;
	private static NetworkInfo networkInfo;
	private static ConnectivityManager connectivityManager;
	
	public static boolean isNetworkEnabled(Context context) {
		connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}
}