package com.hemant.locaroid;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.ProfilePictureView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Share extends Activity {

	private UiLifecycleHelper uiHelper;
	Button fbshare;
	com.facebook.widget.ProfilePictureView fbpropic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharefb);
		fbpropic = (ProfilePictureView) findViewById(R.id.fbpropic);
		fbshare = (Button) findViewById(R.id.buttonfbs);
		fbpropic.setDrawingCacheEnabled(true);
		// fbpropic.setProfileId();
		fbshare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
						Share.this).setLink(
						"https://www.facebook.com/fireballsingh").build();
				uiHelper.trackPendingDialogCall(shareDialog.present());
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						Log.i("Activity", "Success!");
						Toast.makeText(Share.this, "Shared", Toast.LENGTH_LONG)
								.show();
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
}
