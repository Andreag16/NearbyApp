package com.progetto.nearby.models;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class Offerta {

	public static final String tag_id = "id_offer";
	public static final String tag_name = "title";
	public static final String tag_description = "offer_description";
	public static final String tag_distanza = "distance";
	
	public int id;
	public String nome;
	public String descrizione;
	public String distanza;
	
	public static Offerta decodeJSON(JSONObject offert) throws JSONException
	{
		Offerta offerta = new Offerta();
		offerta.id = offert.getInt(tag_id);
		offerta.nome = offert.getString(tag_name);
		offerta.descrizione = offert.getString(tag_description);
		if(offert.has(tag_distanza))
			offerta.distanza = distance(offert.getDouble(tag_distanza));
		else
			offerta.distanza = "";
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
	
}
