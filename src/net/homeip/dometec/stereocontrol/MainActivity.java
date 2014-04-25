package net.homeip.dometec.stereocontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tvMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		tvMessage = (TextView) findViewById(R.id.textMessage);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {

		Button btn = (Button) v;

		String command = "";

		if (R.id.buttonFm == btn.getId())
			command = "s32113";

		if (R.id.buttonOff == btn.getId())
			command = "s53905";

		if (R.id.buttonPc == btn.getId())
			command = "s32401";

		if (R.id.buttonVu == btn.getId())
			command = "s51153";

		if (R.id.buttonVd == btn.getId())
			command = "s53201";

		if (R.id.buttonCu == btn.getId())
			command = "s3150";

		if (R.id.buttonCd == btn.getId())
			command = "s32198";

		if (R.id.buttonBass == btn.getId())
			command = "s322793";

		if (R.id.buttonMute == btn.getId())
			command = "s3641";

		command += '\n';

		new SendPacket(command, "192.168.0.177", 20118).execute();

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

}
