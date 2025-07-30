package de.gajd.idod.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.gajd.idod.databinding.ItemCardviewBinding;
import de.gajd.idod.methods.Utils;
import de.gajd.idod.models.Mitarbeiter;

public class MitarbeiterAdapter extends BaseAdapter<Mitarbeiter, ItemCardviewBinding> {
	
	private final OnActionListener<Mitarbeiter> listener;
	
	public interface OnActionListener<T> {
		void onEdit(T item, String key);
		void onDelete(T item, String key);
	}
	
	public MitarbeiterAdapter(OnActionListener<Mitarbeiter> listener) {
		this.listener = listener;
	}
	
	@Override
	protected ItemCardviewBinding inflateBinding(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return ItemCardviewBinding.inflate(inflater, parent, false);
	}
	
	@Override
	protected ViewHolder<Mitarbeiter, ItemCardviewBinding> createViewHolder(ItemCardviewBinding binding) {
		return new ViewHolder<Mitarbeiter, ItemCardviewBinding>(binding) {
			
			private boolean details = false;
			
			@Override
			public void bind(Mitarbeiter item, String key) {
				binding.tvName.setText(item.getName());
				binding.tvNummer.setText(item.getPhoneNumber());
				binding.tvFunktion.setText(item.getFunktion());
				binding.tvEmail.setText(item.getEmail());
				binding.tvStatus.setText(item.getStatus());
				binding.tvProfi.setText(item.getProfi());		
				binding.getRoot().setOnClickListener(v -> toggleDetails());			
				binding.btnAnruf.setOnClickListener(v -> Utils.startCall(v.getContext(), item.getPhoneNumber()));
				binding.btnWhatsApp.setOnClickListener(v -> Utils.startWhatsApp(v.getContext(), item.getPhoneNumber()));
				binding.btnEdit.setOnClickListener(v -> listener.onEdit(item, key));
				binding.btnDelete.setOnClickListener(v -> listener.onDelete(item, key));
			}
			
			private void toggleDetails() {
				details = !details;
				setDetailVisibility();
			}
			
			private void setDetailVisibility() {
				binding.tvNummer.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.tvEmail.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.tvStatus.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.tvProfi.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.btnEdit.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.btnDelete.setVisibility(details ? View.VISIBLE : View.GONE);
			}
		};
	}
}