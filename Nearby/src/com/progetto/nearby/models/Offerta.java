package com.progetto.nearby.models;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class Offerta implements Comparable<Offerta> {

	public static final String tag_id = "id_offer";
	public static final String tag_nomeofferta = "title";
	public static final String tag_description = "offer_description";
	public static final String tag_nomeposto = "place_name";
	public static final String tag_nomecittaposto = "town_name";
	public static final String tag_distanza = "distance";
	public static final String tag_indirizzo = "place_address";
	public static final String tag_gps = "gps";
	
	public int id;
	public String nomeofferta;
	public String descrizione;
	public String nomepostoofferta;
	public String nomecittaofferta;
	public String distanza;
	public String indirizzoposto;
	public float lat;
	public float longit;
	
	public static Offerta decodeJSON(JSONObject offert) throws JSONException
	{
		Offerta offerta = new Offerta();
		if(offert.has(tag_id))
			offerta.id = offert.getInt(tag_id);
		else
			offerta.id = 0;
		offerta.nomeofferta = offert.getString(tag_nomeofferta);
		offerta.descrizione = offert.getString(tag_description);
		if(offert.has(tag_nomeposto))
			offerta.nomepostoofferta = offert.getString(tag_nomeposto);
		else
			offerta.nomepostoofferta = "";
		if(offert.has(tag_nomecittaposto))
			offerta.nomecittaofferta = offert.getString(tag_nomecittaposto);
		else
			offerta.nomecittaofferta = "";
		if(offert.has(tag_distanza))
			offerta.distanza = distance(offert.getDouble(tag_distanza));
		else
			offerta.distanza = "";
		if(offert.has(tag_indirizzo))
			offerta.indirizzoposto = offert.getString(tag_indirizzo);
		else
			offerta.indirizzoposto = "";
		if(offert.has(tag_gps))
		{
			String gps = offert.getString(tag_gps);
			String[] gpssplit = gps.split("\\+");
			offerta.lat = Float.parseFloat(gpssplit[0]);
			offerta.longit = Float.parseFloat(gpssplit[1]);
		}
		else
		{
			offerta.lat = 0;
			offerta.longit = 0;
		}
		return offerta;
	}
	
	private static String distance(double value)
	{
		BigDecimal bd = null;
		String dist = "";
		if(value >= 1000)
		{
			bd = new BigDecimal(value/1000);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			dist = bd.floatValue() + " Km";
		}
		else if(value < 1000)
		{
			bd = new BigDecimal(value);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			dist = bd.floatValue() + " m";
		}
        return dist;
	}

	@Override
	public int compareTo(Offerta another) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
