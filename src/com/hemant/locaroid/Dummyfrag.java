package com.hemant.locaroid;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.*;
import com.facebook.widget.FacebookDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Read this: https://developers.facebook.com/docs/android/share-dialog/
 */

public class Dummyfrag extends Fragment {
	private static final String TAG_LOG = Menu.class.getSimpleName();
	private UiLifecycleHelper uiHelper; // FACEBOOK SETUP

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d(TAG_LOG, String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d(TAG_LOG, "Success!");
		}
	};

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG_LOG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG_LOG, "Logged out...");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// EXAMPLE 1 rootView/shareButton
		// View rootView = inflater.inflate(R.layout.fragment_EXAMPLE,
		// container, false);
		// Button shareButton = (Button)
		// rootView.findViewById(android.R.id.share);

		// EXAMPLE 2 rootView/shareButton
		Button rootView = new Button(getActivity());
		Button shareButton = rootView;
		shareButton.setText("Share button");

		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FacebookDialog.canPresentShareDialog(getActivity(),
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							getActivity())
							.setName("Titolo")
							.setLink(
									"http://developer.neosperience.com/android")
							.setDescription("Hello from Neosperience Developer")
							.setPicture(
									"http://lh3.googleusercontent.com/-P4JBVTv_kSI/AAAAAAAAAAI/AAAAAAAAAAs/bZptjIhkWu4/s265-c-k-no/photo.jpg")
							.build();
					uiHelper.trackPendingDialogCall(shareDialog.present());

				}

				// if
				// (FacebookDialog.canPresentOpenGraphActionDialog(getApplicationContext(),
				// FacebookDialog.OpenGraphActionDialogFeature.OG_ACTION_DIALOG))
				// {
				// OpenGraphAction action =
				// GraphObject.Factory.create(OpenGraphAction.class);
				// action.setProperty("book",
				// "https://example.com/book/Snow-Crash.html");
				//
				// FacebookDialog shareDialog = new
				// FacebookDialog.OpenGraphActionDialogBuilder(this, action,
				// "books.reads", "book")
				// .build();
				// uiHelper.trackPendingDialogCall(shareDialog.present());
				// }

				else {
					Log.d(TAG_LOG, "Success!");
				}
			}
		});
		return rootView;
	}

	/*
	 * FACEBOOK SETUP
	 * https://developers.facebook.com/docs/android/share-dialog/#setup
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
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