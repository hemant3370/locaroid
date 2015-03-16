package com.hemant.locaroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registerscreenactivity extends Activity {

	private EditText rusernameField, rpasswordField, rmobile_numField;
	private Button rRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg);
		rusernameField = (EditText) findViewById(R.id.editText1);
		rpasswordField = (EditText) findViewById(R.id.editText2);
		rmobile_numField = (EditText) findViewById(R.id.mobi);
		rRegister = (Button) findViewById(R.id.reg);
		rRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new registerfull();
			}
		});
	}

	private class registerfull extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(registerscreenactivity.this);
			pDialog.setMessage("Updating information ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			try {

				String usname = arg0[0];

				String mobile = arg0[0];

				// String link =
				// "http://localhost:82/locaroid/geoupdate.php?longitude="
				// +longitude+"&latitude="+latitude+"&service="+service+"&username="+usname;
				String link = "http://locationservices.site40.net/locaroid/geoupdate.php?";
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(link));
				HttpResponse response = client.execute(request);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				StringBuffer sb = new StringBuffer("");
				String line = "";
				while ((line = in.readLine()) != null) {
					sb.append(line);
					break;
				}
				in.close();
				Log.d("service update response", sb.toString());
				return sb.toString();
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {

			pDialog.dismiss();
			Toast.makeText(registerscreenactivity.this,
					"Location updated" + result, Toast.LENGTH_SHORT).show();

		}
	}
}
