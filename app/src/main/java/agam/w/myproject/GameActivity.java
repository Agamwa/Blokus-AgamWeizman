package agam.w.myproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class GameActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    MaterialToolbar tbMain;
    NavigationView navigationView;
    FrameLayout flMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        flMain = findViewById(R.id.frame_container);
        tbMain = findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction("PLAY");
        startService(intent);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                tbMain,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_choose) {
                    replaceFragment(new ChooseFragment());
                } else if (id == R.id.nav_game_history) {
                    replaceFragment(new GameHistoryFragment());
                } else if (id == R.id.nav_rules) {
                    replaceFragment(new RulesFragment());
                } else if (id == R.id.nav_volume) {
                    if(item.getTitle().toString().equals("Turn on music")){
                        Intent intent = new Intent(getApplicationContext(), MusicService.class);
                        intent.setAction("PLAY");
                        startService(intent);
                        item.setIcon(R.drawable.ic_volume_off);
                        item.setTitle("Turn off music");
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), MusicService.class);
                        intent.setAction("STOP");
                        startService(intent);
                        item.setIcon(R.drawable.ic_volume_up);
                        item.setTitle("Turn on music");
                        }
                } else if (id == R.id.nav_board) {
                    replaceFragment(new BoardFragment());
                } else if (id == R.id.nav_logout) {

                }


                drawerLayout.closeDrawers();
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackInvokedCallback callback = new OnBackInvokedCallback() {
                @Override
                public void onBackInvoked() {
                    if (drawerLayout.isDrawerOpen(navigationView)) {
                        drawerLayout.closeDrawer(navigationView);
                    } else {
                        finish();
                    }
                }
            };

            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, callback);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    }

