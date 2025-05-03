package agam.w.myproject;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class HomeActivity extends AppCompatActivity {
Button btnSignIn, btnSignUp;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    btnSignIn = findViewById(R.id.btnSignIn);
    btnSignUp = findViewById(R.id.btnSignUp);

    // Initially hide the buttons
    btnSignIn.setVisibility(INVISIBLE);
    btnSignUp.setVisibility(INVISIBLE);
    btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When Sign In is clicked, navigate to SignInActivity
                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    btnSignUp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Sign Up is clicked, navigate to SignUpActivity
            Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    });
    // Create a handler to delay the appearance of buttons
    Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            // After 2 seconds, make both buttons visible
            btnSignIn.setVisibility(VISIBLE);
            btnSignUp.setVisibility(VISIBLE);
        }
    }, 2000);// Delay in milliseconds (2000 = 2 seconds)
    }
}