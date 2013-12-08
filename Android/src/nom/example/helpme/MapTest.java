package nom.example.helpme;

import nom.example.helpme.models.Request;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.R.string;
import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTest extends Activity {
	public static final int TRUNCATED_DESCRIP_LENGTH = 20;

	String username;
	SharedPreferences settings;
	GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		// LatLng paypal = new LatLng(37.37783, -121.921507);
		LatLng test = new LatLng(37.385, -121.921507);

		map.setMyLocationEnabled(true);
		//TODO center camera on location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 13));

//		Request newRequest = new Request(37.37783, -121.921507, "helphelp", false, true,
//				"TestTitletestTESTTESTTESThehehehehe",
//				"TestDespheheheheheheheheheheheheheheheheheheheheheheh");

//		map.addMarker(newRequest.getMarker()).showInfoWindow();

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			//Compares the current username against the username attached to snippet. Returns true when they match, indicating
			//that the marker is mine. 
			public boolean onMarkerClick(Marker marker) {
				if (username.equals(marker.getSnippet().substring(TRUNCATED_DESCRIP_LENGTH + 6))) {
					Log.d("MapTest", "mine");
				} else {
					Log.d("MapTest", "other");
				}
				return false;
			}
		});

		final Button helpButton = (Button) findViewById(R.id.needHelpButton);
		helpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Location loc = map.getMyLocation();
				settings.edit().putString("lng", "" + loc.getLongitude()).commit();
				settings.edit().putString("lat", "" + loc.getLatitude()).commit();

				new HelpRequestDialogFragment().show(getFragmentManager(),
						"HelpRequestDialogFragment");
			}
		});

		settings = getSharedPreferences("nom.example.helpme", 0);
		username = settings.getString("username", null);
		if (username == null) {
			new SignupDialogFragment().show(getFragmentManager(), "SignupDialogFragment");
		}
		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				updateMap();
			}
		});
	}

	public static void get() {

	}
	public void updateMap() {
		Log.d("MapTest", "Updating map...");

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		Location l = map.getMyLocation();
		String loc = "{\"x\":" + l.getLongitude() + ",\"y\":" + l.getLatitude() + "}";

		String baseUrl = "http://23.226.224.191";
		String url = baseUrl + "/getRequests?sessionKey={sessionKey}&loc={loc}";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

		String sessionKey = settings.getString("sessionKey", "");

		String requestss = restTemplate.getForObject(url, String.class, sessionKey, loc);
		Log.d("Requests", requestss);

		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
		Request[] requests = restTemplate.getForObject(url, Request[].class, sessionKey, loc);

		//TODO parse requests into a List<Request>
		for (Request request : requests) {
			map.clear();
			Log.d("stuff", request._id + " " + request.loc.x);
			map.addMarker(request.getMarker()).showInfoWindow();
		}
	}
}
