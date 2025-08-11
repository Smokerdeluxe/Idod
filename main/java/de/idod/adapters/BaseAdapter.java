package de.gajd.idod.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import de.gajd.idod.interfaces.Identifiable;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

public abstract class BaseAdapter<T extends Identifiable<T>, B extends ViewBinding>
extends RecyclerView.Adapter<BaseAdapter.ViewHolder<T, B>> {
	
	private final HashMap<String, T> items = new HashMap<>();
	
	public void setItems(HashMap<String, T> newItems) {
		DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback<>(items, newItems));
		items.clear();
		items.putAll(newItems);
		diffResult.dispatchUpdatesTo(this);
	}
	
	@NonNull
	@Override
	public ViewHolder<T, B> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		B binding = inflateBinding(parent);
		return createViewHolder(binding);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder<T, B> holder, int position) {
		String key = new ArrayList<>(items.keySet()).get(position);
		holder.bind(items.get(key), key);
	}
	
	@Override
	public int getItemCount() {
		return items.size();
	}
	
	protected abstract B inflateBinding(ViewGroup parent);
	
	protected abstract ViewHolder<T, B> createViewHolder(B binding);
	
	public abstract static class ViewHolder<T, B extends ViewBinding> extends RecyclerView.ViewHolder {
		protected final B binding;
		
		public ViewHolder(@NonNull B binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
		
		public abstract void bind(T item, String key);
	}
}