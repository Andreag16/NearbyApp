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
	
	public int id;
	public String nomeofferta;
	public String descrizione;
	public String nomepostoofferta;
	public String nomecittaofferta;
	public String distanza;
	
	public static Offerta decodeJSON(JSONObject offert) throws JSONException
	{
		Offerta offerta = new Offerta();
		offerta.id = offert.getInt(tag_id);
		offerta.nomeofferta = offert.getString(tag_nomeofferta);
		offerta.descrizione = offert.getString(tag_description);
		if(offert.has(tag_nomeposto) && offert.has(tag_nomecittaposto)
				&& offert.has(tag_distanza))
		{
			offerta.nomepostoofferta = offert.getString(tag_nomeposto);
			offerta.nomecittaofferta = offert.getString(tag_nomecittaposto);
			offerta.distanza = distance(offert.getDouble(tag_distanza));
		}
		else
		{
			offerta.nomepostoofferta = "";
			offerta.nomecittaofferta = "";
			offerta.distanza = "";
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
