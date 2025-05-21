package agam.w.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WaitActivity extends AppCompatActivity {

    ImageView countDownIm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wait);

        countDownIm = findViewById(R.id.imageViewNumberCount);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownIm.setImageResource(R.drawable.two);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countDownIm.setImageResource(R.drawable.one);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(WaitActivity.this, GameActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }
                },2000);
            }
        },2000);
    }
}