package agam.w.myproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    Button btnSignUp;
    TextInputLayout emailAddressTi;
    TextInputLayout passwordTi;
    TextInputLayout firstNameTi;
    TextInputLayout lastNameTi;
    TextInputLayout usernameTi;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailAddressTi = findViewById(R.id.tiSignUpEmail);
        passwordTi = findViewById(R.id.tiSignUpPassword);
        firstNameTi = findViewById(R.id.tiSignUpName);
        lastNameTi = findViewById(R.id.tiSignUpLastName);
        usernameTi = findViewById(R.id.tiSignUpUsername);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> {
            String email = emailAddressTi.getEditText().getText().toString();
            String password = passwordTi.getEditText().getText().toString();
            // Collect user input and validate required fields
            if(email.isEmpty() || password.isEmpty()) {
                // Show error if required fields are empty
                Toast.makeText(SignUpActivity.this, "email and/or password can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                // add display name to user
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            try {
                                FirebaseUser user = task.getResult().getUser();
                                String firstName = firstNameTi.getEditText().getText().toString();
                                String lastName = lastNameTi.getEditText().getText().toString();
                                String username = usernameTi.getEditText().getText().toString();
                                Map<String, Object> data = new HashMap<>();
                                data.put("id", user.getUid());
                                data.put("username",username );
                                data.put("firstName", firstName);
                                data.put("lastName", lastName);
                                data.put("email", email);
                                data.put("wins", "0");
                                // Save the user data into the Firestore "Users" collection
                                db
                                    .collection("Users")
                                    .document(user.getUid())
                                    .set(data)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, GameActivity.class);
                                            intent.putExtra("username", usernameTi.getEditText().getText().toString());
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.d("TAG1", "onFailure: " + task1.getException().getMessage());
                                            Toast.makeText(SignUpActivity.this, "Saving Document Falied!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                // Start SignInActivity and pass the username to it
                            } catch (Exception e) {
                                Log.d("TAG2", "onFailure: " + e.getMessage());
                                Toast.makeText(SignUpActivity.this, "Saving Document Falied!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("TAG3", "onFailure: " + task.getException().getMessage());
                            Toast.makeText(SignUpActivity.this, "Registration failed!!" + " Please try again later", Toast.LENGTH_SHORT).show();
                        }
                });

            }

        });
    }
}