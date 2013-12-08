package nom.example.helpme.models;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Request {

	public double lat;
	public double lng;
	public String username;
	public boolean urgent;
	
	//TODO write a constructor
	

	public MarkerOptions getMarker() {
		MarkerOptions marker = new MarkerOptions();
		marker.title("TITLE");
		marker.snippet("DESCRIPTION"); //TODO truncate?
		marker.position(new LatLng(lat, lng));
		
		//Sets color
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		
		return marker;
	}
}
