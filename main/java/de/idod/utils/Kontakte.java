package de.gajd.idod.utils;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import de.gajd.idod.databinding.DialogUserEditBinding;
import de.gajd.idod.interfaces.ConfirmationDialogCallback;
import de.gajd.idod.models.Kontakt;

public class Kontakte {
	
	public static Kontakt editDialog(Context context, Kontakt item, ConfirmationDialogCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		DialogUserEditBinding dialogBinding = DialogUserEditBinding.inflate(getLayoutInflater());
		builder.setView(dialogBinding.getRoot());
		dialogBinding.etStatus.setVisibility(View.GONE);
		dialogBinding.etProfi.setVisibility(View.GONE);
		
		if(item != null) {
			dialogBinding.etName.setText(item.getName());
			dialogBinding.etPhone.setText(item.getNummer());
			dialogBinding.etEmail.setText(item.getEmail());
			dialogBinding.etFunktion.setText(item.getFunktion());
			
			builder.setTitle("Kontakt bearbeiten");
			} else {
			builder.setTitle("Kontakt erstellen");
		}
		builder.setPositiveButton("Speichern", (dialog, which) -> {
		
			item.setName(dialogBinding.etName.getText().toString().trim());
			item.setNummer(dialogBinding.etPhone.getText().toString().trim());
			item.setEmail(dialogBinding.etEmail.getText().toString().trim());
			item.setFunktion(dialogBinding.etFunktion.getText().toString().trim());
			
			
			if(item != null){
				callback.onDecision(true);
				callback.onKontakt(item);
				Toast.makeText(context, "Kontakt aktualisiert", Toast.LENGTH_SHORT).show();
				}
		});
		
		builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public static Kontakt newItem(Kontakt item){
		return item;
	}
}