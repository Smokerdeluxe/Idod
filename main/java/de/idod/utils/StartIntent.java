package de.gajd.idod.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class StartIntent {
	public static void call(Context context, String nummer){
		Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nummer));
			context.startActivity(callIntent);
	}
	
	public static void whatsApp(Context context, String nummer){
		Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + nummer));
			context.startActivity(whatsappIntent);
	}
}