package de.gajd.idod.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseRepository<T> {
	private final DatabaseReference databaseReference;
	private final Class<T> modelClass;
	private final HashMap<String, T> cache = new HashMap<>();
	private final MutableLiveData<HashMap<String, T>> dataMap = new MutableLiveData<>(new HashMap<>());
	
	public BaseRepository(String path, Class<T> modelClass) {
		this.databaseReference = FirebaseDatabase.getInstance().getReference(path);
		this.modelClass = modelClass;
		databaseReference.keepSynced(true);
		loadData();
	}
	
	public void add(T item) {
		String key = databaseReference.push().getKey();
		if (key != null) {
			databaseReference.child(key).setValue(item);
			cache.put(key, item);
		}
	}
	
	public void save(String key, T item) {
		databaseReference.child(key).setValue(item);
		cache.put(key, item);
	}
	
	public void delete(String key) {
		databaseReference.child(key).removeValue();
		cache.remove(key);
	}
	
	public T getFromCache(String key) {
		return cache.get(key);
	}
	
	public LiveData<HashMap<String, T>> getAll() {
		return dataMap;
	}
	
	public void loadData() {
		databaseReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				for (DataSnapshot data : snapshot.getChildren()) {
					String key = data.getKey();
					T item = data.getValue(modelClass);
					if (item != null) {
						cache.put(key, item);
					}
				}
				dataMap.setValue(new HashMap<>(cache));
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e("Firebase", "Fehler beim Laden der Daten", error.toException());
			}
		});
	}
}