package com.hemant.locaroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SigninActivity extends AsyncTask<String, Void, String> {

	private ProgressDialog pDialog;
	private Context ctx;
	private static final String TAG_SUCCESS = "success";
	public String username, mobile, service;

	public SigninActivity(Context context) {

		this.ctx = context;

	}

	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(ctx);
		pDialog.setMessage("Logging in...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {

			String username = (String) arg0[0];
			String password = (String) arg0[1];
			String link = "http://localhost:82/locaroid/get.php?username="
					+ username + "&password=" + password;
			// String link =
			// "http://locationservices.site40.net/locaroid/get.php?username="
			// +username+"&password="+password;
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
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}

	}

	@Override
	protected void onPostExecute(String result) {
		// Toast.makeText(ctx, TAG_SUCCESS, Toast.LENGTH_LONG).show();
		pDialog.dismiss();

		try {
			JSONObject res = new JSONObject(result);
			int successid = res.getInt("success");
			if (successid == 1) {
				// this.mobile = res.getString("mobile");
				// this.username = res.getString("username");
				Toast.makeText(ctx, TAG_SUCCESS, Toast.LENGTH_LONG).show();

				// new getdistancelist();

			}
		} catch (JSONException e) {

			e.printStackTrace();

		}

	}
}
