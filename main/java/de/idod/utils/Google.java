package de.gajd.idod.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import de.gajd.idod.R;

public class Google {

	private final String TAG = "GOOGLE";
	private final SignInClient oneTapClient;
	private final Activity activity;
	private final Context context;

	public Google(Activity activity) {
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.oneTapClient = Identity.getSignInClient(activity);
	}

	public void startLogin(ActivityResultLauncher<IntentSenderRequest> launcher) {
		BeginSignInRequest signInRequest = BeginSignInRequest.builder()
				.setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
						.setSupported(true)
						.setServerClientId(activity.getString(R.string.default_web_client_id))
						.setFilterByAuthorizedAccounts(false)
						.build())
				.setAutoSelectEnabled(false)
				.build();

		oneTapClient.beginSignIn(signInRequest)
				.addOnSuccessListener(activity, result -> {
					try {
						IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
						launcher.launch(intentSenderRequest);
					} catch (Exception e) {
						Log.e(TAG, "IntentSenderRequest Error: " + e.getMessage());
					}
				})
				.addOnFailureListener(activity, e -> Log.e(TAG, "Google One Tap Fehler: " + e.getMessage()));
	}

	public String holeToken(Intent data) {
		String idToken = null;
		try {
			SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
			idToken = credential.getGoogleIdToken();
			if (idToken != null) {
				Log.d(TAG, "Got ID Token: " + idToken);
				// Weiter mit Firebase Auth oder eigenen Methoden
			} else {
				Log.w(TAG, "Kein ID-Token vorhanden.");
			}
		} catch (ApiException e) {
			Log.e(TAG, "Fehler beim Parsen der Anmeldedaten: " + e.getLocalizedMessage());
			
		}
		return idToken;
	}
}