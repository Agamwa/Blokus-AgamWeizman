package agam.w.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signUpActivity extends AppCompatActivity {
    Button btnSignUp;
    TextInputLayout emailAddressEt;
    TextInputLayout passwordEt;
    
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        emailAddressEt = findViewById(R.id.tiSignUpEmail);
        passwordEt = findViewById(R.id.tiSignUpPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {

                String email = emailAddressEt.getEditText().getText().toString();
                String password = passwordEt.getEditText().getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(signUpActivity.this, "email and/or password can't be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(signUpActivity.this, GameActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(signUpActivity.this, "invalid email and/or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                   
                }
                
            }
        });


    }
}