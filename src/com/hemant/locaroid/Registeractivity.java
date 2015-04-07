package com.hemant.locaroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Registeractivity extends AsyncTask<String, String, JSONObject> {

	private ProgressDialog pDialog;
	private Context ctx;
	JSONObject jobj;

	public Registeractivity() {

	}

	/*
	 * protected void onPreExecute(){ super.onPreExecute(); pDialog = new
	 * ProgressDialog(ctx); pDialog.setMessage("Registering in...");
	 * pDialog.setIndeterminate(false); pDialog.setCancelable(true);
	 * pDialog.show(); }
	 */
	@Override
	protected JSONObject doInBackground(String... arg0) {

		try {

			String username = arg0[0];
			// String password = (String)arg0[1];
			// String mobile = (String)arg0[2];
			// String link =
			// "http://10.0.2.2:82/locaroid/registerget.php?username="
			// +username+"&password="+password+"&mobile="+mobile;
			String link = "http://locationservices.site40.net/locaroid/registerwithemailonly.php?username="+username;
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
			jobj = new JSONObject(sb.toString());
			Log.e("Register JSON string", sb.toString());

		} catch (Exception e) {
			Log.e(" Register JSON Parser", "Error parsing data " + e.toString());
		}
		return jobj;

	}

}
