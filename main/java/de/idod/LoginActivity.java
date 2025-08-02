package de.gajd.idod;

import de.gajd.idod.databinding.ActivityLoginBinding;
import de.gajd.idod.utils.AppSignatur;
import de.gajd.idod.utils.Google;
import de.gajd.idod.utils.InfoUI;
import de.gajd.idod.utils.Valid;
import de.gajd.idod.utils.ValidResult;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;


public class LoginActivity extends AppCompatActivity {

	private static final String TAG = "ANMELDUNG LOGIN -> ";
	
	private ActivityLoginBinding binding;
	private FirebaseAuth auth;
	private FirebaseUser user;
	private Google google;
	private ActivityResultLauncher<IntentSenderRequest> launcher;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		auth = FirebaseAuth.getInstance();
		user = auth.getCurrentUser();
		if (user != null && user.isEmailVerified()) {
			InfoUI.toast(this, "Automatisch angemeldet!");
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		
		google = new Google(this);
		launcher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
			if (result.getResultCode() == RESULT_OK) {
				String idToken = google.holeToken(result.getData());
				AuthCredential googleCredential = GoogleAuthProvider.getCredential(idToken, null);
				firebaseLogin(googleCredential);
			}
		});
	}
	
	protected void onStart(){
		super.onStart();
		binding.btnLogin.setOnClickListener(v -> validateFields());
		binding.btnGoogle.setOnClickListener(v -> google.startLogin(launcher));
	}
	
	private void validateFields() {
		String email = binding.etEmail.getText().toString().trim();
		String passwort = binding.etPasswort.getText().toString().trim();
		boolean valid = true;
		ValidResult mail = Valid.email(email);
		if (!mail.isValid) {
			binding.etEmail.setError(mail.message);
			valid = false;
		}
		ValidResult pw = Valid.password(passwort);
			if (!pw.isValid) {
			binding.etPasswort.setError(pw.message);
			valid = false;
		}
		if (!valid) return;
		AuthCredential emailCredential = EmailAuthProvider.getCredential(email, passwort);
		firebaseLogin(emailCredential);
	}

	private void firebaseLogin(AuthCredential credential) {
		auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				user = auth.getCurrentUser();
				if (user != null) {
					userIsVerified();
				}
			} else {
				if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthException){
					InfoUI.toast(this, "Kein verbundenes Konto grfunden!");
				}
				Log.e(TAG, credential.getProvider(), task.getException());
				InfoUI.toast(this, "Anmeldung fehlgeschlagen!");
			}
		});
	}

	private void userIsVerified() {
		if (!user.isEmailVerified()) { //erster Login
			InfoUI.toast(this, "Erster Login! Bitte Profil anpassen.");
			startActivity(new Intent(this, EditProfilActivity.class));
			finish();
		} else { //normaler Login
			InfoUI.toast(this, "Anmeldung erfolgreich!");
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
}