package de.gajd.idod;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

// Imports für Google One Tap (Identity API)
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialProvider;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

// Firebase Imports
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.gajd.idod.utils.Google;
import de.gajd.idod.utils.Valid;
import de.gajd.idod.utils.ValidResult;
import java.util.HashMap;
import java.util.Map;

import de.gajd.idod.databinding.ActivityEditProfilBinding;
import de.gajd.idod.R;

public class EditProfilActivity extends AppCompatActivity {

	private static final String TAG = "PROFIL BEARBEITEN -> ";

	private ActivityEditProfilBinding binding;
	private FirebaseAuth mAuth;
	private DatabaseReference mDatabase;
	private FirebaseUser currentUser;
	private Google google;
	private String provider;
	private ActivityResultLauncher<IntentSenderRequest> launcher;

	private boolean isGoogleLinked = false;
	private boolean isEmailLinked = false;
	private boolean valid = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityEditProfilBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		google = new Google(this);
		launcher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
			if (result.getResultCode() == RESULT_OK) {
				String idToken = google.holeToken(result.getData());
				AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
				firebaseLinkWithCredential(credential);
			}
		});

		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference("users");
		binding.btnSaveProfile.setOnClickListener(v -> {
			saveUserName();
			saveUserDataToDatabase();
		});
	}

	protected void onStart() {
		super.onStart();
		checkUser();
		checkProvider();
		isNewUser();
		buttonSettings();
		loadUserProfile();
	}

	private void checkUser() {
		currentUser = mAuth.getCurrentUser();
		if (currentUser == null || currentUser.getProviderData() == null) {
			Toast.makeText(this, "Kein angemeldeten Benutzer gefunden.", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void checkProvider() {
		for (UserInfo profile : currentUser.getProviderData()) {
			if (profile.getProviderId().equals(EmailAuthProvider.PROVIDER_ID)) {
				isEmailLinked = true;
				provider = "E-Mail";
			}
			if (profile.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
				isGoogleLinked = true;
				provider = "Google";
			}
		}
	}

	private void isNewUser() {
		if (!currentUser.isEmailVerified()) {
			binding.tvAccountSettingsHeader
					.setText("Kontoeinstellungen (Bitte E-Mail/Passwort setzen oder Google-Konto verknüpfen)");
			setProfileFieldsVisible(View.GONE);
			setLoginFieldsVisible(View.VISIBLE);
			Toast.makeText(this, "Bitte E-Mail und Passwort/Google-Verknüpfung festlegen!", Toast.LENGTH_LONG).show();
		} else {
			binding.tvAccountSettingsHeader.setText("Anmeldemethode anpassen: Angemeldet mit " + provider);
			setProfileFieldsVisible(View.VISIBLE);
			setLoginFieldsVisible(View.GONE);
			loadUserProfile();
		}
	}

	private void buttonSettings() {
		if (isEmailLinked) {
			//Vorm Click
			binding.btnPasswordSignIn.setText("Passwort\nändern");
			binding.btnGoogleSignIn.setText("Wechsel zu Google");
			binding.btnPasswordSignIn.setOnClickListener(v -> {
				//Click 1
				if (binding.tilEmail.getVisibility() == View.GONE) {
					setLoginFieldsVisible(View.VISIBLE);
					binding.btnPasswordSignIn.setText("Passwort\nspeichern");
					binding.btnGoogleSignIn.setText("Abbrechen");
				} else {
					valid = true;
					saveUserMail();
					saveUserPW();
					if (valid) {
						setLoginFieldsVisible(View.GONE);
						binding.btnPasswordSignIn.setText("Passwort\nändern");
						binding.btnGoogleSignIn.setText("Wechsel zu Google");
					}
				}
			});

			binding.btnGoogleSignIn.setOnClickListener(v -> {
				if (binding.tilEmail.getVisibility() == View.GONE) {
					google.startLogin(launcher);
				} else {
					setLoginFieldsVisible(View.GONE);
					binding.btnGoogleSignIn.setText("Wechsel zu Google");
					binding.btnPasswordSignIn.setText("Passwort\nändern");
					binding.etEmail.setText(currentUser.getEmail());
					binding.etPassword.setText("");
					binding.etConfirmPassword.setText("");
				}
			});

		}

		if (isGoogleLinked) {
			binding.btnPasswordSignIn.setText("Wechseln zu\nPasswort");
			binding.btnPasswordSignIn.setOnClickListener(v -> {
				if (binding.tilEmail.getVisibility() == View.GONE) {
					setLoginFieldsVisible(View.VISIBLE);
					binding.btnPasswordSignIn.setText("Neue Daten\nspeichern");
					binding.btnGoogleSignIn.setText("Abbrechen");
				} else {
					String[] data = getInputLogin();
					ValidResult mail = Valid.email(data[0]);
					ValidResult pw = Valid.password(data[1]);
					if (mail.isValid && pw.isValid) {
						AuthCredential credential = EmailAuthProvider.getCredential(data[0], data[1]);
						firebaseLinkWithCredential(credential);
						//Soll nur bei Erfolg passiwren!!!
						setLoginFieldsVisible(View.GONE);
						binding.btnPasswordSignIn.setText("Wechseln zu\nPasswort");
					}
				}
			});
			binding.btnGoogleSignIn.setText("Angemeldet mit Google");
			binding.btnGoogleSignIn.setOnClickListener(v -> {
				if (binding.tilEmail.getVisibility() == View.GONE) {
					Toast.makeText(this, "Dieses Konto ist bereits mit Google verknüpft.", Toast.LENGTH_SHORT).show();
				} else {
					binding.btnGoogleSignIn.setText("Abbrechen");
					setLoginFieldsVisible(View.GONE);
					binding.btnGoogleSignIn.setText("Angemeldet mit Google");
					binding.btnPasswordSignIn.setText("Wechseln zu\nPasswort");
				}
			});

		}

	}

	private void setProfileFieldsVisible(int enabled) {
		binding.tilName.setVisibility(enabled);
		binding.tilTelefonnummer.setVisibility(enabled);
		binding.tilAdresse.setVisibility(enabled);
		binding.tilFunktion.setVisibility(enabled);
		binding.tilStatus.setVisibility(enabled);
		binding.tilProfi.setVisibility(enabled);
		binding.tilKFZ.setVisibility(enabled);
		binding.btnSaveProfile.setVisibility(enabled);
	}

	private void setLoginFieldsVisible(int enabled) {
		binding.tilEmail.setVisibility(enabled);
		binding.tilPassword.setVisibility(enabled);
		binding.tilConfirmPassword.setVisibility(enabled);
	}

	private void loadUserFirebase() {
		binding.etName.setText(currentUser.getDisplayName());
		binding.etEmail.setText(currentUser.getEmail());
	}

	private String[] getInputLogin() {
		String email = binding.etEmail.getText().toString().trim();
		String password = binding.etPassword.getText().toString().trim();
		String[] data = { email, password };
		return data;
	}

	private void loadUserProfile() {
		loadUserFirebase();
		mDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					binding.etAdresse.setText(snapshot.child("adresse").getValue(String.class));
					binding.etTelefonnummer.setText(snapshot.child("nummer").getValue(String.class));
					binding.etFunktion.setText(snapshot.child("funktion").getValue(String.class));
					binding.etStatus.setText(snapshot.child("status").getValue(String.class));
					binding.etProfi.setText(snapshot.child("profi").getValue(String.class));
					binding.etKFZ.setText(snapshot.child("kfz").getValue(String.class));
					/*
					String profiValue = snapshot.child("profi").getValue(String.class);
					if (profiValue != null) {
						ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) binding.spnProfi.getAdapter();
						if (adapter != null) {
							int spinnerPosition = adapter.getPosition(profiValue);
							binding.spnProfi.setSelection(spinnerPosition);
						}
					}
					*/
				} else {
					Log.d(TAG, "Keine zusätzlichen Benutzerdaten in Realtime DB für UID: " + currentUser.getUid());
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.w(TAG, "loadUserProfile:onCancelled", error.toException());
				Toast.makeText(EditProfilActivity.this, "Fehler beim Laden der Profildaten.", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void saveUserName() {
		binding.etName.setError(null);
		String name = binding.etName.getText().toString().trim();
		if (!name.equals(currentUser.getDisplayName())) {
			if (TextUtils.isEmpty(name)) {
				binding.etName.setError("Name ist erforderlich!");
				return;
			}
			UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name)
					.build();
			currentUser.updateProfile(profileUpdates).addOnSuccessListener(authResult -> {
				Log.d(TAG, "Name von " + currentUser.getUid() + " zu " + name + "geändert!");
				Toast.makeText(EditProfilActivity.this,
						"Name erfolgreich geändert für Benutzer mit UID: " + currentUser.getUid(), Toast.LENGTH_LONG)
						.show();
			}).addOnFailureListener(e -> {
				Log.e(TAG, "Fehler bei Namensänderung", e.getCause());
				Toast.makeText(EditProfilActivity.this, "Fehler beim Ändern des Namen: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			});
			currentUser.reload();
		}
	}

	private void saveUserMail() {
		valid = true;
		binding.etEmail.setError(null);
		String email = binding.etEmail.getText().toString().trim();
		if (!currentUser.isEmailVerified() || !email.equals(currentUser.getEmail())) {
			ValidResult mail = Valid.email(email);
			if (!mail.isValid) {
				binding.etEmail.setError(mail.message);
				valid = false;
			}
			if (!valid)
				return;
			currentUser.verifyBeforeUpdateEmail(email).addOnSuccessListener(this, task -> {
				Log.d(TAG, "Verifizierungs-E-Mail gesendet. E-Mail wird nach Bestätigung geändert.");
				Toast.makeText(EditProfilActivity.this,
						"Bestätigungs-E-Mail an neue Adresse gesendet. Bitte E-Mail verifizieren.", Toast.LENGTH_LONG)
						.show();
			}).addOnFailureListener(this, task -> {
				Log.e(TAG, "Fehler beim Senden der Verifizierungs-E-Mail.", task.getCause());
				Toast.makeText(EditProfilActivity.this, "Fehler beim E-Mail-Update: " + task.getMessage(),
						Toast.LENGTH_LONG).show();
			});

		}
	}

	private void saveUserPW() {
		valid = true;
		binding.etPassword.setError(null);
		binding.etConfirmPassword.setError(null);
		String passwort = binding.etPassword.getText().toString().trim();
		String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
		if (!currentUser.isEmailVerified() || !TextUtils.isEmpty(passwort) || !TextUtils.isEmpty(confirmPassword)) {
			ValidResult pw = Valid.password(passwort);
			if (!pw.isValid) {
				binding.etPassword.setError(pw.message);
				valid = false;
			}
			ValidResult confiPW = Valid.confiPW(passwort, confirmPassword);
			if (!confiPW.isValid) {
				binding.etConfirmPassword.setError(confiPW.message);
				valid = false;
			}
			if (!valid)
				return;
			currentUser.updatePassword(passwort).addOnCompleteListener(new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
					if (task.isSuccessful()) {
						Log.d(TAG, "Passwort geändert!");
						Toast.makeText(EditProfilActivity.this, "Passwort aktualisiert.", Toast.LENGTH_SHORT).show();
					} else {
						Log.e(TAG, "Passwort Änderung fehlgeschlagen", task.getException());
						Toast.makeText(EditProfilActivity.this,
								"Fehler beim Aktualisieren des Passworts: " + task.getException().getMessage(),
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	private void saveUserDataToDatabase() {
		checkUser();
		String adresse = binding.etAdresse.getText().toString().trim();
		String number = binding.etTelefonnummer.getText().toString().trim();
		String funktion = binding.etFunktion.getText().toString().trim();
		String status = binding.etStatus.getText().toString().trim();
		String profi = binding.etProfi.getText().toString().trim();
		String kfz = binding.etKFZ.getText().toString().trim();

		Map<String, Object> userMap = new HashMap<>();
		userMap.put("adresse", adresse);
		userMap.put("nummer", number);
		userMap.put("funktion", funktion);
		userMap.put("status", status);
		userMap.put("profi", profi);
		userMap.put("kfz", kfz);

		mDatabase.child(currentUser.getUid()).updateChildren(userMap).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Log.d(TAG, "Userdaten aktualisiert");
				Toast.makeText(EditProfilActivity.this, "Profildaten erfolgreich gespeichert!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Log.e(TAG, "Fehler beim Speichern der Profildaten in DB.", task.getException());
				Toast.makeText(EditProfilActivity.this, "Fehler beim Speichern der Profildaten.", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void firebaseLinkWithCredential(AuthCredential credential) {
		currentUser.linkWithCredential(credential).addOnCompleteListener(this, task -> {
	//	String provider = "";
			if (task.isSuccessful()) {
				if (isGoogleLinked) {
					currentUser.unlink(GoogleAuthProvider.PROVIDER_ID);
					provider = "E-Mail";
					isEmailLinked = true;
					buttonSettings();
				}
				if (isEmailLinked) {
					currentUser.unlink(EmailAuthProvider.PROVIDER_ID);
					provider = "Google";
					isGoogleLinked = true;
					buttonSettings();
				}
				Log.d(TAG, "Firebase Auth mit " + provider + " erfolgreich.");
				currentUser = mAuth.getCurrentUser();
				Toast.makeText(EditProfilActivity.this, "Erfolgreich mit " + provider + " verknüpft.",
						Toast.LENGTH_SHORT).show();
				//authProviderUser();
				if (!currentUser.isEmailVerified()) {
					Toast.makeText(EditProfilActivity.this, "Bitte bestätige deine E-Mail Adresse!", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Log.e(TAG, provider + "-Verlinkung fehlgeschlagen! ", task.getException());
				if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
					Toast.makeText(EditProfilActivity.this,
							"Dieses Google-Konto ist bereits mit einem anderen Nutzer verknüpft.", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(EditProfilActivity.this,
							provider + "-Verlinkung fehlgeschlagen: " + task.getException().getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/*	private void firebaseLinkWithGoogle(String idToken) {
			AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
			currentUser.linkWithCredential(credential).addOnCompleteListener(this, task -> {
				if (task.isSuccessful()) {
					// Anmeldung erfolgreich, UI aktualisieren
					Log.d(TAG, "Firebase Auth mit Google erfolgreich.");
					currentUser.unlink(EmailAuthProvider.PROVIDER_ID);
					currentUser = mAuth.getCurrentUser(); // currentUser aktualisieren
					Toast.makeText(EditProfilActivity.this, "Erfolgreich mit Google verknüpft.", Toast.LENGTH_SHORT).show();
					isNewUser();
					if (currentUser.isEmailVerified()) {
						setProfileFieldsVisible(View.VISIBLE);
					} else {
						Toast.makeText(EditProfilActivity.this, "Bitte bestätige deine E-Mail Adresse!", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Log.e(TAG, "Goggle-Verlinkung fehlgeschlagen", task.getException());
					if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
						Toast.makeText(EditProfilActivity.this,
								"Dieses Google-Konto ist bereits mit einem anderen Nutzer verknüpft.", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(EditProfilActivity.this,
								"Fehler beim Verknüpfen mit Google: " + task.getException().getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	
		private void firebaseLinkWithEmail(String email, String password) {
			AuthCredential credential = EmailAuthProvider.getCredential(email, password);
			currentUser.linkWithCredential(credential).addOnCompleteListener(this, task -> {
				if (task.isSuccessful()) {
					Log.d(TAG, "Firebase Auth mit Email erfolgreich.");
					currentUser.unlink(GoogleAuthProvider.PROVIDER_ID);
					currentUser = mAuth.getCurrentUser(); // currentUser aktualisieren
					Toast.makeText(EditProfilActivity.this, "Erfolgreich mit Email verknüpft.", Toast.LENGTH_SHORT).show();
					isNewUser();
				} else {
					Log.e(TAG, "Firebase Auth mit Email fehlgeschlagen.", task.getException());
					Toast.makeText(EditProfilActivity.this, "Authentifizierung mit Email fehlgeschlagen.",
							Toast.LENGTH_SHORT).show();
				}
			});
		}*/
}