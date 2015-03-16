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
import android.util.Log;
import android.widget.Toast;

public class locationupdateactivity extends
		AsyncTask<String, String, JSONObject> {

	private ProgressDialog pDialog;
	public JSONObject jObj;
	String ukey;
	Context context;

	public locationupdateactivity(Context ctx) {
		this.context = ctx;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Updating location...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected JSONObject doInBackground(String... arg0) {

		try {
			String latitude = arg0[0];
			String longitude = arg0[1];
			String service = arg0[2];
			String usname = arg0[3];
			// String link =
			// "http://10.2.2.0:82/locaroid/geoupdate.php?longitude="
			// +longitude+"&latitude="+latitude+"&service="+service+"&username="+usname;
			String link = "http://locationservices.site40.net/locaroid/geoupdate.php?longitude="
					+longitude
					+"&latitude="
					+latitude
					+"&service="
					+service.replaceAll(" ", "_")+"&username="+usname;
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

			jObj = new JSONObject(sb.toString());
			Log.e("JSON string", sb.toString());
		} catch (Exception e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		return jObj;

	}

	@Override
	protected void onPostExecute(JSONObject json) {

		pDialog.dismiss();
		try {

			int successid = json.getInt("success");
			if (successid == 1) {
				Toast.makeText(context, "successfully updated",
						Toast.LENGTH_LONG).show();
				Log.e("jsonresponse", "successid = 1");
			} else {
				Toast.makeText(context, "Not updated", Toast.LENGTH_SHORT)
						.show();
				Log.e("jsonresponse", "successid != 1");
			}
		} catch (JSONException e) {

			Log.e("jsonparse", "exceptioon");
			e.printStackTrace();
		}

	}
}
