package com.progetto.nearby.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.progetto.nearby.DatabaseLocale.SubcategoriesTableHelper;

import android.content.ContentValues;

public class Subcategories {

	public static final String tag_id = "id_subcategory";
	public static final String tag_name = "subcategory_name";
	public static final String tag_id_category = "id_category";
	
	
	public int id;
	public String name;
	public int category_id;
	
	public Subcategories(int id, String name, int category_id) {
		this.id = id;
		this.name = name;
		this.category_id = category_id;
	}
	
	public static Subcategories decodeJSON(JSONObject obj) {
		try {
			int id = obj.getInt(tag_id);
			String name = obj.getString(tag_name);
			int category = obj.getInt(tag_id_category);
			return new Subcategories(id, name, category);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SubcategoriesTableHelper._ID, this.id);
		contentValues.put(SubcategoriesTableHelper.name, this.name);
		contentValues.put(SubcategoriesTableHelper.category_id, this.category_id);
		return contentValues;
	}
}
