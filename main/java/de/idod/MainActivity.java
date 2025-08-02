package de.gajd.idod;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
//import de.gajd.idod.fragments.KontaktAdd;
import de.gajd.idod.databinding.ActivityMainBinding;
import de.gajd.idod.fragments.KontaktList;
import de.gajd.idod.fragments.MitarbeiterList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	
	private DrawerLayout drawerLayout;
	private FirebaseAuth mAuth;
	private ActivityMainBinding binding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//se
		
		mAuth = FirebaseAuth.getInstance();
		// Falls Nutzer nicht eingeloggt ist, zurück zur Login-Seite
		if (mAuth.getCurrentUser() == null) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		}
		
		drawerLayout = binding.drawerLayout;
		NavigationView navView = binding.navView;
		navView.setNavigationItemSelectedListener(this);
		Toolbar toolbar = binding.toolbar;
		setSupportActionBar(toolbar);
		
		// ActionBar-Drawer-Toggle (Hamburger-Icon)
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
		this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		
		// Standard-Fragment laden
		if (savedInstanceState == null) {
			showFragment(new KontaktList());
			navView.setCheckedItem(R.id.nav_1);
		}
	}
	
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_1:
			showFragment(new KontaktList());
			break;
			case R.id.nav_2:
			showFragment(new MitarbeiterList());
			break;
			case R.id.nav_3:
			showFragment(new MitarbeiterList());
			break;
			case R.id.nav_5:
			startActivity(new Intent(this, EditProfilActivity.class));
			break;
			case R.id.nav_6:
			mAuth.signOut();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return true;
		}
		
		drawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
			} else {
			super.onBackPressed();
		}
	}
	
	private void showFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
	}
	
}