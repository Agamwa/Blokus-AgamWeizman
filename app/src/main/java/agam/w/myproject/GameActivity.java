package agam.w.myproject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;

public class GameActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    MaterialToolbar tbMain;
    NavigationView navigationView;
    FrameLayout flMain;
    FirebaseAuth auth;

    Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        // Getting the current user's email and parsing the username from it
        String s = auth.getCurrentUser().getEmail();
        int etPos = s.indexOf('@');
        String s2 = s.substring(0,etPos);
        setContentView(R.layout.activity_game);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        flMain = findViewById(R.id.frame_container);
        tbMain = findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);
        // Starting music service to play background music
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction("PLAY");
        startService(intent);
        // Setting up the navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                tbMain,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // Displaying the username in the navigation drawer header
        TextView tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvUsername.setText("Hello "+s2+"!");
        // Handling navigation item selections
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_choose) {
                    replaceFragment(new ChooseFragment());
                } else if (id == R.id.nav_game_history) {
                    replaceFragment(new LeaderBoardFragment());
                } else if (id == R.id.nav_rules) {
                    replaceFragment(new RulesFragment());
                } else if (id==R.id.nav_tips) {
                    replaceFragment(new TipsFragment());
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
                    Intent intent = new Intent(GameActivity.this, MusicService.class);
                    intent.setAction("STOP");
                    startService(intent);
                    Intent intent1 = new Intent(GameActivity.this, HomeActivity.class);
                    startActivity(intent1);

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

       scheduleDailyNotification();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void scheduleDailyNotification() {
        Intent intent = new Intent(this, MyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // for testing purposes
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond() + 5;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 13); //(13 AM)
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour); //(13 AM)
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        // If the time is in the past, set it for the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
                );
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    public void showWinnerDialog(String winner) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Game Over")
                .setMessage(winner)
                .setPositiveButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton("Restart", (dialog, which) -> {
                    currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                    ((BoardFragment) currentFragment).init();
                    dialog.dismiss();
                })
                .show();
    }
}

