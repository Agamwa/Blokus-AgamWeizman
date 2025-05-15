package agam.w.myproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;

    private final int duration = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Load logo and apply animation
        logo = findViewById(R.id.imageView2);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_anim);
        animation.setDuration(duration);
        logo.setAnimation(animation);
        // Play opening sound
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.oppening_app);
        mediaPlayer.start();

        // After 8 seconds, move to the HomeActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        },duration);

    }
}