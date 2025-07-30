package de.gajd.idod.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import de.gajd.idod.interfaces.Identifiable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffCallback<T extends Identifiable<T>> extends DiffUtil.Callback {
	private final List<Map.Entry<String, T>> oldList;
	private final List<Map.Entry<String, T>> newList;
	
	public DiffCallback(HashMap<String, T> oldMap, HashMap<String, T> newMap) {
		this.oldList = new ArrayList<>(oldMap.entrySet());
		this.newList = new ArrayList<>(newMap.entrySet());
	}
	
	@Override
	public int getOldListSize() {
		return oldList.size();
	}
	
	@Override
	public int getNewListSize() {
		return newList.size();
	}
	
	@Override
	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
		return oldList.get(oldItemPosition).getKey().equals(newList.get(newItemPosition).getKey());
	}
	
	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
		return oldList.get(oldItemPosition).getValue().isSameContent(newList.get(newItemPosition).getValue());
	}
	
	@Nullable
	@Override
	public Object getChangePayload(int oldItemPosition, int newItemPosition) {
		return oldList.get(oldItemPosition).getValue().getChangePayload(newList.get(newItemPosition).getValue());
	}
}