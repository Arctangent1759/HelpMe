package nom.example.helpme;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class HelpRequestDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View view = inflater.inflate(R.layout.dialog_help_request, null);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		// Add action buttons
		.setPositiveButton("Request", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String title = ((EditText) view.findViewById(R.id.title)).getText().toString();
				String description = ((EditText) view.findViewById(R.id.description)).getText().toString();
				String urgent = ((CheckBox) view.findViewById(R.id.urgentCheckBox)).isChecked() ? "true"
						: "false";

				SharedPreferences sp = getActivity().getSharedPreferences("nom.example.helpme", 0);
				String sessionKey = sp.getString("sessionKey", "");
				String lat = sp.getString("lat", "");
				String lng = sp.getString("lng", "");
				String loc = "{\"x\":" + lng + ",\"y\":" + lat + "}";

				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);

				String baseUrl = "http://23.226.224.191";
				String url = baseUrl + "/getHelp?sessionKey={sessionKey}&title={title}"
						+ "&desc={description}&urgent={urgent}&loc={loc}&epicenter={loc}";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				
				String result = restTemplate.getForObject(url, String.class, sessionKey, title,
						description, urgent, loc, loc);
				Log.d("HelpRequest", result);

				// TODO update map
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				HelpRequestDialogFragment.this.getDialog().cancel();
				//clears the sharedpref. TODO remove
				getActivity().getSharedPreferences("nom.example.helpme", 0).edit().clear().commit();
			}
		});
		return builder.create();
	}
}