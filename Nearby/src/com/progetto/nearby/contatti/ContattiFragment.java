package com.progetto.nearby.contatti;

import com.progetto.nearby.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContattiFragment extends Fragment {
	
	public static final String TAG = "contattifragment";
	
	public static ContattiFragment newInstance()
	{
		return new ContattiFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View contatti = inflater.inflate(R.layout.fragment_contatti, null);
		return contatti;
	}
}
