package nom.example.helpme;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SignupDialogFragment extends DialogFragment {

	static final String SENDER_ID = "371724014375";
	static final String TAG = "GCMDemo";
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();

	String regid;

	SharedPreferences settings;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		settings = getActivity().getSharedPreferences("nom.example.helpme", 0);

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(getActivity());
//			regid = settings.getString(PROPERTY_REG_ID + getAppVersion(getActivity()), "");
//
//			if (regid.isEmpty()) {
//				registerInBackground();
//			}
			
			registerInBackground();
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View view = inflater.inflate(R.layout.dialog_signup, null);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		// Add action buttons
		.setPositiveButton("Sign up", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Write username to sharedpreferences
				String username = ((EditText) view.findViewById(R.id.username)).getText().toString();
				settings.edit().putString("username", username).commit();
				
				// Post username, email, password
				String email = ((EditText) view.findViewById(R.id.email)).getText().toString();
				String password = ((EditText) view.findViewById(R.id.password)).getText().toString();
				
				String regid = settings.getString(PROPERTY_REG_ID + getAppVersion(getActivity()), "");
				Log.d(TAG, email + " " + username + " " + password + " " + regid);
				
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				
				String baseUrl = "http://23.226.224.191";
				
				String url = baseUrl + "/createAccount?email={email}&username={username}&password={password}&reg_id={regid}";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				String result = restTemplate.getForObject(url, String.class, email, username, password, regid);
				Log.d(TAG, result);
				
				url = baseUrl + "/login?credential={username}&password={password}";
				String sessionKey = restTemplate.getForObject(url, String.class, username, password);
				Log.d(TAG, sessionKey);
				settings.edit().putString("sessionKey", sessionKey).commit();
			}
		}).setNegativeButton("Login", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				String username = ((EditText) view.findViewById(R.id.username)).getText().toString();
				String password = ((EditText) view.findViewById(R.id.password)).getText().toString();
				
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				
				String baseUrl = "http://23.226.224.191";
				String url = baseUrl + "/login?credential={username}&password={password}";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				String sessionKey = restTemplate.getForObject(url, String.class, username, password);
				Log.d(TAG, sessionKey);
				settings.edit().putString("sessionKey", sessionKey).commit();
				settings.edit().putString("username", username).commit();
			}
		});
		return builder.create();
	}

	private void sendInfoToBackend() {

	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getActivity());
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// Post regid
					sendInfoToBackend();

					// Persist the regID - no need to register again.
					storeRegistrationId(regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i(TAG, msg);
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(String regId) {
		settings.edit().putString(PROPERTY_REG_ID + getAppVersion(getActivity()), regId).commit();
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a
	 * dialog that allows users to download the APK from the Google Play Store or enable it in the
	 * device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				getActivity().finish();
			}
			return false;
		}
		return true;
	}
}