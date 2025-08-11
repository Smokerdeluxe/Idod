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
import de.gajd.idod.adapters.KontaktAdapter;
import de.gajd.idod.databinding.ListRecyclerviewBinding;
import de.gajd.idod.databinding.DialogUserEditBinding;
import de.gajd.idod.utils.Dialog;
import de.gajd.idod.models.Kontakt;
import de.gajd.idod.utils.Kontakte;
import de.gajd.idod.viewModels.KontaktViewModel;
import de.gajd.idod.R;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KontaktList extends Fragment implements KontaktAdapter.OnActionListener<Kontakt> {
	
	private KontaktViewModel viewModel;
	private KontaktAdapter adapter;
	private ListRecyclerviewBinding binding;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		binding = ListRecyclerviewBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new KontaktAdapter(this);
        binding.recyclerView.setAdapter(adapter);
		viewModel = new ViewModelProvider(requireActivity()).get(KontaktViewModel.class);
		viewModel.getItems().observe(getViewLifecycleOwner(), itemList -> {
			adapter.setItems(itemList);
			setupChips(itemList);
		});
		binding.fabAdd.setOnClickListener(v -> onEdit(null, null));
		return binding.getRoot();
	}
	
	@Override
	public void onEdit(Kontakt item, String key) {
		Dialog.editKontakt(binding.getRoot(), item, confirmed -> {
			if (confirmed){
				if(key != null){
					viewModel.save(key, item);
					Toast.makeText(requireContext(), "Kontakt aktualisiert", Toast.LENGTH_SHORT).show();
				}else{
					viewModel.add(item);
					Toast.makeText(requireContext(), "Kontakt hinugefügt", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	public void onDelete(Kontakt item, String key) {
		Dialog.jaNein(requireContext(), "Kontakt löschen?", item.getName() + " wirklich aus den Kontakten löschen?", confirmed -> {
			if(confirmed){
				viewModel.delete(key);
			}
		});
	}
	
	private void setupChips(HashMap<String, Kontakt> itemList) {
		Set<String> uniqueFunctions = new HashSet<>();
		itemList.forEach((key, item) -> uniqueFunctions.add(item.getFunktion().split(" ")[0]));
		binding.chipGroup.removeAllViews();	
		// Für jede eindeutige Funktion einen Chip erstellen und zur ChipGroup hinzufügen
		for (String funktion : uniqueFunctions) {
			Chip chip = new Chip(requireContext());
			chip.setText(funktion);
			chip.setCheckable(true);
			binding.chipGroup.addView(chip);
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
		HashMap<String, Kontakt> filteredList = new HashMap<>();
		viewModel.getItems().getValue().forEach((key, item) -> {
			if (funktion.equals(item.getFunktion().split(" ")[0])) {
				filteredList.put(key, item);
			}
		});
		adapter.setItems(filteredList);
	}

}