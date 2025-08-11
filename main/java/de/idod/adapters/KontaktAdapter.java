package de.gajd.idod.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.gajd.idod.databinding.ItemCardviewBinding;
import de.gajd.idod.models.Kontakt;
import de.gajd.idod.utils.StartIntent;

public class KontaktAdapter extends BaseAdapter<Kontakt, ItemCardviewBinding> {

	private final OnActionListener<Kontakt> listener;

	public interface OnActionListener<T> {
		void onEdit(T item, String key);
		void onDelete(T item, String key);
	}

	public KontaktAdapter(OnActionListener<Kontakt> listener) {
		this.listener = listener;
	}

	@Override
	protected ItemCardviewBinding inflateBinding(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return ItemCardviewBinding.inflate(inflater, parent, false);
	}

	@Override
	protected ViewHolder<Kontakt, ItemCardviewBinding> createViewHolder(ItemCardviewBinding binding) {
		return new ViewHolder<Kontakt, ItemCardviewBinding>(binding) {

			private boolean details = false;

			@Override
			public void bind(Kontakt item, String key) {	
				binding.tvName.setText(item.getName());
				binding.tvNummer.setText(item.getNummer());
				binding.tvEmail.setText(item.getEmail());
				binding.tvFunktion.setText(item.getFunktion());
				binding.getRoot().setOnClickListener(v -> toggleDetails());
				binding.btnAnruf.setOnClickListener(v -> StartIntent.call(v.getContext(), item.getNummer()));
				binding.btnWhatsApp.setOnClickListener(v -> StartIntent.whatsApp(v.getContext(), item.getNummer()));
				binding.btnDelete.setOnClickListener(v -> listener.onDelete(item, key));
				binding.btnEdit.setOnClickListener(v -> listener.onEdit(item, key));
			}

			private void toggleDetails() {
				details = !details;
				setDetailVisibility();
			}

			private void setDetailVisibility() {
				binding.tvNummer.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.tvEmail.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.btnEdit.setVisibility(details ? View.VISIBLE : View.GONE);
				binding.btnDelete.setVisibility(details ? View.VISIBLE : View.GONE);
			}
		};
	}
}