package de.gajd.idod.utils;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import de.gajd.idod.interfaces.ConfirmationDialogCallback;

public class Dialog {
	
	public static void jaNein(Context context, String titel, String nachricht, ConfirmationDialogCallback callback) {
		new AlertDialog.Builder(context).setTitle(titel).setMessage(nachricht)
				.setPositiveButton("Ja", (dialog, which) -> {
					callback.onDecision(true);
					dialog.dismiss();
				}).setNegativeButton("Nein", (dialog, which) -> {
					callback.onDecision(false);
					dialog.dismiss();
				}).show();
	}

}