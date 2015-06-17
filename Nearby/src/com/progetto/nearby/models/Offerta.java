package com.progetto.nearby.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Offerta {

	public static final String tag_id = "id_offer";
	public static final String tag_name = "title";
	public static final String tag_description = "offer_description";
	
	public int id;
	public String nome;
	public String descrizione;
	
	public static Offerta decodeJSON(JSONObject offert) throws JSONException
	{
		Offerta offerta = new Offerta();
		offerta.id = offert.getInt(tag_id);
		offerta.nome = offert.getString(tag_name);
		offerta.descrizione = offert.getString(tag_description);
		return offerta;
	}
	
}
