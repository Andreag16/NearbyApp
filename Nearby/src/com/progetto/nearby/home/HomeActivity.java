package com.progetto.nearby.home;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.progetto.nearby.GpsService;
import com.progetto.nearby.R;
import com.progetto.nearby.Tools;
import com.progetto.nearby.AR.ARActivity;
import com.progetto.nearby.Filtri.FiltriActivity;
import com.progetto.nearby.GpsService.LocalBinder;
import com.progetto.nearby.contatti.ContattiFragment;
import com.progetto.nearby.navigationdrawer.NavigationDrawerFragment;
import com.progetto.nearby.offerte.OfferteFragment;

public class HomeActivity extends AppCompatActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	private CharSequence mTitle;
	private HomeFragment homefragment;
	private OfferteFragment offertefragment;
	private FragmentManager fragmentmanager;
	private ContattiFragment contattifragment;
	
	
	public GpsService myService;
    public boolean isBound = false;
	private ServiceConnection myConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        myService = binder.getService();
	        isBound = true;
	        if(homefragment != null) {
	        	homefragment.onServiceConnected();
	        }
	    }
	    
	    public void onServiceDisconnected(ComponentName arg0) {
	        isBound = false;
	    }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent(this, GpsService.class);
		//startService(intent);
		bindService(intent, myConnection, BIND_AUTO_CREATE);

		setContentView(R.layout.activity_home);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		fragmentmanager = getFragmentManager();
		switch(position)
		{
			case 0:
			{
				if(fragmentmanager.findFragmentByTag(HomeFragment.TAG) == null)
				{
					Bundle bundle = null;
					homefragment = HomeFragment.newInstance(bundle);
				}
				else
					homefragment = (HomeFragment) fragmentmanager.findFragmentByTag(HomeFragment.TAG);
					
				fragmentmanager
					.beginTransaction()
					.replace(R.id.container, homefragment, HomeFragment.TAG)
					.commit();
			}
				break;
			case 1:
			{
				if(fragmentmanager.findFragmentByTag(OfferteFragment.TAG) == null)
				{
					Bundle bundle = null;
					offertefragment = OfferteFragment.newInstance(bundle);
				}
				else
					offertefragment = (OfferteFragment) fragmentmanager.findFragmentByTag(OfferteFragment.TAG);
				
				fragmentmanager
					.beginTransaction()
					.replace(R.id.container, offertefragment, OfferteFragment.TAG)
					.commit();
			}
				break;
			case 2:
			{
				Intent aRIntent = new Intent(this, ARActivity.class);
				startActivity(aRIntent);
			}
				break;
			case 3:
			{
				String url = "http://nearby.altervista.org/session/administration.php";
				Intent areaRiservataIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(areaRiservataIntent);
			}
				break;
			case 4:
			{
				if(fragmentmanager.findFragmentByTag(ContattiFragment.TAG) == null)
				{
					contattifragment = ContattiFragment.newInstance();
				}
				else
					contattifragment = (ContattiFragment) fragmentmanager.findFragmentByTag(ContattiFragment.TAG);
				
				fragmentmanager
					.beginTransaction()
					.replace(R.id.container, contattifragment, ContattiFragment.TAG)
					.commit();
			}
				break;
			default:
				break;
		}
		
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if(Tools.isNetworkEnabled(this)) {
				Intent filtriIntent = new Intent(HomeActivity.this, FiltriActivity.class);
				startActivity(filtriIntent);
				return true;
			} else {
				Toast.makeText(this, "Nessuna connessione ad internet", Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//unbindService(myConnection);
	}
	
	@Override
	public void onBackPressed() {
		if(mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
		} else {
			FragmentManager fragmentManager = getFragmentManager();
			Fragment f = fragmentManager.findFragmentByTag(HomeFragment.TAG);
			
			if(f != null) { //se sono nel fragment mappa chiedo di chiudere l'app
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			    
		        alertDialog.setMessage("Vuoi chiudere Nearby?");
		        
		        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		                finish();
		            }
		        });

		        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            dialog.cancel();
		            }
		        });

		        alertDialog.show();
			} else {
				mNavigationDrawerFragment.selectItem(0); // Se sono nelle offerte torno alla mappa
			}
		}
		//super.onBackPressed();
	}
}
