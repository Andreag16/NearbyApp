package com.progetto.nearby.detailPlaces;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.loopj.android.image.SmartImageView;

public class imageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> gallery;
	private int idPlace;
	private ImageView imageView;
	//private LoadImage loadImage;
	private Bitmap bitmap;
	
	public imageAdapter(Context context, ArrayList<String> gallery) {
		super();
		this.context = context;
		this.gallery = gallery;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gallery.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		SmartImageView image = new SmartImageView(context);
//		imageView = new ImageView(context);
//		loadImage = (LoadImage) new LoadImage().execute(gallery.get(arg0));
		//Log.d("url", gallery.get(arg0));
		image.setImageUrl(gallery.get(arg0));
//		URL url;
//		try {
//			url = new URL(gallery.get(arg0));
//			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//			imageView.setImageBitmap(bmp);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//return imageView;
		return image;
	}
	
	/*private class LoadImage extends AsyncTask<String, String, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				bitmap = BitmapFactory.decodeStream(new URL(params[0].toString()).openConnection().getInputStream());
                //bitmap = BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
			} catch (Exception e) {
               e.printStackTrace();
			}
         return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result != null)
				{
				imageView.setImageBitmap(result);
				DetailPlaceActivity.galleryPlace.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
				DetailPlaceActivity.galleryPlace.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				}
		}
		
	}*/

}
