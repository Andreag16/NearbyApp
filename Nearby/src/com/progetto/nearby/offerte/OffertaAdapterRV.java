package com.progetto.nearby.offerte;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.progetto.nearby.R;
import com.progetto.nearby.models.Offerta;

public class OffertaAdapterRV extends RecyclerView.Adapter<OffertaAdapterRV.OffertaViewHolder>{

	private Context context;
	private ArrayList<Offerta> offerts;
	
	public OffertaAdapterRV(Context context, ArrayList<Offerta> allOfferts) {
		super();
		this.context = context;
		this.offerts = allOfferts;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return offerts.size();
	}

	@Override
	public void onBindViewHolder(OffertaViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.nomeOfferta.setText(offerts.get(arg1).nome);
		arg0.descrizioneOfferta.setText(offerts.get(arg1).descrizione);
		if(!(offerts.get(arg1).distanza == ""))
			arg0.distanza.setText(offerts.get(arg1).distanza);
		else
			arg0.distanza.setVisibility(View.INVISIBLE);
	}

	@Override
	public OffertaViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		View offerta_view = inflater.inflate(R.layout.cell_offerta, null);
		OffertaViewHolder offerta_holder = new OffertaViewHolder(offerta_view);
		return offerta_holder;
	}

	public static class OffertaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {      
        CardView cv;
        TextView nomeOfferta, descrizioneOfferta, distanza;
        OnItemClickListener mItemClickListener;
 
        OffertaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nomeOfferta = (TextView)itemView.findViewById(R.id.txtNomeOfferta);
            descrizioneOfferta = (TextView)itemView.findViewById(R.id.txtDescrizioneOfferta);
            distanza = (TextView) itemView.findViewById(R.id.txtDistanzaOff);
        }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mItemClickListener.onItemClick(v, getPosition());
		}
		
		public interface OnItemClickListener {
	        public void onItemClick(View view , int position);
	    }

	    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
	        this.mItemClickListener = mItemClickListener;
	    }
    }

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		// TODO Auto-generated method stub
		super.onAttachedToRecyclerView(recyclerView);
	}
}
