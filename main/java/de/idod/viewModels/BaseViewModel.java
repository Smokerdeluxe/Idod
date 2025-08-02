package de.gajd.idod.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.gajd.idod.repositories.BaseRepository;
import java.util.HashMap;

public abstract class BaseViewModel<T> extends ViewModel {
	protected final BaseRepository<T> repository;
	private T selectedItem;
	private String selectedKey;
	
	public BaseViewModel(BaseRepository<T> repository) {
		this.repository = repository;
	}
	
	public LiveData<HashMap<String, T>> getItems() {
		return repository.getAll();
	}
	
	public void add(T item) {
		repository.add(item);
	}
	
	public void save(String key, T item) {
		repository.save(key, item);
	}
	
	public void delete(String key) {
		repository.delete(key);
	}
	
	public void setSelectedItem(T item) {
		this.selectedItem = item;
		//this.selectedKey = key;
	}
	
	public T getSelectedItem() {
		return selectedItem;
	}
	
	public void setSelectedKey(String key) {
		this.selectedKey = key;
	}
	
	public String getSelectedKey() {
		return selectedKey;
	}
}