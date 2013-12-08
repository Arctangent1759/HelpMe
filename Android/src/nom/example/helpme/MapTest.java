package nom.example.helpme;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		//		LatLng paypal = new LatLng(37.37783, -121.921507);
		LatLng test = new LatLng(37.385, -121.921507);

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 13));

		map.addMarker(new MarkerOptions().title("Paypal").snippet("The most blah blah.").position(
				test).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();

		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				//process marker here
				return false;
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
}
