package nom.example.helpme.models;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//EAch request is request made by person. Eventually saved in model. 
public class Request {

	public double lat;
	public double lng;
	public String username;
	public boolean urgent;
	public boolean mine;
	public String title;
	public String description;
	public static final int TRUNCATED_TITLE_LENGTH = 14;
	public static final int TRUNCATED_DESCRIP_LENGTH = 20;
	
	//TODO write a constructor
	public Request(double lat, double lng, String username, boolean urgent, boolean mine, String title, String description)
	{
		this.lat = lat;
		this.lng = lng;
		this.username = username;
		this.urgent = urgent;
		this.mine = mine;
		this.title = title;
		this.description = description;	
	}

	public MarkerOptions getMarker() 
	{
		String truncateTitle = null;
		String truncateDescription = null;
		int leftOverLength;
		String overflowedString = "";
		//Truncate the title and Description if too long. 
		if (title.length() >= 15)
			truncateTitle = title.substring(0, TRUNCATED_TITLE_LENGTH) + "...";
		else
			truncateTitle = title;
		
		
		if (description.length() >= 20)
			truncateDescription = description.substring(0, TRUNCATED_DESCRIP_LENGTH) + "..." + " --" + username;
		else
		{
			leftOverLength = 20-description.length();
			while (leftOverLength > 0)
			{
				overflowedString = overflowedString + " ";
				leftOverLength--;
			}
			truncateDescription = description + overflowedString + "    --" + username;
		}
		
		MarkerOptions marker = new MarkerOptions();
		marker.title(truncateTitle);
		marker.snippet(truncateDescription); //TODO truncate?
		marker.position(new LatLng(lat, lng));
		
		//Sets color.
		if (mine == true)
		{
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		} 
		else if (urgent == true)
		{
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		} 
		else
		{
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		}
		return marker;
	}
}
