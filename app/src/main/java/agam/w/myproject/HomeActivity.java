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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
Button btnSignIn, btnSignUp, btnStart, btnLogout;
TextView tvHelloMsg;
FirebaseAuth mAuth;
FirebaseUser currentUser;
FirebaseFirestore db;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvHelloMsg = findViewById(R.id.tvHelloMsg);
        btnStart = findViewById(R.id.btnStart);
        btnLogout = findViewById(R.id.btnLogout);

        // Initially hide the buttons
        tvHelloMsg.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.GONE);
    // Sign In button click - navigate to SignInActivity
        btnSignIn.setOnClickListener(v -> {
            // When Sign In is clicked, navigate to SignInActivity
            Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    // Sign Up button click - navigate to SignUpActivity
        btnSignUp.setOnClickListener(v -> {
            // When Sign Up is clicked, navigate to SignUpActivity
            Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
// Start Game button click - navigate to GameActivity
        btnStart.setOnClickListener(v -> {
            // When Start is clicked, navigate to GameActivity
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            startActivity(intent);
        });
    // Logout button click - sign the user out and reset UI
        btnLogout.setOnClickListener(v -> {
            // When Logout is clicked, sign out the user
            mAuth.signOut();
            currentUser = null;
            tvHelloMsg.setText("Hello Guest!");
            tvHelloMsg.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnSignIn.setVisibility(VISIBLE);
            btnSignUp.setVisibility(VISIBLE);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get current user (in case it changed while app was paused)
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is logged in - show Sign In and Sign Up buttons
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);
            tvHelloMsg.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            // User is logged in - fetch their first name from Firestore
        } else {
            db.collection("Users").document(currentUser.getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists())
                                    tvHelloMsg.setText("Hello " + documentSnapshot.get("firstName").toString() + "!");
                            }).addOnFailureListener(e -> {
                        // If failed to fetch name, use email instead
                                tvHelloMsg.setText("Hello " + currentUser.getEmail() + "!");
                            });
            // Show personalized UI
            tvHelloMsg.setVisibility(VISIBLE);
            btnStart.setVisibility(VISIBLE);
            btnLogout.setVisibility(VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);
        }

    }
}