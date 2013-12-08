package nom.example.helpme;

import nom.example.helpme.models.Request;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTest extends Activity {
	public static final int TRUNCATED_DESCRIP_LENGTH = 20;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		// LatLng paypal = new LatLng(37.37783, -121.921507);
		LatLng test = new LatLng(37.385, -121.921507);

		map.setMyLocationEnabled(true);
		//TODO center camera on location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 13));

		Request newRequest = new Request(37.37783, -121.921507, "helphelp", false, true, "TestTitletestTESTTESTTESTheheheheheheheheheheheheheheheheheheheheheheheheheheheh", "TestDesp");
		
		map.addMarker(newRequest.getMarker()).showInfoWindow();
		
		
		
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			//Compares the current username against the username attached to snippet. Returns true when they match, indicating
			//that the marker is mine. 
			public boolean onMarkerClick(Marker marker) {
				if (username.equals(marker.getSnippet().substring(TRUNCATED_DESCRIP_LENGTH + 6)))
				{
					System.out.print("itworked!!");
					return true;
				}
				else
				{
					System.out.print("itdidn'twork!!");
					return false;
				}
			}
		});

		final Button helpButton = (Button) findViewById(R.id.needHelpButton);
		helpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new HelpRequestDialogFragment().show(getFragmentManager(),
						"HelpRequestDialogFragment");
			}
		});
	}
	
	String username = "";
	
	//TODO check sharedpref for username, prompt for login if missing
	
	
	
}
