package com.progetto.nearby.offerte;

import java.util.ArrayList;

import com.progetto.nearby.R;
import com.progetto.nearby.models.Offerta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OffertaAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Offerta> offerte;
	
	private class OffertaViewHolder
	{
		TextView txtNomeOfferta;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return offerte.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return offerte.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		Offerta offerta = (Offerta) getItem(arg0);
		return offerta.id;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		OffertaViewHolder offertaholder = new OffertaViewHolder();
		View cellofferta = null;
		if(arg1 != null)
			cellofferta = arg1;
		else
		{
			cellofferta = inflater.inflate(R.layout.cell_offerta, null);
			offertaholder.txtNomeOfferta = (TextView) cellofferta.findViewById(R.id.txtNomeOfferta);
			cellofferta.setTag(offertaholder);
		}
		Offerta offerta = (Offerta) getItem(arg0);
		OffertaViewHolder getoffertaholder = (OffertaViewHolder) cellofferta.getTag();
		getoffertaholder.txtNomeOfferta.setText(offerta.nomeofferta);
		return cellofferta;
	}

}
