package de.gajd.idod.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import de.gajd.idod.adapters.MitarbeiterAdapter;
import de.gajd.idod.databinding.DialogUserEditBinding;
import de.gajd.idod.databinding.ListRecyclerviewBinding;
import de.gajd.idod.utils.Dialog;
import de.gajd.idod.models.Mitarbeiter;
import de.gajd.idod.viewModels.MitarbeiterViewModel;
import de.gajd.idod.R;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MitarbeiterList extends Fragment implements MitarbeiterAdapter.OnActionListener<Mitarbeiter> {

	private MitarbeiterViewModel viewModel;
	private MitarbeiterAdapter adapter;
	private ListRecyclerviewBinding binding;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		binding = ListRecyclerviewBinding.inflate(inflater, container, false);
		binding.fabAdd.setOnClickListener(v -> showEditDialog(null, null));
		binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
		adapter = new MitarbeiterAdapter(this);
		binding.recyclerView.setAdapter(adapter);
		viewModel = new ViewModelProvider(requireActivity()).get(MitarbeiterViewModel.class);
		viewModel.getItems().observe(getViewLifecycleOwner(), itemList -> {
			adapter.setItems(itemList);
			setupChips(itemList);
		});
		return binding.getRoot();
	}

	@Override
	public void onEdit(Mitarbeiter item, String key) {
		showEditDialog(item, key);
	}

	@Override
	public void onDelete(Mitarbeiter item, String key) {
		Dialog.jaNein(requireContext(),"Kontakt löschen", "Kontakt wirklich löschen?", confirmed -> {
			if (confirmed) {
				viewModel.delete(key);
			} else {
				Toast.makeText(requireContext(), "Aktion abgebrochen", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showEditDialog(Mitarbeiter item, String key) {
		AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
		DialogUserEditBinding dialogBinding = DialogUserEditBinding.inflate(getLayoutInflater());
		builder.setView(dialogBinding.getRoot());

		if (item != null) {
			dialogBinding.etName.setText(item.getName());
			dialogBinding.etPhone.setText(item.getPhoneNumber());
			dialogBinding.etEmail.setText(item.getEmail());
			dialogBinding.etFunktion.setText(item.getFunktion());
			dialogBinding.etStatus.setText(item.getStatus());
			dialogBinding.etProfi.setText(item.getProfi());
			builder.setTitle("Mitarbeiter bearbeiten");
		} else {
			builder.setTitle("Mitarbeiter erstellen");
		}
		builder.setPositiveButton("Speichern", (dialog, which) -> {
			Mitarbeiter newItem = new Mitarbeiter(null, dialogBinding.etName.getText().toString().trim(),
					dialogBinding.etPhone.getText().toString().trim(),
					dialogBinding.etEmail.getText().toString().trim(),
					dialogBinding.etFunktion.getText().toString().trim(),
					dialogBinding.etStatus.getText().toString().trim(),
					dialogBinding.etProfi.getText().toString().trim());
			if (item != null) {
				viewModel.save(key, newItem);
				Toast.makeText(requireContext(), "Mitarbeiter aktualisiert", Toast.LENGTH_SHORT).show();
			} else {
				viewModel.add(newItem);
				Toast.makeText(requireContext(), "Mitarbeiter hinugefügt", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	// Diese Methode wird aufgerufen, um die Chips dynamisch zu erstellen.	
	private void setupChips(HashMap<String, Mitarbeiter> itemList) {
		Set<String> uniqueFunctions = new HashSet<>();
		itemList.forEach((key, item) -> uniqueFunctions.add(item.getFunktion().split(" ")[0]));
		// ChipGroup in der XML-Datei finden
		//ChipGroup chipGroup = getView().findViewById(R.id.chipGroup);
		binding.chipGroup.removeAllViews(); // Vorherige Chips entfernen, falls vorhanden
	//	chipGroup.removeAllViews();
		// Für jede eindeutige Funktion einen Chip erstellen und zur ChipGroup hinzufügen
		for (String funktion : uniqueFunctions) {
			Chip chip = new Chip(requireContext());
			chip.setText(funktion);
			chip.setCheckable(true);
			binding.chipGroup.addView(chip);
			//chipGroup.addView(chip);
		}
		// Setzt das Verhalten, wenn der Chip ausgewählt oder abgewählt wird
		binding.chipGroup.setOnCheckedStateChangeListener((group, checkedId) -> {
			if (checkedId.isEmpty()) {
				adapter.setItems(itemList);
			} else {
				Chip selectedChip = group.findViewById(checkedId.get(0));
				String selectedFunktion = selectedChip.getText().toString();
	
				filterContactsByFunction(selectedFunktion);
			}
		});
	}

	// Diese Methode filtert die Kontakte basierend auf der ausgewählten Funktion
	private void filterContactsByFunction(String funktion) {
		// Nur Kontakte mit der ausgewählten Funktion anzeigen
		HashMap<String, Mitarbeiter> filteredList = new HashMap<>();
		viewModel.getItems().getValue().forEach((key, item) -> {
			if (funktion.equals(item.getFunktion().split(" ")[0])) {
				filteredList.put(key, item);
			}
		});
		adapter.setItems(filteredList);
	}

}