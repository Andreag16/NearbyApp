package com.progetto.nearby.navigationdrawer;

import java.util.ArrayList;

import com.progetto.nearby.R;

import android.content.res.Resources;

public class DrawerItemObject {
	public int icon;
    public String name;
    public int id;
 
    public DrawerItemObject(int id,String name,int icon) {
        this.icon = icon;
        this.name = name;
        this.id = id;
    }
    
    public static ArrayList<DrawerItemObject> createNavigationDrawerArrayOfItems(Resources resources){	
    	ArrayList<DrawerItemObject> data = new ArrayList<DrawerItemObject>();
    	DrawerItemObject home= new DrawerItemObject(0,resources.getString(R.string.title_section1),R.drawable.home);
    	data.add(home);
    	DrawerItemObject offerte = new DrawerItemObject(1,resources.getString(R.string.title_section2),R.drawable.offerte);
    	data.add(offerte);
    	DrawerItemObject ar = new DrawerItemObject(2,resources.getString(R.string.title_section3),R.drawable.contatti);
    	data.add(ar);
    	DrawerItemObject contatti = new DrawerItemObject(3,resources.getString(R.string.title_section4),R.drawable.contatti);
    	data.add(contatti);
    	return data;
    	}
}
