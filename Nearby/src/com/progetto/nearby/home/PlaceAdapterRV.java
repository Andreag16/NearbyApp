package com.progetto.nearby.home;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.progetto.nearby.R;
import com.progetto.nearby.models.Place;

public class PlaceAdapterRV extends RecyclerView.Adapter<PlaceAdapterRV.PlaceViewHolder> {

	private static Context context;
	private ArrayList<Place> places;
	
	public PlaceAdapterRV(Context context, ArrayList<Place> places) {
		super();
		this.context = context;
		this.places = places;
	}

	public static class PlaceViewHolder extends RecyclerView.ViewHolder 
			implements View.OnClickListener {      
        CardView cv;
        SmartImageView logo;
        TextView nomePlace, citta, distanza;
 
        PlaceViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView)itemView.findViewById(R.id.cv);
            logo = (SmartImageView)itemView.findViewById(R.id.imgPlace);
            nomePlace = (TextView)itemView.findViewById(R.id.txtNomePlace);
            citta = (TextView)itemView.findViewById(R.id.txtCittaPlace);
            distanza = (TextView)itemView.findViewById(R.id.txtDistanzaPlace);
        }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "aa", Toast.LENGTH_LONG).show();
		}
    }

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return places.size();
	}

	@Override
	public void onBindViewHolder(PlaceViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.logo.setImageUrl(places.get(arg1).urlImg);
		arg0.nomePlace.setText(places.get(arg1).nome);
		arg0.citta.setText(places.get(arg1).città);
	}

	@Override
	public PlaceViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		View place_view = inflater.inflate(R.layout.cell_places, null);
		PlaceViewHolder placeholder = new PlaceViewHolder(place_view);
		return placeholder;
	}
}
