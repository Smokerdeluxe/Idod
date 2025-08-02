package de.gajd.idod;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class DataPersistence extends Application{
	@Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}
}