package de.gajd.idod.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppSignatur {
	public static void printAppSignatures(Context context) {
		try {
			String packageName = context.getPackageName();
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
					PackageManager.GET_SIGNING_CERTIFICATES);

			Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();
			for (Signature signature : signatures) {
				// SHA-1 Hash
				String sha1 = getSignatureHash(signature, "SHA-1");
				// SHA-256 Hash
				String sha256 = getSignatureHash(signature, "SHA-256");

				Log.d("SHA-1: ", sha1);
				Log.d("SHA-256", sha256);
				textinAblage(context, "SHA-1: " + sha1 + "\nSHA-256: " + sha256);
				Toast.makeText(context, "App-Signatur in Zwischenablage kopiert!", Toast.LENGTH_LONG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSignatureHash(Signature signature, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(signature.toByteArray());
			byte[] digest = md.digest();
			return bytesToHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString().toUpperCase();
	}

	private static void textinAblage(Context context, String text) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Log", text);
		clipboard.setPrimaryClip(clip);
	}
}