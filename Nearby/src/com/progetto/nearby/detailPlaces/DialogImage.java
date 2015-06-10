package com.progetto.nearby.detailPlaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.loopj.android.image.SmartImageView;
import com.progetto.nearby.Tools;

public class DialogImage extends DialogFragment {

	public static DialogImage newInstance(String url)
	{
		DialogImage image = new DialogImage();
		Bundle image_bundle = new Bundle();
		image_bundle.putString(Tools.URL_IMMAGINE_DETTAGLIO, url);
		image.setArguments(image_bundle);
		return image;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AlertDialog.Builder image_dialog_builder = new AlertDialog.Builder(getActivity());
		SmartImageView image = new SmartImageView(getActivity());
		Bundle getBundle = getArguments();
		if(getBundle != null)
			image.setImageUrl(getBundle.getString(Tools.URL_IMMAGINE_DETTAGLIO));
		image_dialog_builder.setPositiveButton("Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
        image_dialog_builder.setView(image);
		Dialog image_dialog = image_dialog_builder.create();
		return image_dialog;
	}
	
}
