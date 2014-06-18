package net.homeip.dometec.stereocontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tvMessage;

	private final static String SENDER_ID = "614770691258";

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private static final String KEY_PROPERTY_REG_ID = "registration_id";
	private static final String KEY_PROPERTY_APP_VERSION = "appVersion";
	private static final String LOG_TAG = "MainActivity";

	private GoogleCloudMessaging gcm = null;
	private String regid = null;
	private Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		tvMessage = (TextView) findViewById(R.id.textMessage);

		context = getApplicationContext();

		if (checkPlayServices()) {

			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			} else {
				Log.d(LOG_TAG, "No valid Google Play Services APK found.");
			}

		} else {
			Log.d(LOG_TAG, "No Google Play Services APK found.");
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {

		Button btn = (Button) v;

		String command = "";

		if (R.id.buttonFm == btn.getId())
			command = "Sony,2113,9";

		if (R.id.buttonOff == btn.getId())
			command = "Sony,3905,9";

		if (R.id.buttonPc == btn.getId())
			command = "Sony,1377,9";

		if (R.id.buttonVu == btn.getId())
			command = "Sony,1153,10";

		if (R.id.buttonVd == btn.getId())
			command = "Sony,3201,10";

		if (R.id.buttonCu == btn.getId())
			command = "Sony,150,3";

		if (R.id.buttonCd == btn.getId())
			command = "Sony,2198,3";

		if (R.id.buttonBass == btn.getId())
			command = "Sony,22793,3";

		if (R.id.buttonMute == btn.getId())
			command = "Sony,641,1";

		command += '\n';

		new SendPacket(command, "192.168.0.14", 20118).execute();

	}

	@SuppressWarnings("rawtypes")
	private class SendPacket extends AsyncTask {

		private String host;
		private int port;
		private int msg_length;
		private byte[] messages;

		public SendPacket(String command, String host, int port) {
			this.host = host;
			this.port = port;
			msg_length = command.length();
			messages = command.getBytes();
		}

		@Override
		protected Object doInBackground(Object... arg0) {

			try {

				InetAddress iHost = InetAddress.getByName(host);
				DatagramPacket packet = new DatagramPacket(messages, msg_length, iHost, port);

				DatagramSocket datagramSocket = new DatagramSocket();
				datagramSocket.setBroadcast(false);
				datagramSocket.send(packet);

				Log.i(this.getClass().getName(), "Sended package: " + new String(packet.getData()));

			} catch (Exception e) {
				tvMessage.setText(e.getMessage());
			}

			return null;
		}

	}

	private boolean checkPlayServices() {

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.d(LOG_TAG, "This device is not supported - Google Play Services.");
				finish();
			}
			return false;
		}

		return true;
	}

	private String getRegistrationId(Context context) {

		final SharedPreferences prefs = getGCMPreferences(context);

		String registrationId = prefs.getString(KEY_PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.d(LOG_TAG, "Registration ID not found.");
			return "";
		}

		int registeredVersion = prefs.getInt(KEY_PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.d(LOG_TAG, "App version changed.");
			return "";
		}

		return registrationId;

	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerInBackground() {

		new AsyncTask() {

			@Override
			protected Object doInBackground(Object... params) {

				String msg = "";

				try {

					if (gcm == null)
						gcm = GoogleCloudMessaging.getInstance(context);

					regid = gcm.register(SENDER_ID);

					Log.d(LOG_TAG, "########################################");
					Log.d(LOG_TAG, "Current Device's Registration ID is: " + regid);

				} catch (IOException ex) {
					ex.printStackTrace();
					msg = "Error :" + ex.getMessage();
					Log.d(LOG_TAG, "Error: " + msg);
				}

				return null;
			}

			protected void onPostExecute(Object result) {
				// to do here
			}

		}.execute(null, null, null);
	}

}
