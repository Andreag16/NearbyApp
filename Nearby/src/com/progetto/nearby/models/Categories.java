package com.progetto.nearby.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.progetto.nearby.DatabaseLocale.CategoriesTableHelper;

import android.content.ContentValues;


public class Categories {
	
	public static final String tag_id = "id_category";
	public static final String tag_name = "category_name";
	
	
	public int id;
	public String name;
	
	
	public Categories(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static Categories decodeJSON(JSONObject obj) {
		try {
			int id = obj.getInt(tag_id);
			String name = obj.getString(tag_name);
			return new Categories(id, name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(CategoriesTableHelper._ID, this.id);
		contentValues.put(CategoriesTableHelper.name, this.name);
		return contentValues;
	}
}
