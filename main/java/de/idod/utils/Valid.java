package de.gajd.idod.utils;

import android.text.TextUtils;

public class Valid {
	
	public static ValidResult password(String password) {
		String msg = "Passwort ";
		boolean valid = true;

		if (TextUtils.isEmpty(password)) {
			msg += "ist erforderlich\n";
			valid = false;
		}
		if (password.length() < 8) {
			msg += "muss mindestens 8 Zeichen lang sein\n";
			valid = false;
		}
		if (!password.matches(".*[A-Z].*[A-Z].*")) {
			msg += "muss 2 Groß-Buchstaben enthalten\n";
			valid = false;
		}
		if (!password.matches(".*[a-z].*[a-z].*")) {
			msg += "muss 2 Klein-Buchstaben enthalten\n";
			valid = false;
		}
		if (!password.matches(".*\\d.*\\d.*")) {
			msg += "muss 2 Zahlen enthalten\n";
			valid = false;
		}
		return new ValidResult(valid, msg);
	}
	
	public static ValidResult confiPW(String password, String confirmPW){
		String msg = "";
		boolean valid = true;
		
		if (TextUtils.isEmpty(confirmPW)) {
			msg += "Passwortbestätigung ist erforderlich";
			valid = false;
		} else if (!password.equals(confirmPW)) {
			msg += "Passwörter sind nicht gleich!";
			valid = false;
		}	
		return new ValidResult(valid, msg);
	}
	
	public static ValidResult email(String email){
		String msg = "";
		boolean valid = true;
		
		if (TextUtils.isEmpty(email)) {
			msg += "E-Mail ist erforderlich";
			valid = false;
		} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			msg += "Ungültiges E-Mail-Format";
			valid = false;
		}		
		return new ValidResult(valid, msg);
	}
}