package de.gajd.idod.utils;

import android.content.Context;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;


public class InfoUI {
	public static void toast(Context context, String nachricht){
		Toast.makeText(context, nachricht, Toast.LENGTH_LONG).show();
	}
	
	public void bar(){
		
	}
}