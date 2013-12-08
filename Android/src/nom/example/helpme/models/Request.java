package nom.example.helpme.models;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//EAch request is request made by person. Eventually saved in model. 
public class Request {
	public String username, title, desc, _id;
	public boolean active, helpComing, urgent;
	public Loc loc, epicenter;

	private final static int TRUNCATED_DESCRIP_LENGTH = 20;

	public MarkerOptions getMarker() {
		String truncateTitle = null;
		String truncateDescription = null;
		int leftOverLength;
		String overflowedString = "";
		//Truncate the title and Description if too long. 
		if (title.length() >= 15)
			truncateTitle = title.substring(0, 20) + "...";
		else
			truncateTitle = title;

		if (desc.length() >= 20)
			truncateDescription = desc.substring(0, TRUNCATED_DESCRIP_LENGTH) + "..." + " --"
					+ username;
		else {
			leftOverLength = 20 - desc.length();
			while (leftOverLength > 0) {
				overflowedString = overflowedString + " ";
				leftOverLength--;
			}
			truncateDescription = desc + overflowedString + "    --" + username;
		}

		MarkerOptions marker = new MarkerOptions();
		marker.title(truncateTitle);
		marker.snippet(truncateDescription); //TODO truncate?
		marker.position(new LatLng(loc.y, loc.x));

		//Sets color.
		if (username == null) {//TODO fix
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		} else if (urgent == true) {
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		} else {
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		}
		return marker;
	}

	public class Loc {
		public double x, y;
	}
}
